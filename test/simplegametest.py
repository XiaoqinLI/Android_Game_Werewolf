import requests
import json
from pprint import pprint

hostname = "http://localhost:5000"
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
    pprint (response)
    return r

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
    '''allows you to override the current time to a user specified one'''
    payload = {'username': username, 'game_id': game_id, 'game_status': game_status}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/status", data=payload )

def get_games(username, password):
    r = requests.get(hostname + rest_prefix + "/game")
    r = r.json()
    return r["results"]

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


if __name__ == "__main__":
    game_round = 0
    #------------------Game Simulation---------------------------------
    # The client script will register 8 new users in the game (michael,
    # dwight, jim, pam, ryan, andy, angela, toby)
    # create_users()

    # A new game called NightHunt will be created by michael. michael will automatically be
    # added to that game. Next, all the other users will join that game,creating new players for each.
    # current_game_id = create_game('michael', 'paper01', 'NightHunt', 'A test for werewolf winning')
    # all_join_game(current_game_id)

    # michael will set the game to active, and the first day round begins. 30% of the players rounding up will be
    # set to be werewolves- 3 werewolves in our case.
    # set_game_status('michael', 1, 1)
    set_game_time('michael', 1, '08:00:00')
    # game_round += 1








    # leave_game('rfdickerson', 'awesome', 1)
    # join_game('dwight', 'paperpaper', 3)  # need to test game lobby status later on
    # join_game('dwight', 'paper', 2)
    # join_game('rfdickerson', 'awesome', 3)
    # join_game('rfdickerson', 'awesome', 2)
    # update_game('rfdickerson','awesome', 1, 30, 97)
    # game_info('rfdickerson','awesome','1')
    # cast_vote('rfdickerson', 'awesome',1,3)
    # get_vote_stats('rfdickerson','awesome','1')
    # pass
    # attack('rfdickerson', 'awesome', '1', '3')



   #create_users()
   # werewolf_winning_game()
   # create_game('rfdickerson', 'awesome', 'NightHunt', 'A game in Austin')
   # update_game('rfdickerson', 'awesome', 80, 20)
   # game_info('rfdickerson', 'awesome', 22)
   # leave_game('rfdickerson', 'awesome', 302)

