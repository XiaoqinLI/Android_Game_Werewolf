# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game
# the question I have:
  # gameid on create a game
  # post and put share same url (so far, put is 'game1')
  # write method get to show returned results on browser
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
def create_user():
    username = request.form['username']
    password = request.form['password']
    firstname = request.form['firstname']
    lastname = request.form['lastname']
    if len(password)<6:
        response = {"status": "failure"}
        return jsonify(response)
    else:
        result = dao.create_user(username, password, firstname, lastname)
        response = {"status": "success"} if result else {"status": "failure"}
        return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["POST"])
def create_game(game_ID):
    username = request.form['username']
    password = request.form['password']
    game_name = request.form['game_name']
    description = request.form['description']
    #
    result = dao.create_game(username, game_name, description)
    response = {"status": "failure", "results": {"game_id": 0}} if result is None else {"status": "success", "results": {"game_id": result}}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["DELETE"])
def leave_game(game_ID):
    username = request.form['username']
    game_id = request.form['game_id']
    #
    dao.leave_game(game_id)
    result = dao.delete_game(username, game_id)
    response = {"status": "success"} if result else {"status": "failure"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/lobby', methods=["POST"])
def join_game(game_ID):
    username = request.form['username']
    game_id = request.form['game_id']
    gameinfo =  dao.game_info(game_id)
    if gameinfo == None:
        response = {"status": "failure"}
        return jsonify(response)
    if gameinfo["status"] != 0:  # 0: not stared, 1: started, 2: ended
        response = {"status": "failure"}
        return jsonify(response)
    else:
        result = dao.join_game(username, game_id)
        response = {"status": "success"} if result else {"status": "failure"}
        return jsonify(response)

@app.route(rest_prefix+'/game1/'+'<game_ID>', methods=["PUT"])
def update_game(game_ID):
    print 'aaaaaaaaa'
    username = request.form['username']
    game_id = request.form['game_id']
    lat = request.form['lat']
    lng = request.form['lng']
    radius = request.form['radius']
    #
    dao.set_location(username, lat, lng)

    result = dao.get_alive_nearby(username, game_id, radius)

    response = {'status': 'success', 'results': {'werewolfscent': []}}
    for entry in result:
        if entry['distance'] < radius:
            response['results']['werewolfscent'].append({'player_id': entry['player_id'], 'distance': entry['distance']})
    return jsonify(response)





if __name__ == "__main__":
    app.run(debug=True)
