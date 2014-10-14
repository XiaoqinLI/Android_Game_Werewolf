# Wherewolf game DAO
# Abstraction for the SQL database access.

import psycopg2
import md5
from sqlalchemy.exc import IntegrityError
from pprint import pprint
# from datetime import time
# from datetime import datetime


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

class WherewolfDao(object):

    def __init__(self, dbname='wherewolf', pgusername='postgres', pgpasswd='121314'):
        self.dbname = dbname
        self.pgusername = pgusername
        self.pgpasswd = pgpasswd
        print ('connection to database {}, user: {}, password: {}'.format(dbname, pgusername, pgpasswd))

    def get_db(self):
        return psycopg2.connect(database=self.dbname,user=self.pgusername,password=self.pgpasswd)

    def create_user(self, username, password, firstname, lastname): # create gameuser, tested
        """ registers a new player in the system """
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('SELECT COUNT(*) from gameuser WHERE username=%s',(username,))
            n = int(c.fetchone()[0])
            # print 'num of rfdickersons is ' + str(n)
            if n == 0:
                hashedpass = md5.new(password).hexdigest()
                c.execute('INSERT INTO gameuser (username, password, firstname, lastname) VALUES (%s,%s,%s,%s)', 
                          (username, hashedpass, firstname, lastname))
                conn.commit()
                # return True
            else:
                # return False
                raise UserAlreadyExistsException('{} user already exists'.format((username)) )

    def check_password(self, username, password): # tested
        """ return true if password checks out """
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            sql = ('select password from gameuser where username=%s')
            c.execute(sql,(username,))
            hashedpass = md5.new(password).hexdigest()
            u = c.fetchone()
            if u == None:
                raise NoUserExistsException(username)
            # print 'database contains {}, entered password was {}'.format(u[0],hashedpass)
            return u[0] == hashedpass

    def set_location(self, username, lat, lng): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('update player set lat=%s, lng=%s '
                   'where player_id=(select current_player from gameuser '
                   'where username=%s)')
            cur.execute(sql, (lat, lng, username))
            conn.commit()

    def get_location(self, username): # tested
        conn = self.get_db()
        result = {}
        with conn:
            c = conn.cursor()
            sql = ('select player_id, lat, lng from player, gameuser '
                   'where player.player_id = gameuser.current_player '
                   'and gameuser.username=%s')
            c.execute(sql, (username,))
            row = c.fetchone()
            result["playerid"] = row[0]
            result["lat"] = row[1]
            result["lng"] = row[2]
        return result

    def get_alive_nearby(self, username, game_id, radius): # modified and tested
        ''' returns all alive players near a player '''
        conn = self.get_db()
        result = []
        with conn:
            c = conn.cursor()
            # sql = ('select username, player_id, point( '
            #        '(select lng from player, gameuser '
            #        'where player.player_id=gameuser.current_player '
            #        'and gameuser.username=%s), '
            #        '(select lat from player, gameuser '
            #        'where player.player_id=gameuser.current_player '
            #        'and gameuser.username=%s)) '
            #        '<@> point(lng, lat)::point as distance, '
            #        'is_werewolf '
            #        'from player, gameuser where game_id=%s '
            #        'and is_dead=0 '
            #        'and gameuser.current_player=player.player_id '
            #        'order by distance')

            # using the radius for lookups now
            # not fixed yet..
            sql = ( 'select player_id from player where '
                    'earth_box(ll_to_earth( '
                    '(select lat from player, gameuser '
                    'where player.player_id = gameuser.current_player '
                    'and gameuser.username=%s), '
                    '(select lng from player, gameuser '
                    'where player.player_id = gameuser.current_player '
                    'and gameuser.username=%s)), %s) '
                    '@> ll_to_earth(lat, lng) '
                    'and is_dead = 0 '
                    'and game_id = %s'
                    )
            # print sql
            c.execute(sql, (username, username, radius, game_id))
            for row in c.fetchall():
                d = {}
                d["player_id"] = row[0]
                # d["player_id"] = row[1]
                # d["distance"] = row[2]
                # d["is_werewolf"] = row[3]
                result.append(d)
        return result

    def add_item(self, username, itemname): # tested
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            cmdupdate = ('update inventory set quantity=quantity+1'
                         'where itemid=(select itemid from item where name=%s)' 
                         'and playerid='
                         '(select current_player from gameuser where username=%s);')
            cmd = ('insert into inventory (playerid, itemid, quantity)' 
                   'select (select current_player from gameuser where username=%s) as cplayer,'
                   '(select itemid from item where name=%s) as item,' 
                   '1 where not exists'
                   '(select 1 from inventory where itemid=(select itemid from item where name=%s)' 
                   'and playerid=(select current_player from gameuser where username=%s))')
            cur.execute(cmdupdate + cmd, (itemname, username, username, itemname, itemname, username))
            conn.commit()

    def remove_item(self, username, itemname):  # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update inventory set quantity=quantity-1 where ' 
                   'itemid=(select itemid from item where name=%s) and ' 
                   'playerid=(select current_player from gameuser where username=%s);')
            cmddelete = ('delete from inventory where itemid=(select itemid from item where name=%s)' 
                         'and playerid=(select current_player from gameuser where username=%s) '
                         'and quantity < 1;')
            cur.execute(cmd + cmddelete, (itemname, username, itemname, username))
            conn.commit()

    def get_items(self, username):   # tested
        conn = self.get_db()
        items = []
        with conn:
            c = conn.cursor()
            sql = ('select item.name, item.description, quantity '
                   'from item, inventory, gameuser where '
                   'inventory.itemid = item.itemid and '
                   'gameuser.current_player=inventory.playerid and '
                   'gameuser.username=%s')
            c.execute(sql, (username,))
            for item in c.fetchall():
                d = {}
                d["name"] = item[0]
                d["description"] = item[1]
                d["quantity"] = item[2]
                items.append(d)
        return items

    def award_achievement(self, username, achievementname):  # tested
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            cmd = ('insert into user_achievement (user_id, achievement_id, created_at) '
                   'values ((select user_id from gameuser where username=%s), '
                   '(select achievement_id from achievement where name=%s), now());')
            cur.execute(cmd, (username, achievementname))
            conn.commit()

    def get_achievements(self, username):   # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('select name, description, created_at from achievement, user_achievement '
                   'where achievement.achievement_id = user_achievement.achievement_id '
                   'and user_achievement.user_id = '
                   '(select user_id from gameuser where username=%s);')
            cur.execute(cmd, (username,))
            achievements = []
            for row in cur.fetchall():
                d = {}
                d["name"] = row[0]
                d["description"] = row[1]
                d["created_at"] = row[2]
                achievements.append(d)
        return achievements

    def set_dead(self, username): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update player set is_dead=1 '
                   'where player_id='
                   '(select current_player from gameuser where username=%s);')
            cur.execute(cmd, (username,))
            conn.commit()

    def get_players(self, gameid): # tested
        conn = self.get_db()
        players = []
        with conn:
            cur = conn.cursor()
            cmd = ('select player_id, is_dead, lat, lng, is_werewolf from player '
                   ' where game_id=%s;')
            cur.execute(cmd, (gameid,))
            for row in cur.fetchall():
                p = {}
                p["playerid"] = row[0]
                p["is_dead"] = row[1]
                p["lat"] = float(row[2])
                p["lng"] = float(row[3])
                p["is_werewolf"] = row[4]
                players.append(p)
        return players

    def get_current_player(self, username):
        conn = self.get_db()
        currentplayer = {}
        with conn:
            cur = conn.cursor()
            cmd = ('select player_id, is_dead, lat, lng, is_werewolf from player '
                   ' where player_id=(select current_player from gameuser where username=%s) ')
            cur.execute(cmd, (username,))
            row = cur.fetchone()
            currentplayer["playerid"] = row[0]
            currentplayer["is_dead"] = row[1]
            currentplayer["lat"] = float(row[2])
            currentplayer["lng"] = float(row[3])
            currentplayer["is_werewolf"] = row[4]
            return currentplayer

    def get_user_stats(self, username):
        pass
        # conn = self.get_db()
        # with conn:
        #     cur = conn.cursor()
        #     cmd = ('SELECT user_stat.user_id, user_stat.stat_name, user_stat.stat_value from user_stat, gameuser '
        #            ' where user_stat.user_id=gameuser.user_id and '
        #            ' gameuser.username=%s')
        #     cur.execute(cmd, (username,))
        #     row = cur.fetchone()
        #     uStats = {}
        #     uStats["user_id"] = row[0]
        #     uStats["stat_name"] = row[1]
        #     uStats["stat_value"] = row[2]
        # return uStats

    def get_player_stats(self, username):
        pass
        # conn = self.get_db()
        # with conn:
        #     cur = conn.cursor()
        #     cmd = ('SELECT player_stat.player_id, player_stat.stat_name, player_stat.stat_value from player_stat, gameuser '
        #            ' where player_stat.player_id = gameuser.current_player and '
        #            ' gameuser.username=%s')
        #     cur.execute(cmd, (username,))
        #     row = cur.fetchone()
        #     pStats = {}
        #     pStats["user_id"] = row[0]
        #     pStats["stat_name"] = row[1]
        #     pStats["stat_value"] = row[2]
        #     return pStats

    # game methods    
    def join_game(self, username, gameid): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd0 = ('Select game_id from player where player_id=(Select current_player from gameuser where username=%s)')
            cur.execute(cmd0,(username,))
            typechecker = cur.fetchone()
            if type(typechecker).__name__ != 'NoneType':
                typechecker = typechecker[0]

            if typechecker != None:
                return False
            else:
                cmd1 = ('INSERT INTO player ( is_dead, lat, lng, game_id) '
                        'VALUES ( %s, %s, %s, %s) returning player_id')
                cmd2 = ('update gameuser set current_player=%s where username=%s')
                cur.execute(cmd1,( 0, 0, 0, gameid))
                cur.execute(cmd2, (cur.fetchone()[0], username));
                conn.commit()
                return True

    def quit_game(self, username): # totally quit wherewolf game, not just a specific game; tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd1 = '''UPDATE gameuser set current_player = null where username=%s'''
            cur.execute(cmd1, (username,)) 
            conn.commit()

    def leave_game(self, game_id, player_id = None): # one specific or all players leave this game, #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            if player_id == None:
                cmd = '''UPDATE player set game_id = null where game_id=%s'''
                cur.execute(cmd, (game_id,))
                conn.commit()
            else:
                cmd = '''UPDATE player set game_id = null where game_id=%s and player_id=%s'''
                cur.execute(cmd, (game_id, player_id))
                conn.commit()

    def create_game(self, username, gamename, description = ""): # tested
        ''' returns the game id for that game '''
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cur.execute('SELECT COUNT(*) from game WHERE admin_id=(select user_id from gameuser '
                    'where username=%s)',(username,))
            n = int(cur.fetchone()[0])
            # print 'num of rfdickersons is ' + str(n)
            if n == 0:
                cmd = ('INSERT INTO game (admin_id, name, description) VALUES ( '
                    '(SELECT user_id FROM gameuser where username=%s), '
                    '%s,%s) returning game_id')
                cur.execute(cmd,(username, gamename, description))
                game_id = cur.fetchone()[0]
                conn.commit()
                return game_id
            else:
                return None

    def delete_game(self, username, game_id):  # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()

            cmd = ('select count(*) from game where game_id=%s and admin_id=(select user_id from gameuser where username=%s)' )
            cur.execute(cmd, (game_id, username))
            result = cur.fetchone()[0]
            if result == 0:
                return False
            else:
                cmddelete = ('delete from game where admin_id=(select user_id from gameuser where username=%s) '
                             ' and game_id=%s ')
                cur.execute(cmddelete, (username, game_id))
                conn.commit()
                return True

    def game_info(self, game_id): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = '''SELECT game_id, admin_id, status, name, daybreak, nightfall, currenttime from game where game_id=%s'''
            cur.execute(cmd, (game_id,))
            row = cur.fetchone()
            if row != None:
                d = {}
                d["game_id"] = row[0]
                d["admin_id"] = row[1]
                d["status"] = row[2]
                d["name"] = row[3]
                d["daybreak"] = row[4]
                d["nightfall"] = row[5]
                d["currenttime"] = row[6]
                return d
            else:
                return None

    # def get_game(self, username, game_name):  #tested
    #     conn = self.get_db()
    #
    #     with conn:
    #         cur = conn.cursor()
    #         cmd = ('SELECT game_id from game where game_id=%s and admin_id=(select user_id from gameuser where username=%s)'')
    #         cur.execute(cmd)
    #         for row in cur.fetchall():
    #             d = {}
    #             d["game_id"] = row[0]
    #             d["name"] = row[1]
    #             d["status"] = row[2]
    #             games.append(d)
    #     return games

    def get_games(self):  #tested
        conn = self.get_db()
        games = []
        with conn:
            cur = conn.cursor()
            cmd = ('SELECT game_id, name, status from game')
            cur.execute(cmd)
            for row in cur.fetchall():
                d = {}
                d["game_id"] = row[0]
                d["name"] = row[1]
                d["status"] = row[2]
                games.append(d)
        return games

    def set_game_status(self, game_id, status): #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE game set status=%s '
                   'where game_id=%s')
            cur.execute(cmd, (game_id, status))

    def vote(self, game_id, player_id, target_id): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('insert into vote '
                   '(game_id, player_id, target_id, cast_date) '
                   'values ( %s,'
                   '(select current_player from gameuser where username=%s), '
                   '(select current_player from gameuser where username=%s), '
                   'now())')
            cur.execute(sql, (game_id, player_id, target_id))
            conn.commit()

    def clear_tables(self):  # modified and tested
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('truncate gameuser RESTART IDENTITY cascade')
            c.execute('truncate player RESTART IDENTITY cascade')
            c.execute('truncate user_achievement RESTART IDENTITY cascade')
            conn.commit()

            
if __name__ == "__main__":
    dao = WherewolfDao()

    # dao.clear_tables()#clear gameuser, player and user_achievement table
    # try:
    #     dao.create_user('rfdickerson', 'awesome', 'Robert', 'Dickerson')
    #     dao.create_user('oliver','furry','Oliver','Cat')
    #     dao.create_user('vanhelsing', 'van', 'Van', 'Helsing')
    #     print 'Created new players!'
    # except UserAlreadyExistsException as e:
    #     print e
    # except Exception:
    #     print 'General error happened'
    #
    # username = 'rfdickerson'
    # correct_pass = 'awesome'
    # incorrect_pass = 'scaley'
    # print 'Logging in {} with {}'.format(username, correct_pass)
    # print 'Result: {} '.format( dao.check_password(username, correct_pass ))
    #
    # print 'Logging in {} with {}'.format(username, incorrect_pass)
    # print 'Result: {} '.format( dao.check_password(username, incorrect_pass ))
    #
    # game_id = dao.create_game('rfdickerson', 'TheGame')
    # print "game_id {}".format(game_id)
    # game_id = dao.create_game('rfdickerson', 'TheGame')
    # print "game_id {}".format(game_id)
    # game_id = dao.create_game('oliver', 'TheGame')
    # print "game_id {}".format(game_id)
    # dao.create_game('oliver', 'AnotherGame')
    #
    # dao.join_game('oliver', 1)
    # dao.join_game('rfdickerson', 1)
    # dao.join_game('vanhelsing', 1)
    # print(dao.join_game('rfdickerson', 3))
    #
    # print(dao.delete_game('oliver',2))
    # print(dao.delete_game('oliver',2))
    # dao.create_game('vanhelsing', 'TheGame')
    #
    # print "Adding some items..."
    # dao.add_item('rfdickerson', 'Silver Knife')
    # dao.add_item('rfdickerson', 'Blunderbuss')
    # dao.add_item('rfdickerson', 'Blunderbuss')
    # dao.add_item('rfdickerson', 'Blunderbuss')
    # dao.add_item('oliver', 'Blunderbuss')
    # dao.remove_item('rfdickerson', 'Blunderbuss')
    #
    # print
    # print 'rfdickerson items'
    # print '--------------------------------'
    # items = dao.get_items("rfdickerson")
    # for item in items:
    #     print item["name"] + "\t" + str(item["quantity"])
    # print
    #
    # # location stuff
    # dao.set_location('rfdickerson', 30.25, 97.75)
    # dao.set_location('oliver', 30.3, 97.76)
    # dao.set_location('vanhelsing', 30.2, 97.7)
    # loc = dao.get_location('rfdickerson')
    # loc2 = dao.get_location('oliver')
    # print "rfdickerson at {}, {}".format(loc["lat"], loc["lng"])
    # print "oliver at {}, {}".format(loc2["lat"], loc2["lng"])
    #
    # dao.award_achievement('rfdickerson', 'Children of the moon')
    # dao.award_achievement('rfdickerson', 'A hairy situation')
    # achievements = dao.get_achievements("rfdickerson")
    #
    # print
    # print 'rfdickerson\'s achievements'
    # print '--------------------------------'
    # for a in achievements:
    #     print "{} ({}) - {}".format(a["name"],a["description"],a["created_at"].strftime('%a, %H:%M'))
    # print
    #
    # nearby = dao.get_alive_nearby('rfdickerson', 1, 0.00000000001)
    # for p in nearby:
    #     print "{}".format(p["player_id"])

    # dao.vote(game_id, 'rfdickerson', 'oliver')
    # dao.vote(game_id, 'oliver', 'vanhelsing')
    # dao.vote(game_id, 'vanhelsing', 'oliver')
    #
    # print 'Players in game 1 are'
    # pprint(dao.get_players(1))
    #
    # dao.set_dead('rfdickerson')
    #
    # print 'Games are'
    # pprint(dao.get_games())
    #
    # print 'Leaving a game'
    # dao.quit_game("oliver")
    #
    # print 'Game Info'
    # pprint(dao.game_info(1))
    #
    # print 'Leave a game'
    # dao.leave_game(1,1)
    # dao.leave_game(1)
    # print 'User Stats'
    # pprint(dao.get_user_stats('rfdickerson'))
    #
    # print 'Player Stats'
    # pprint(dao.get_player_stats('rfdickerson'))

    # print 'get current player of a user'
    # print(dao.get_current_player('rfdickerson'))
    print(dao.game_info(1))
    a = dao.game_info(1)
    print a['daybreak']
    print(type(a['daybreak']))
