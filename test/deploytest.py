import requests
import json
from pprint import pprint
import random
import math

hostname = "http://wherewolfLB-1277079358.us-west-2.elb.amazonaws.com"
# hostname = "http://ec2-54-186-76-62.us-west-2.compute.amazonaws.com:5000"

rest_prefix = "/v1"

def create_user(username, password, firstname, lastname):  # Done
    payload = {'username': username, 'password': password, 'firstname': firstname, 'lastname': lastname}
    url = "{}{}{}".format(hostname, rest_prefix, "/register")
    r = requests.post(url, data=payload)
    response = r.json()
    print response

def create_game(username, password, game_name, description): # Done
    payload = {'username': username, 'password':password, 'game_name': game_name, 'description': description}
    url = "{}{}/game".format(hostname, rest_prefix)
    print 'sending {} to {}'.format(payload, url)
    r = requests.post(url, auth=(username, password), data=payload)
    response = r.json()
    print response["status"]
    join_game(username, password, response["results"]["game_id"]) # the guy created game gonna join it automatically
    return response["results"]["game_id"]
    
def leave_game(username, password, game_id): # Done
    '''
    first of all, set game_id to null in player table if gameid = gameid, otherwise can't delete table since
    game_id is a foreign key
    Then, delete the game if exist.
    '''
    payload = {'username': username, 'password':password, 'game_id': game_id}
    r = requests.delete(hostname + rest_prefix + "/game/" + str(game_id),
                        auth=(username, password), data=payload)
    print r
    response = r.json()
    print response
    # return response

def join_game(username, password, game_id): # Done
    print 'Joining game id {}'.format(game_id)
    payload = {'username': username, 'password': password, 'game_id': game_id}
    url = "{}{}/game/{}/lobby".format(hostname, rest_prefix, game_id)
    r = requests.post(url, auth=(username, password), data=payload)
    response = r.json()
    print response

def update_game(username, password, game_id, lat, lng): # Done
    """ reports to the game your current location, and the game
    returns to you a list of players nearby if you are alive werewolf
    """
    payload = {'username': username, 'game_id': game_id, 'lat': lat, 'lng': lng}
    url = "{}{}/game/{}".format(hostname, rest_prefix, game_id)
    r = requests.put(url, auth=(username, password), data=payload)
    response = r.json()
    print response

def game_info(username, password, game_id): # Done
    ''' returns all the players, the time of day, and other options for the game '''
    payload = {'username': username, 'password': password, 'game_id': game_id}
    r = requests.get(hostname + rest_prefix + "/game/" + str(game_id), auth=(username, password), data=payload)
    response = r.json()
    # pprint (response)
    return response

def cast_vote(username, password, game_id, player_id): # Done
    '''each call will vote for an particular target player'''
    payload = {'username': username, 'password': password, 'game_id': game_id, 'player_id': player_id}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) + "/vote",auth=(username, password), data=payload)
    response = r.json()
    print response

def get_vote_stats(username, password, game_id): # Done
    payload = {'username': username, 'password': password, 'game_id': game_id}
    r = requests.get(hostname + rest_prefix + "/game/" + str(game_id) + "/vote",auth=(username, password), data=payload)
    response = r.json()
    print response
    return response

def attack(username, password, game_id, target_id):
    payload = {'username': username, 'password': password, 'game_id': game_id, 'target_id': target_id}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) + "/attack",auth=(username, password), data=payload)
    response = r.json()
    print response

def set_game_time(username, game_id, game_time):
    '''allows you to override the current time to a user specified one'''
    payload = {'username': username, 'game_id': game_id, 'current_time': game_time}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/time", data=payload)
    response = r.json()
    print response

def set_game_status(username, game_id, game_status):
    payload = {'username': username, 'game_id': game_id, 'game_status': game_status}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/status", data=payload )

def set_werewolf(game_id, werewolf_id):
    payload = {'werewolf_id': werewolf_id, 'game_id': game_id}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/werewolf", data=payload )

def get_games(username, password):
    r = requests.get(hostname + rest_prefix + "/game")
    r = r.json()
    return r["results"]

def set_random_landmark(game_id):
    minValue = 9.9
    maxValue = 10.1
    radius = 2500  # 2500 meters is quite far away
    num_landmark = random.randint(3,5)  # 3 to 5 landmark
    payload = {'game_id': game_id, 'minValue': minValue, 'maxValue': maxValue, 'radius': radius, 'num_landmark': num_landmark}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/landmark", data=payload )
    return num_landmark

def set_treasure_to_landmark(game_id, num_landmark):
    payload = {'game_id': game_id, 'num_landmark': num_landmark}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/treasure", data=payload )
    return

def set_top_voted_death(game_id, player_id):
    payload = {'game_id': game_id, 'player_id': player_id}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/vote_death", data=payload )
    return

def assign_lupus_and_clear_votes_table(game_id, death_player_id):
    payload = {'game_id': game_id, 'death_player_id': death_player_id}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/assign_lupus", data=payload )
    return

def check_game_results(game_id):
    payload = {'game_id': game_id}
    r = requests.get(hostname + rest_prefix + "/game/" + str(game_id) +"/game_results", data=payload )
    response = r.json()
    return response

def assign_achievement():
    r = requests.post(hostname + rest_prefix + "/assign_achievement")
    response = r.json()
    return response

def get_all_achievement():
    r = requests.get(hostname + rest_prefix + "/get_achievement")
    response = r.json()
    return response

def create_users():
    create_user('michael', 'paper01', 'Michael', 'Scott')
    create_user('dwight', 'paper02', 'Dwight', 'Schrute')
    create_user('jim', 'paper03', 'Jim', 'Halpert')
    create_user('pam', 'paper04', 'Pam', 'Beesly')
    create_user('ryan', 'paper05', 'Ryan', 'Howard')
    create_user('andy', 'paper06', 'Andy', 'Bernard')
    create_user('angela', 'paper07', 'Angela', 'Martin')
    create_user('toby', 'paper08', 'Toby', 'Flenderson')

def all_join_game(current_game_id):
    join_game('dwight', 'paper02', current_game_id)
    join_game('jim', 'paper03', current_game_id)
    join_game('pam', 'paper04', current_game_id)
    join_game('ryan', 'paper05', current_game_id)
    join_game('andy', 'paper06', current_game_id)
    join_game('angela', 'paper07', current_game_id)
    join_game('toby', 'paper08', current_game_id)

    username_password_playerid_list = []  # hard coded this for voting, no better way for our case
    username_password_playerid_list.append({'username': 'michael', 'password': 'paper01', 'playerid': 1})
    username_password_playerid_list.append({'username': 'dwight', 'password': 'paper02', 'playerid': 2})
    username_password_playerid_list.append({'username': 'jim', 'password': 'paper03', 'playerid': 3})
    username_password_playerid_list.append({'username': 'pam', 'password': 'paper04', 'playerid': 4})
    username_password_playerid_list.append({'username': 'ryan', 'password': 'paper05', 'playerid': 5})
    username_password_playerid_list.append({'username': 'andy', 'password': 'paper06', 'playerid': 6})
    username_password_playerid_list.append({'username': 'angela', 'password': 'paper07', 'playerid': 7})
    username_password_playerid_list.append({'username': 'toby', 'password': 'paper08', 'playerid': 8})
    return username_password_playerid_list

def clean_game_data():
    r = requests.delete(hostname + rest_prefix +"/clean_database" )
    response = r.json()
    return response

def clean_landmark_treasure():
    r = requests.delete(hostname + rest_prefix +"/clean_landmark_treasure" )
    response = r.json()
    return response


def update_locations(current_game_id):
    #positioned in a rectangular region (9.9, 9.9),(10.1, 10.1). Max possible Distance: 31210m
    minValue = 9.9
    maxValue = 10.1
    update_game('michael', 'paper01', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('dwight', 'paper02', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('jim', 'paper03', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('pam', 'paper04', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('ryan', 'paper05', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('andy', 'paper06', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('angela', 'paper07', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
    update_game('toby', 'paper08', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))

if __name__ == "__main__":

    print "--------------------Preparing the game------------------------------------------"
    clean_game_data()
    print "Preparation Done"
    game_round = 0
    #------------------Game Simulation---------------------------------
    # The client script will register 8 new users in the game (michael,
    # dwight, jim, pam, ryan, andy, angela, toby)
    print "-----------creating users-------------------------------------------------------"
    create_users()

    # A new game called NightHunt will be created by michael. michael will automatically be
    # added to that game. Next, all the other users will join that game,creating new players for each.
    print "------------------------------creating a game-----------------------------------"
    admin_name = 'michael'
    admin_pwd = 'paper01'
    current_game_id = create_game(admin_name, admin_pwd, 'NightHunt', 'A test for werewolf winning')
    print '-----------------everyone joins the game----------------------------------------'
    username_password_playerid_list = all_join_game(current_game_id)
    print "Preparation Done"

    # # michael will set the game to active, and the first day round begins.
    # print '-----------------start the game-------------------------------------------------'
    # set_game_status(admin_name, current_game_id, 1)
    # set_game_time(admin_name, current_game_id, '08:00:00')
    # # game_round += 1
    #
    # # 30% of the players rounding up will be set to be werewolves (3 werewolves in our case)
    # print '-----------------current game info----------------------------------------------'
    # current_game_info = game_info(admin_name, admin_pwd, current_game_id)
    # random_playerid_list =[ entry['playerid'] for entry in current_game_info['players'] ]
    # random.shuffle(random_playerid_list)
    # num_werewolf = int(math.ceil(len(random_playerid_list)*0.3))
    # for i in xrange(num_werewolf):
    #     set_werewolf(current_game_id,random_playerid_list[i])
    #
    # # Daytime:  Vote starts at day 2. All Players will be randomly positioned in a rectangular region.
    # # The admin sets the round to night.
    # # One werewolf will move to a location of one random villager. The werewolf will make an attack. The villager may or may not survive this encounter
    # # The admin sets the round to day.
    # pprint (current_game_info)  # print game_info before it starts.
    #
    # while(current_game_info['status'] == 1):  # as long as game not end yet, continue play
    #     print "---------------------------set to a day time--------------------------------"
    #     set_game_time(admin_name, '1', '10:00:00')
    #     game_round += 1
    #
    #     print "%%%%%%-----------------------Game in daytime-------------------------%%%%%%%"
    #     print "%%%%%%----------------------------round {}---------------------------%%%%%%%".format(game_round)
    #     if game_round <= 1:      #There is no vote in first day round
    #         print "------------set all random landmark in random place in day 1------------"
    #         numLandmark = set_random_landmark(current_game_id)   # set random land marks on the game in day 1
    #         print "--------attach treasure to each landmark if it is not a save zone-------"
    #         set_treasure_to_landmark(current_game_id,numLandmark)         # link treasure to landmark
    #         update_locations(current_game_id)
    #     else:
    #         print "--------- reset all random landmark in random place after day 1---------"
    #         clean_landmark_treasure()
    #         numLandmark = set_random_landmark(current_game_id)   # set random land marks on the game after day 1
    #         print "----attach treasure to each landmark if it is not a save zone-----------"
    #         set_treasure_to_landmark(current_game_id,numLandmark)         # link treasure to landmark
    #
    #         #-------get all alive playerid:----------------
    #         alive_playerid_list = []
    #         for player in current_game_info['players']:
    #             if player['is_dead'] == 0:
    #                 alive_playerid_list.append(int(player['playerid']))
    #
    #         # -------------every one votes------------------
    #         print "-----------Voting begins------------------------------------------------"
    #         for player in current_game_info['players']:
    #             if int(player['is_dead']) == 0:
    #                 # get the user_info
    #                 for i in xrange(len(username_password_playerid_list)):
    #                     if username_password_playerid_list[i]['playerid'] == player['playerid']:
    #                         userInfo = username_password_playerid_list[i]
    #                         break
    #
    #                 # get target id, target id can not be voter's player id
    #                 target_id = alive_playerid_list[random.randint(0,len(alive_playerid_list)-1)]
    #                 while(target_id == player['playerid']):
    #                     target_id = alive_playerid_list[random.randint(0,len(alive_playerid_list)-1)]
    #                 # call vote function
    #                 cast_vote(userInfo['username'], userInfo['password'], current_game_id, target_id)
    #
    #         print '-------showing vote results and checking if game is end, clear all vote info after pull over vote results'
    #         vote_results = get_vote_stats(admin_name, admin_pwd, current_game_id)
    #         vote_results_sorted = sorted(vote_results['results'], key = lambda k: k['votes'], reverse=True )
    #
    #         print "-----------------voting results for round {}----------------------------".format(game_round)
    #         pprint (vote_results_sorted)
    #
    #         print '#---------------set top voted player to death---------------------------'
    #         if len(vote_results_sorted) > 0:
    #             dead_playerid = vote_results_sorted[0]['playerid']
    #             set_top_voted_death(current_game_id,dead_playerid)
    #             for player in current_game_info['players']:
    #                 if dead_playerid == player['playerid']:
    #                     werewolf_checker = player['is_werewolf']
    #                     break
    #
    #             print '#-----------if the dead is not a werewolf----assign lupus----------#'
    #             if werewolf_checker == 0:   # then assign lupus to each player's stats
    #                 assign_lupus_and_clear_votes_table(current_game_id, dead_playerid)
    #
    #         print '#---------------is game ended?? who won??-------------------------------'
    #         game_results = check_game_results(current_game_id)
    #         print game_results
    #         if game_results['game_status'] == 'villagers won' or game_results['game_status'] == 'werewolves won':
    #             set_game_status(admin_name, current_game_id, 2)
    #             break  # game ended
    #         else:
    #             update_locations(current_game_id)
    #
    #     print "#-----------update current_game_info before night comes---------------------"
    #     current_game_info = game_info(admin_name, admin_pwd, current_game_id)
    #
    #     ########################THE NIGHT IS COMING###################################################
    #     print "set to a night time"
    #     set_game_time(admin_name, current_game_id, '20:00:00')
    #     print "%%%%%%-----------------------Game in night time-------------------------%%%%"
    #     print "%%%%%%----------------------------round {}---------------------------%%%%%%%".format(game_round)
    #
    #     alive_werewolf_list = []
    #     alive_village_list = []
    #     for player in current_game_info['players']:
    #         if player['is_dead'] == 0 and player['is_werewolf'] == 0:
    #             alive_village_list.append(int(player['playerid']))
    #         elif player['is_dead'] == 0 and player['is_werewolf'] != 0:
    #             alive_werewolf_list.append(int(player['playerid']))
    #     # -------------every one votes--------------------
    #     print "----------------Attacking Begins--------------------------------------------"
    #
    #     attacker_id = alive_werewolf_list[random.randint(0,len(alive_werewolf_list)-1)]
    #     target_id = alive_village_list[random.randint(0,len(alive_village_list)-1)]
    #
    #     for i in xrange(len(username_password_playerid_list)):
    #         if username_password_playerid_list[i]['playerid'] == attacker_id:
    #             attackerInfo = username_password_playerid_list[i]
    #
    #     # call attack function
    #     attack(attackerInfo['username'], attackerInfo['password'], current_game_id, target_id)
    #
    #     print '#---------------is game ended?? werewolves won???---------------------------'
    #     game_results = check_game_results(current_game_id)
    #     print game_results
    #     if game_results['game_status'] == 'werewolves won':
    #         set_game_status(admin_name, current_game_id, 2)
    #         break  # game ended
    #     else:
    #         pass
    #     # update current game info before going next loop.
    #     current_game_info = game_info(admin_name, admin_pwd, current_game_id)
    #
    # ########################THE GAME HAS ENDED#########################################
    # current_game_info = game_info(admin_name, admin_pwd, current_game_id)
    # print "Game status: {}".format(current_game_info['status'])
    # print "------------------game ended----------------------------------------------------"
    # #Set all the game's users' current player field to NULL
    # print "------------------everyone left the game----------------------------------------"
    # leave_game(admin_name, admin_pwd, current_game_id)
    # print "------------------Assigning Achievements----------------------------------------"
    # assign_achievement()
    # print "----------------Show all achievement made in last game--------------------------"
    # all_achievement = get_all_achievement()
    # pprint(all_achievement)



