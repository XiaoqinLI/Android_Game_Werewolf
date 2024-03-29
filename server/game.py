# __author__ = 'daybreaklee'
# game is the web rest service API for wherewolf game

from flask import Flask, request, jsonify
import psycopg2
from wherewolfdao import WherewolfDao, BadArgumentsException, NoUserExistsException, UserAlreadyExistsException

import datetime
import time
import random

app = Flask(__name__)

rest_prefix = "/v1"

dao = WherewolfDao()

#
#-----------------APIs-----------------------------
@app.route("/")
def welcome():
    return "welcome to wherewolf game REST api"

@app.route('/healthcheck')
def health_check():
    return "healthy"

@app.route(rest_prefix+'/checkpassword', methods=["POST"])
def check_password():
    username = request.form['username']
    password = request.form['password']
    try:
        auth_checker = dao.check_password(username, password)
        if auth_checker:  # if auth correct
            response = {"status": "success"}
        else:
            response = {"status": "failure(bad auth)"}
    except NoUserExistsException:
        response = {"status": "failure(No such a user)"}
    finally:
        return jsonify(response)

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

@app.route('/v1/games', methods=["GET"])
def get_games():
    db = WherewolfDao()
    response = {}
    try:
        allGames = db.get_games()
        response["games"] = []
        for i in allGames:
            row = {}
            row["game_id"] = i["game_id"]
            row["name"] = i["name"]
            row["admin_name"] = i["admin_name"]
            response["games"].append(row)
        response["status"] = "success"
    except Exception:
        response["status"] = "failure(could not retrieve data)"
    return jsonify(response)

@app.route('/v1/game/<game_ID>/players', methods=["POST"])
def get_players(game_ID):
    db = WherewolfDao()
    username = request.form['username']
    password = request.form['password']
    game_id = request.form['game_id']

    response = {}
    try:
        allplayers = db.get_player_names(game_id)
        response["players"] = []
        for i in allplayers:
            row = {}
            row["playername"] = i["playername"]
            row["playerid"] = i["playerid"]
            response["players"].append(row)

        response["status"] = "success"
    except Exception:
        response["status"] = "failure(could not retrieve players)"
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
            response = {"status": "failure(bad auth)", "results": {"game_id": 0}}
    except NoUserExistsException:
        response = {"status": "failure(No such a user)", "results": {"game_id": 0}}
    finally:
        return jsonify(response)

@app.route(rest_prefix+'/gamedel/'+'<game_ID>', methods=["POST"])
def leave_game(game_ID):   # Done
    username = request.form['username']
    game_id = request.form['game_id']
    password = request.form['password']
    try:
        auth_checker = dao.check_password(username, password)
        if auth_checker:
            dao.leave_game(game_id)
            # result = dao.delete_game(username, game_id)
            # response = {"status": "success"} if result else {"status": "failure(game does not exists)"}
            response = {"status": "success"}
            return jsonify(response)
        else:
            response = {"status": "failure(not admin of the game)"}
            return jsonify(response)
    except NoUserExistsException:
        response = {"status": "failure(bad auth)"}
        return jsonify(response)

@app.route(rest_prefix+'/gamequit/'+'<game_ID>', methods=["POST"])
def quit_game(game_ID):   # Done
    username = request.form['username']
    try:
        dao.quit_game(username)
        response = {"status": "success"}
        return jsonify(response)
    except NoUserExistsException:
        response = {"status": "failure(I have no idea why )"}
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
def update_game(game_ID):  # Done
    username = request.form['username']
    game_id = request.form['game_id']
    lat = request.form['lat']
    lng = request.form['lng']
    RADIUS = 10000   # possible max distance is about 32000m, so let us set it as 10000m
    dao.set_location(username, lat, lng)
    player_id_location = dao.get_location(username)
    player_id = player_id_location['playerid']

    #---------remove protection since the location changed---------------
    dao.delete_save_zone_protection(player_id)

    all_landmark = dao.get_landmark_nearby(lat, lng, game_id) # a list of receivable landmark.
    for entry in all_landmark:
        if entry['type'] == 0: # get in a save zone, then this player is protected
            dao.set_player_stats(player_id,'Protected')
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

@app.route(rest_prefix + '/game/' + '<gameID>'+'/locationInfo', methods=["POST"])
def update_game_info(gameID):
    username = request.form['username']
    password = request.form['password']
    lat = request.form["lat"]
    lng = request.form["lng"]

    lat = float(lat.encode('utf-8'))
    lng = float(lng.encode('utf-8'))

    response = {}

    try:
        dao.set_location(username, lat, lng)
        response["status"] = "success"
        response["info"] = "updated position"
    except Exception:
        response["status"] = "failure"
        response["info"] = "could not update position"

    response["current_time"] = time.time()

    return jsonify(response)

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
def get_vote_stats(game_ID): #Done
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

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/attack', methods=["POST"])
def attack(game_ID):
    username = request.form['username']
    password = request.form['password']
    game_id = int(request.form['game_id'])
    target_id = int(request.form['target_id'])

    gameinfo =  dao.game_info(game_id)
    if gameinfo['daybreak'] < gameinfo['currenttime'] < gameinfo['nightfall']:
        response = {"status": "failure(attack in daytime)"}
        return jsonify(response)
    else:
        try:
            auth_checker = dao.check_password(username, password)
            if auth_checker:

                current_player = dao.get_current_player(username)
                if current_player == None:
                    response = {"status": "failure(not in game)"}
                    return jsonify(response)
                elif current_player['is_werewolf'] == 0:
                    response = {"status": "failure( attacker is a villager)"}
                    return jsonify(response)
                else:
                    if is_in_cooldown(current_player['playerid']):
                        response = {"status": "failure(player is werewolf under cooldown period of 30 minutes)"}
                        return jsonify(response)
                    else:
                        if dao.checking_ifProtected(target_id):
                            response = {'status': 'success', 'results': { 'summary': 'no death, the target is in save zone', 'combatant': target_id }}
                            set_cooldown(current_player['playerid'])
                            return jsonify(response)
                        else:
                            avoidability = random.uniform(0.4,0.8)
                            attackaccuracy = random.uniform(0.4,1)
                            if attackaccuracy >= avoidability:
                                dao.set_dead(target_id)
                                get_One_Kill(current_player['playerid'])
                                response = {'status': 'success', 'results': { 'summary': 'death', 'combatant': target_id }}
                                set_cooldown(current_player['playerid']);
                                return jsonify(response)
                            else:
                                hair_of_dog(target_id)
                                response = {'status': 'success', 'results': { 'summary': 'no death', 'combatant': target_id }}
                                set_cooldown(current_player['playerid']);
                                return jsonify(response)
            else:
                response = {"status": "failure(bad auth)"}
                return jsonify(response)
        except NoUserExistsException:  # bad auth
            response = {"status": "failure(No such a user)"}
            return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/status', methods=["POST"])
def set_game_status(game_ID):
    username = request.form['username']
    game_id = int(request.form['game_id'])
    game_status = int(request.form['game_status'])
    dao.set_game_status(username, game_id, game_status)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/time', methods=["POST"])
def set_game_time(game_ID):
    username = request.form['username']
    game_id = int(request.form['game_id'])
    current_time = request.form['current_time']
    dao.set_game_current_time(username, game_id, current_time)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/werewolf', methods=["POST"])
def set_werewolf(game_ID):
    werewolf_id = request.form['werewolf_id']
    game_id = int(request.form['game_id'])
    dao.set_werewolf(game_id, werewolf_id)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/landmark', methods=["POST"])
def set_landmark(game_ID):
    game_id = request.form['game_id']
    minValue = float(request.form['minValue'])
    maxValue = float(request.form['maxValue'])
    num_landmark = int(request.form['num_landmark'])
    radius = request.form['radius']

    for i in xrange(num_landmark):
        lat = random.uniform(minValue, maxValue)
        lng = random.uniform(minValue, maxValue)
        landmark_type =  random.randint(0,1)
        dao.set_landmark(game_id, lat, lng, radius, landmark_type)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/treasure', methods=["POST"])
def set_treasure(game_ID):
    game_id = request.form['game_id']
    num_landmark = int(request.form['num_landmark'])

    for i in xrange(num_landmark):
        item_id = random.randint(1,1)  # only support invisible portion
        quantity = random.randint(1,2)
        dao.set_treasure(i+1,item_id,quantity) # i is landmark id, # checking landmark type, and do not add treasure to save zone
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/vote_death', methods=["POST"])
def set_top_voted_death(game_ID):
    game_id = request.form['game_id']
    target_id = int(request.form['player_id'])
    dao.set_dead(target_id)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/assign_lupus', methods=["POST"])
def assign_lupus_and_clear_votes_table(game_ID):
    game_id = request.form['game_id']
    death_player_id = int(request.form['death_player_id'])
    dao.assign_lupus_and_clear_votes_table(game_id, death_player_id)
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix+'/game/'+'<game_ID>'+'/game_results', methods=["GET"])
def check_game_results(game_ID):
    game_id = request.form['game_id']
    results = dao.get_all_players_status(game_id)
    if 'alive_werewolf' not in results:
        response = {"game_status": "villagers won"}
    elif 'alive_villager' not in results:
        response = {"game_status": "werewolves won"}
    else:
        response = {"game_status": "continue"}
    return jsonify(response)

@app.route(rest_prefix +'/clean_database', methods=["DELETE"])
def clean_game_data():
    dao.clear_tables()
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix +'/clean_landmark_treasure', methods=["DELETE"])
def clean_landmark_treasure():
    dao.clean_landmark_treasure()
    response = {"status": "success"}
    return jsonify(response)


@app.route(rest_prefix +'/assign_achievement', methods=["POST"])
def assign_achievement():
    dao.assign_achievement()
    response = {"status": "success"}
    return jsonify(response)

@app.route(rest_prefix +'/get_achievement', methods=["GET"])
def get_all_achievement():
    assigned_achive_list = dao.get_all_achievement()
    response = {'results': assigned_achive_list}
    return jsonify(response)

def is_in_cooldown(playerid): # implement in player_stat
    '''
    The whole Logic here is tested and correct; however since one werewolves attacks once each night,
    there is no reason to add it here to slowdown the process.
    So I just disable it for now and may call it later on if multiple attacks allowed in the future
    '''
    return False
    # player_stat = dao.get_player_stats(playerid, name='CoolDown')
    # if player_stat == None:
    #     return False
    # else:
    #     current_time = str(datetime.datetime.time(datetime.datetime.now())).split(".")[0]
    #     last_attack_time =  str(player_stat['stat_time']).split(".")[0]
    #     FMT = '%H:%M:%S'
    #     time_delta = (datetime.datetime.strptime(current_time, FMT) - datetime.datetime.strptime(last_attack_time, FMT))
    #     cooldown = datetime.timedelta(minutes=30)
    #     return time_delta < cooldown

def set_cooldown(playerid): #done
    dao.set_player_stats(playerid, name='CoolDown')

def get_One_Kill(player_id): # done
    dao.set_player_stats(player_id) # default is kill + 1

def hair_of_dog(target_id):
    user_id = dao.get_userID(target_id)
    dao.set_user_achievements(user_id, 1)  # No.1 is hair_of_dog

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)

