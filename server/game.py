# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game
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

    result = dao.create_user(username, password, firstname, lastname)
    response = {"status": "success"} if result else {"status": "failure"}

    return jsonify(response)


@app.route(rest_prefix+'/game/'+'<game_ID>', methods=["POST"])
def create_game(game_ID):
    username = request.form['username']
    password = request.form['password']
    game_name = request.form['game_name']
    description = request.form['description']

    result = dao.create_game(username, game_name, description)
    response = {"status": "failure", "results": {"game_id": 0}} if result is None else {"status": "success", "results": {"game_id": result}}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>')
def leave_game(game_ID):
    username = request.form['username']
    game_id = request.form['game_id']
    dao.leave_game(game_id)
    result = dao.delete_game(username, game_id)
    response = {"status": "success"} if result else {"status": "failure"}
    return jsonify(response)


if __name__ == "__main__":
    app.run(debug=True)
