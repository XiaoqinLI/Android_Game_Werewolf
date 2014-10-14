# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game
# the questions I have:
    # how to show get_game_info in the browser???


  # Basic Game Logic
  # The WhereWolf game has two game states, a day and a night cycle. However, in terms of event-based handling,
  #  there is a daybreak and there is a nightfall. Create two functions that can be called.

from flask import Flask, request, jsonify
import psycopg2
from wherewolfdao import WherewolfDao, BadArgumentsException, NoUserExistsException, UserAlreadyExistsException
import datetime

app = Flask(__name__)

rest_prefix = "/v1"

dao = WherewolfDao()

@app.route("/")
def welcome():
    return "welcome to wherewolf game REST api"

@app.route(rest_prefix+'/register', methods=["POST"])
def create_user():  # Done
    username = request.form['username']
    password = request.form['password']
    firstname = request.form['firstname']
    lastname = request.form['lastname']
    if len(password)<6:
        response = {"status": "failure"}
        return jsonify(response)
    else:
        try:
            result = dao.create_user(username, password, firstname, lastname)
            response = {"status": "success"} #if result else {"status": "failure"}
        except UserAlreadyExistsException:
            response = {"status": "failure"}
        finally:
            return jsonify(response)

@app.route(rest_prefix+'/game', methods=["POST"])
def create_game():  # Done
    username = request.form['username']
    password = request.form['password']
    game_name = request.form['game_name']
    description = request.form['description']
    try:
        auth_checker = dao.check_password(username, password)
        if auth_checker:  # if auth correct
            result = dao.create_game(username, game_name, description) # create game if user is not administering another game
            response = {"status": "success", "results": {"game_id": result}} if result else {"status": "failure", "results": {"game_id": 0}}
        else:
            response = {"status": "failure", "results": {"game_id": 0}}
    except NoUserExistsException:
        response = {"status": "failure", "results": {"game_id": 0}}
    finally:
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["DELETE"])
def leave_game(game_ID):   # Done
    username = request.form['username']
    game_id = request.form['game_id']
    password = request.form['password']
    try:
        auth_checker = dao.check_password(username, password)
        if auth_checker:
            dao.leave_game(game_id)
            result = dao.delete_game(username, game_id)
            response = {"status": "success"} if result else {"status": "failure"}
        else:
            response = {"status": "failure"}
    except NoUserExistsException:
        response = {"status": "failure"}
    finally:
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/lobby', methods=["POST"])
def join_game(game_ID):   # Done
    username = request.form['username']
    game_id = request.form['game_id']
    password = request.form['password']
    gameinfo =  dao.game_info(game_id)

    if gameinfo == None:     # game does not exist
        response = {"status": "failure"}
        return jsonify(response)
    elif gameinfo["status"] != 0:  # 0: not staretd, 1: started, 2: ended
        response = {"status": "failure"}
        return jsonify(response)
    else:
        try:
            auth_checker = dao.check_password(username, password)
            if auth_checker:
                result = dao.join_game(username, game_id)
                response = {"status": "success"} if result else {"status": "failure"}
            else:
                response = {"status": "failure"}
        except NoUserExistsException:
            response = {"status": "failure"}
        finally:
            return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["PUT"])
def update_game(game_ID):  # Hold
    username = request.form['username']
    game_id = request.form['game_id']
    lat = request.form['lat']
    lng = request.form['lng']
    RADIUS = 100000
    dao.set_location(username, lat, lng)
    # checking point of interest stuff
    # implement this later on.


    currentPlayer = dao.get_current_player(username)
    if currentPlayer['is_werewolf'] == 0:
        response = {'status': 'success'}
    else:
        result = dao.get_alive_nearby(username, game_id, RADIUS)
        response = {'status': 'success', 'results': {'werewolfscent': [{'player_id': entry['player_id'], 'distance': round(entry['distance'],2)} for entry in result]}}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>')
def get_game_info(game_ID):  # Done
    # return 'aaa'
    # daybreak, nightfall, game status, players
    # username = request.form['username']
    # password = request.form['password']
    game_id = request.form['game_id']
    players = dao.get_players(game_id)# player is a list
    gameInfo = dao.game_info(game_id)
    for key in gameInfo:
        if isinstance(gameInfo[key], datetime.time):
            gameInfo[key] = str(gameInfo[key])
    gameInfo['players'] = players
    return jsonify(gameInfo)
    # methods to track time in game. Admin sets the round to night

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/vote', methods=["POST"])
def cast_vote(game_ID):
    username = request.form['username']
    password = request.form['password']
    game_id = request.form['game_id']
    target_id = request.form['player_id']
    gameinfo =  dao.game_info(game_id)
    if gameinfo["currenttime"] > gameinfo['nightfall'] and gameinfo["currenttime"] < gameinfo['daybreak']: #voting during night
        response = {"status": "failure"}
        return jsonify(response)
    else:
        try:
            auth_checker = dao.check_password(username, password)
            if auth_checker:
                current_player = dao.get_current_player(username)
                current_gameid = dao.get_player_current_game_id(current_player["playerid"])
                if current_gameid == game_id:
                    response = {"status": "success"}
                    dao.vote(game_id, current_player["playerid"], target_id)
                else:  #not in game
                    response = {"status": "failure"}
            else:
                response = {"status": "failure"}
        except NoUserExistsException:  # bad auth
            response = {"status": "failure"}
        finally:
            return jsonify(response)


if __name__ == "__main__":
    app.run(debug=True)

