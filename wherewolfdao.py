# Wherewolf game DAO
# Abstraction for the SQL database access.

import sqlite3
import md5


class UserAlreadyExistsException(Exception):
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err
        
class NoUserExistsException(Exception):
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err
        
class BadArgumentsException(Exception):
    """Exception for entering bad arguments"""
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err

class WherewolfDao:

    def __init__(self, dbname):
        print 'Created the DAO'
        self.dbname = dbname
        self.conn = sqlite3.connect(dbname)

    def create_player(self, username, password, firstname, lastname):
        """ registers a new player in the system """
        #conn = self.conn
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute('SELECT COUNT(*) from user WHERE username=?',(username,))
            n = int(c.fetchone()[0])
            if n == 0:
                hashedpass = md5.new(password).hexdigest()
                c.execute('INSERT INTO user (username, password, firstname, lastname) VALUES (?,?,?,?)', (username, hashedpass, firstname, lastname))
                self.conn.commit()
            else:
                raise UserAlreadyExistsException('{} user already exists'.format((username)) )
        
    def checkpassword(self, username, password):
        """ return true if password checks out """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            results = c.execute('SELECT password FROM user WHERE username=?',(username,))
            hashedpass = md5.new(password).hexdigest()
            return results.fetchone()[0] == hashedpass
        
    def set_location(self, username, lat, lng):

        pass
        
    def get_location(self, username):
        pass
        
    def get_alive_nearby(self, username):
        """ returns a list of players nearby """
        pass
        
    def add_item(self, username, itemname):
        """ adds a relationship to inventory and or increments quantity by 1"""
        pass
        
    def get_items(self, username):
        """ get a list of items the user has"""
        pass
        
    def award_achievement(self, username, achievementname):
        """ award an achievement to the user """
        pass
        
    def get_achievements(self, username):
        """ return a list of achievements for the user """
        pass
        
    def set_dead(self, username):
        """ set a player as dead """
        pass
        
    def get_players(self, gamename):
        """ get information about all the players currently in the game """
        pass
        
    def get_user_stats(self, username):
        """ return a list of all stats for the user """
        pass
        
    def get_player_stats(self, username):
        """ return a list of all stats for the player """
        pass
        
    # game methods    
    def join_game(self, username, gameid):
        """ makes a player for a user. adds player to a game """
        pass
    
    def leave_game(self, username):
        """ deletes player for user. removes player from game"""
        pass
        
    def create_game(self, username):
        """ creates a new game """
            
    def start_game(self, gameid):
        """ set the game as started """
        
    def end_game(self, gameid):
        """ delete all players in the game, set game as completed """
    
            
if __name__ == "__main__":
    dao = WherewolfDao('wherewolf.db')
    try:
        dao.create_player('rfdickerson', 'furry', 'Robert', 'Dickerson')
        print 'Created a new player!'
    except UserAlreadyExistsException as e:
        print e
    except Exception:
        print 'General error happened'
        
    username = 'rfdickerson'
    correct_pass = 'furry'
    incorrect_pass = 'scaley'
    print 'Logging in {} with {}'.format(username, correct_pass)
    print 'Result: {} '.format( dao.checkpassword(username, correct_pass ))
    
    print 'Logging in {} with {}'.format(username, incorrect_pass)
    print 'Result: {} '.format( dao.checkpassword(username, incorrect_pass ))

# get locationL return a dict[user] = {lat, lng}

