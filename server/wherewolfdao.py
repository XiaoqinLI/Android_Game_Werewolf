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
        self.conn = sqlite3.connect(dbname, timeout=10)

    def create_player(self, username, password, firstname, lastname):
        """ registers a new player in the system """
        conn = self.conn
        # conn = sqlite3.connect(self.dbname)
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
        conn = self.conn
        # conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            results = c.execute('SELECT password FROM user WHERE username=?',(username,))
            hashedpass = md5.new(password).hexdigest()
            return results.fetchone()[0] == hashedpass
        
    def set_location(self, username, lat, lng):
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            c.execute('UPDATE player set lat=' + str(lat) +','+ 'lng=' +str(lng) + ' where userid IN (select userid from user where username =' + "'" + str(username) + "')")

    def get_location(self, username):
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('SELECT lat, lng FROM player as P \
                            join user as U on P.userid = U.userid WHERE username=?',(username,))
            return results.fetchone()

    def get_alive_nearby(self, username):
        """ returns a list of players nearby """
        pass
        
    def add_item(self, username, itemname):
        """ adds a relationship to inventory and or increments quantity by 1"""
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            results = c.execute('select playerid from player where userid=?',(userid,))
            playerid = results.fetchone()[0]
            results = c.execute('select itemid from item where name=?',(itemname,))
            itemid = results.fetchone()[0]
            c.execute('UPDATE inventory set quantity = quantity + 1 where playerid =? and itemid =?',(playerid, itemid))
            c.execute('INSERT OR IGNORE INTO inventory (playerid, itemid, quantity) VALUES (?,?,?)',(playerid, itemid, 1))

    def get_items(self, username):
        """ get a list of items the user has"""
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            results = c.execute('select playerid from player where userid=?',(userid,))
            playerid = results.fetchone()[0]
            results = c.execute('select name, description, quantity from item as IT \
                            join inventory as INV on IT.itemid = INV.itemid \
                            where playerid=?',(playerid,))
            allResults = results.fetchall()
            result = []
            for entry in allResults:
                eleDict = {}
                eleDict["name"] = (entry[0].encode('ascii', 'ignore'))
                eleDict["desciption"] = (entry[1].encode('ascii', 'ignore'))
                eleDict["quantity"] = entry[2]
                result.append(eleDict)
            return result
        
    def award_achievement(self, username, achievementname):
        """ award an achievement to the user """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            results = c.execute('select achievementid from achievement where name=?',(achievementname,))
            achievementid = results.fetchone()[0]
            c.execute('INSERT OR IGNORE INTO user_achievement (userid, achievementid) VALUES (?,?)',(userid,achievementid))
        
    def get_achievements(self, username):
        """ return a list of achievements for the user """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            results = c.execute('select name, description from achievement as A \
                            join user_achievement as UA on A.achievementid = UA.achievementid \
                            where userid=?',(userid,))
            allResults = results.fetchall()
            result = []
            for entry in allResults:
                eleDict = {}
                eleDict["name"] = (entry[0].encode('ascii', 'ignore'))
                eleDict["desciption"] = (entry[1].encode('ascii', 'ignore'))
                result.append(eleDict)
            return result


    def set_dead(self, username):
        """ set a player as dead """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid, current_player from user where username=?',(username,))
            allResults = results.fetchone()
            userid = allResults[0]
            currentPlayer = allResults[1]
            c.execute('update player set is_dead = 1 where userid=? and playerid=?',(userid,currentPlayer))

    def get_players(self, gamename):
        """ get information about all the players currently in the game """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select gameid from game where name=?',(gamename,))
            gameid = results.fetchone()[0]
            results = c.execute('select * from player where game_id=?',(gameid,))
            allResults = results.fetchall()
            result = []
            for entry in allResults:
                eleDict = {}
                eleDict["playerid"] = entry[0]
                eleDict["userid"] = entry[1]
                eleDict["is_dead"] = entry[2]
                eleDict["lat"] = entry[3]
                eleDict["lng"] = entry[4]
                eleDict["is_werewolf"] = entry[5]
                eleDict["num_gold"] = entry[6]
                eleDict["game_id"] = entry[7]
                result.append(eleDict)
            return result
        
    def get_user_stats(self, username):
        """ return a list of all stats for the user """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            results = c.execute('select statName from user_stat where userid=?',(userid,))
            allResults = results.fetchall()
            result = []
            for ele in allResults:
                result.append(ele[0].encode('ascii', 'ignore'))
            return result

    # this function does not make any sense, there is only playerid and numKills in the player_stat table
    # you at least need userid to nail down which player is this one,
    # also,you will need statid in this table, and make another table : stats, which has statid, name, description, quantity
    # now I am only returning numKills for current player of a user since that is all I can do.(since you said: all stats for 'the player' )
    def get_player_stats(self, username):
        """ return a list of all stats for the player """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid, current_player from user where username=?',(username,))
            allResults = results.fetchone()
            userid = allResults[0]
            currentPlayer = allResults[1]
            results = c.execute('select numKills from player_stat where userid=? and playerid=?',(userid,currentPlayer))
            result = [results.fetchone()[0]]
            return result
        
    # game methods    
    def join_game(self, username, gameid):
        """ makes a player for a user. adds player to a game """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            c.execute('INSERT INTO player (userid, is_dead, lat, lng, game_id) VALUES (?, 0, 38, 79, ?)',(userid,gameid))
            # results = c.execute('select playerid from player where userid='+str(userid) + " order by playerid desc limit 1 ")
            # newplayerid = results.fetchone()[0]

    def leave_game(self, username):
        """ deletes player for user. removes player from game"""
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            c.execute('delete from player where userid=?',(userid,))
        
    def create_game(self, username):
        """ creates a new game """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            results = c.execute('select userid from user where username=?',(username,))
            userid = results.fetchone()[0]
            c.execute('INSERT INTO game (adminid, name) VALUES (?, ?)',(userid,'game2'))

    def start_game(self, gameid):
        """ set the game as started """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            c.execute('UPDATE game set status = 1 where gameid =?',(gameid,))

    def end_game(self, gameid):
        """ delete all players in the game, set game as completed """
        conn = self.conn
        with conn:
            c = self.conn.cursor()
            c.execute('UPDATE game set status = 0 where gameid =?',(gameid,))
            c.execute('UPDATE player set game_id = null where game_id =?',(gameid,))
            
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

    print 'set location of rob'
    dao.set_location("rfdickerson", 5000, 6000)
    print 'get location of rob'
    print dao.get_location("rfdickerson")

    print 'add item'
    dao.add_item("rfdickerson","Wolfsbane Potion")

    print 'get items'
    print dao.get_items('rfdickerson')

    print 'assign an achievement'
    dao.award_achievement('rfdickerson', 'Top of the pack')

    print 'get achievements'
    print dao.get_achievements('rfdickerson')

    print 'set a play is dead'
    dao.set_dead('rfdickerson')

    print ' get information about all the players currently in the game '
    print dao.get_players('game1')

    print "return a list of all stats for the user"
    print dao.get_user_stats('rfdickerson')

    print "return a list of all stats for the player"
    print dao.get_player_stats('rfdickerson')

    print """makes a player for a user. adds player to a game """
    dao.join_game('rfdickerson', 1)

    print """deletes player for user. removes player from game"""
    dao.leave_game('vanhelsing')

    print """creates a new game """
    dao.create_game('rfdickerson')

    print """set the game as started """
    dao.start_game(2)

    print """delete all players in the game, set game as completed """
    dao.end_game(1)


