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

#
#-----------------APIs-----------------------------
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
        response = {"status": "failure(password too short-less than 6 characters)"}
        return jsonify(response)
    else:
        try:
            dao.create_user(username, password, firstname, lastname)
            response = {"status": "success"} #if result else {"status": "failure"}
        except UserAlreadyExistsException:
            response = {"status": "failure(user already exists with that username)"}
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
            response = {"status": "success", "results": {"game_id": result}} if result else {"status": "failure(already administering a game)", "results": {"game_id": 0}}
        else:
            response = {"status": "failure(bad aut)", "results": {"game_id": 0}}
    except NoUserExistsException:
        response = {"status": "failure(No such a user)", "results": {"game_id": 0}}
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
            response = {"status": "success"} if result else {"status": "failure(game does not exists)"}
        else:
            response = {"status": "failure(not admin of the game)"}
    except NoUserExistsException:
        response = {"status": "failure(No such a user)"}
    finally:
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/lobby', methods=["POST"])
def join_game(game_ID):   # Done
    username = request.form['username']
    game_id = request.form['game_id']
    password = request.form['password']
    gameinfo =  dao.game_info(game_id)

    if gameinfo == None:     # game does not exist
        response = {"status": "failure(game does not exist)"}
        return jsonify(response)
    elif gameinfo["status"] != 0:  # 0: not staretd, 1: started, 2: ended
        response = {"status": "failure(game not in the lobby mode (started or ended))"}
        return jsonify(response)
    else:
        try:
            auth_checker = dao.check_password(username, password)
            if auth_checker:
                result = dao.join_game(username, game_id)
                response = {"status": "success"} if result else {"status": "failure(already in another game)"}
            else:
                response = {"status": "failure(bad auth)"}
        except NoUserExistsException:
            response = {"status": "failure(No such a user)"}
        finally:
            return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["PUT"])
def update_game(game_ID):  # need to implement save zone
    username = request.form['username']
    game_id = request.form['game_id']
    lat = request.form['lat']
    lng = request.form['lng']
    RADIUS = 100000
    dao.set_location(username, lat, lng)
    player_id_location = dao.get_location(username)
    player_id = player_id_location['playerid']

    all_landmark = dao.get_landmark_nearby(lat, lng, game_id) # a list of receivable landmark.
    for entry in all_landmark:
        if entry['type'] == 0: # get in a save zone, # implement this later on.
            pass
        elif entry['type'] == 1:
            dao.add_treasure(player_id, entry['landmark_id'])
    currentPlayer = dao.get_current_player(username)
    if currentPlayer['is_werewolf'] == 0:
        response = {'status': 'success(you are not a werewolf)'}
    else:
        result = dao.get_alive_nearby(username, game_id, RADIUS)
        response = {'status': 'success', 'results': {'werewolfscent': [{'player_id': entry['player_id'], 'distance': round(entry['distance'],2)} for entry in result]}}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>')
def get_game_info(game_ID):  # Done
    game_id = request.form['game_id']
    players = dao.get_players(game_id)# player is a list
    gameInfo = dao.game_info(game_id)
    for key in gameInfo:
        if isinstance(gameInfo[key], datetime.time):
            gameInfo[key] = str(gameInfo[key])
    gameInfo['players'] = players
    return jsonify(gameInfo)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/vote', methods=["POST"])
def cast_vote(game_ID): # Done
    username = request.form['username']
    password = request.form['password']
    game_id = int(request.form['game_id'])
    target_id = request.form['player_id']
    gameinfo =  dao.game_info(game_id)
    if gameinfo['currenttime'] < gameinfo['nightfall'] and gameinfo['currenttime'] > gameinfo['daybreak']:
        try:
            auth_checker = dao.check_password(username, password)
            if auth_checker:
                current_player = dao.get_current_player(username)
                current_gameid = dao.get_player_current_game_id(current_player["playerid"])
                if current_gameid == game_id:
                    response = {"status": "success"}
                    dao.vote(game_id, current_player["playerid"], target_id)
                else:  #not in game
                    response = {"status": "failure(not in game)"}
            else:
                response = {"status": "failure(bad auth)"}
        except NoUserExistsException:  # bad auth
            response = {"status": "failure(No such a user)"}
        finally:
            return jsonify(response)
    else:
        response = {"status": "failure(voting during night)"}
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/vote', methods=["GET"])
def get_vote_stats(game_ID):
    username = request.form['username']
    password = request.form['password']
    game_id = int(request.form['game_id'])
    try:
        auth_checker = dao.check_password(username, password)
        if auth_checker:  # if auth correct
            current_player = dao.get_current_player(username)
            current_gameid = dao.get_player_current_game_id(current_player["playerid"])
            if current_gameid == game_id:
                result = dao.get_vote_stats(game_id)
                response = {"status": "success", "results": result}
            else:  #not in game
                response = {"status": "failure(not in game)"}
        else:
            response = {"status": "failure(bad auth)"}
    except NoUserExistsException:
        response = {"status": "failure(No such a user)"}
    finally:
        return jsonify(response)

if __name__ == "__main__":
    app.run(debug=True)

