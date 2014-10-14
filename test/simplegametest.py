import requests
import json
from pprint import pprint
# def daybreak()

hostname = "http://localhost:5000"
rest_prefix = "/v1"
# game_id = 1

''' Important functions
create a game
leave a game
update game state with location
cast a vote
'''

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

def join_game(username, password, game_id):
    print 'Joining game id {}'.format(game_id)
    payload = {'username': username, 'password': password, 'game_id': game_id}
    url = "{}{}/game/{}/lobby".format(hostname, rest_prefix, game_id)
    r = requests.post(url, auth=(username, password), data=payload)
    response = r.json()
    print response

def update_game(username, password, game_id, lat, lng):
    """ reports to the game your current location, and the game
    returns to you a list of players nearby
    current radius is 5 (miles)
    """
    payload = {'username': username, 'game_id': game_id, 'lat': lat, 'lng': lng}
    url = "{}{}/game/{}".format(hostname, rest_prefix, game_id)
    r = requests.put(url, auth=(username, password), data=payload)
    response = r.json()
    pprint (response)

def game_info(username, password, game_id):
    ''' returns all the players, the time of day, and other options for the game '''
    r = requests.get(hostname + rest_prefix + "/game/" + str(game_id), auth=(username, password))
    response = r.json()
    print response

def cast_vote(username, password, game_id, player_id):
    payload = {'player_id': player_id}
    
    r = requests.post(hostname + rest_prefix + "/game/" + game_id + "/vote")
    response = r.json()

    print response

def set_game_time(game_id, game_time):
    '''allows you to override the current time to a user specified one'''
    payload = {'game_id': game_id, 'current_time': game_time}
    r = requests.post(hostname + rest_prefix + "/game/admin")
    response = r.json()
    print response

def get_games(username, password):
    r = requests.get(hostname + rest_prefix + "/game")
    r = r.json()
    return r["results"]

def create_users():
    create_user('michael', 'paper', 'Michael', 'Scott')
    create_user('dwight', 'paper', 'Dwight', 'Schrute')
    create_user('jim', 'paper', 'Jim', 'Halpert')
    create_user('pam', 'paper', 'Pam', 'Beesly')
    create_user('ryan', 'paper', 'Ryan', 'Howard')
    create_user('andy', 'paper', 'Andy', 'Bernard')
    create_user('angela', 'paper', 'Angela', 'Martin')
    create_user('toby', 'paper', 'Toby', 'Flenderson')

def werewolf_winning_game():
    game_id = create_game('michael', 'paper', 'NightHunt', 'A test for werewolf winning')
    games = get_games('michael', 'paper')
    for game in games:
        print "Id: {},\tName: {}".format(game["game_id"], game["name"])
    
    join_game('dwight', 'paper', game_id)
    join_game('jim', 'paper', game_id)
    join_game('pam', 'paper', game_id)
    join_game('ryan', 'paper', game_id)
    join_game('andy', 'paper', game_id)
    join_game('angela', 'paper', game_id)
    join_game('toby', 'paper', game_id)
    # start_game('michael', 'paper', game_id)
    #
    # leave_game('micheal', 'paper', game_id)


if __name__ == "__main__":

    # create_user('michael', 'paperpaper', 'Michael', 'Scott')
    # create_user('dwight', 'paperpaper', 'Dwight', 'Schrute')
    # create_game('michael', 'paperpaper', 'NightHunt', 'A test for werewolf winning')
    # create_game('dwight', 'paperpaper', 'NightHunt', 'A game in Austin')

    # leave_game('rfdickerson', 'awesome', 1)
    # join_game('dwight', 'paperpaper', 3)  # need to test game lobby status later on
    # join_game('dwight', 'paper', 2)
    # join_game('rfdickerson', 'awesome', 3)
    # join_game('rfdickerson', 'awesome', 2)
    update_game('rfdickerson','awesome', 1, 30, 97)

   #create_users()
   # werewolf_winning_game()
   # create_game('rfdickerson', 'awesome', 'NightHunt', 'A game in Austin')
   # update_game('rfdickerson', 'awesome', 80, 20)
   # game_info('rfdickerson', 'awesome', 22)
   # leave_game('rfdickerson', 'awesome', 302)