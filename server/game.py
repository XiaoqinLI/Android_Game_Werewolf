# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game
from flask import Flask, request, jsonify
import psycopg2
from wherewolfdao import WherewolfDao, BadArgumentsException, NoUserExistsException, UserAlreadyExistsException

app = Flask(__name__)

rest_prefix = "/v1"

dao = WherewolfDao()
# def get_db(databasename='tasklist',
#            username='postgres',
#            password='121314'):
#     return psycopg2.connect(database= databasename,
#              user=username, password=password)

@app.route("/")
def welcome():
    return "welcome to wherewolf game REST api"

@app.route(rest_prefix+'/register')
def get_user():
    return "aaa"

@app.route(rest_prefix+'/register', methods=["POST"])
def create_user():
    # pass
    # conn = WherewolfDao.wherewolf().get_db()
    username = request.form['username']
    password = request.form['password']
    firstname = request.form['firstname']
    lastname = request.form['lastname']
    result = dao.create_user(username, password, firstname, lastname)
    response = {"status": "success"} if result else {"status": "failure"}
    return jsonify(response)
    # sqlquery = ('select username from taskuser '
    #             'where username=%s')
    # sql = ('insert into taskuser (username, password) '
    #     'values (%s, %s)')
    # cur = conn.cursor()
    # cur.execute(sqlquery, (username,))
    # if not cur.fetchone():
    #     cur.execute(sql, (username, password))
    #     conn.commit()
    #     print 'created a user called {}'.format(username)
    #     response["status"] = "success"
    #     response["username"] = username
        # MIME application/json

# def create_game(username, password, game_name, description)

@app.route(rest_prefix+'/game', methods=["POST"])
def create_game():
    username = request.form['username']
    password = request.form['password']
    game_name = request.form['game_name']
    description = request.form['description']
    result = dao.create_game(username, game_name, description)

    response = {"status": "failure", "game_id": 0} if result is None else {"status": "success", "game_id": result}
    return jsonify(response)

if __name__ == "__main__":
    app.run(debug=True)
