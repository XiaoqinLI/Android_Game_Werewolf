# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game
# the questions I have:
  # write method get to show returned results on browser

  # GET /game/{gameid} reports the (current information??) about the game, def game_info
    # daybreak night fall, game status, players
    # methods to track time in game. Admin sets the round to night

  # Basic Game Logic
  # The WhereWolf game has two game states, a day and a night cycle. However, in terms of event-based handling,
  #  there is a daybreak and there is a nightfall. Create two functions that can be called.

from flask import Flask, request, jsonify
import psycopg2
from wherewolfdao import WherewolfDao, BadArgumentsException, NoUserExistsException, UserAlreadyExistsException

app = Flask(__name__)

rest_prefix = "/v1"

dao = WherewolfDao()

@app.route("/")
def welcome():
    return "welcome to wherewolf game REST api"

# @app.route(rest_prefix+'/register')
# def get_user():
#     return "aaa"

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
def leave_game(game_ID):
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
def join_game(game_ID):
    username = request.form['username']
    game_id = request.form['game_id']
    gameinfo =  dao.game_info(game_id)
    if gameinfo == None:     # game does not exist
        response = {"status": "failure"}
        return jsonify(response)
    if gameinfo["status"] != 0:  # 0: not stared, 1: started, 2: ended
        response = {"status": "failure"}
        return jsonify(response)
    else:
        result = dao.join_game(username, game_id)
        response = {"status": "success"} if result else {"status": "failure"}
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["PUT"])
def update_game(game_ID):
    print 'aaaaaaaaa'
    username = request.form['username']
    game_id = request.form['game_id']
    lat = request.form['lat']
    lng = request.form['lng']
    radius = request.form['radius']
    #
    # checking point of interest stuff
    dao.set_location(username, lat, lng)
    # need to check the identity of the current player
    result = dao.get_alive_nearby(username, game_id, radius)

    response = {'status': 'success', 'results': {'werewolfscent': []}}
    for entry in result:
        if entry['distance'] < radius:
            response['results']['werewolfscent'].append({'player_id': entry['player_id'], 'distance': entry['distance']})
    return jsonify(response)





if __name__ == "__main__":
    app.run(debug=True)

