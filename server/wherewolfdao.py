# Wherewolf game DAO
# Abstraction for the SQL database access.

import psycopg2
import md5
import collections
import operator
from datetime import datetime


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
    # def __init__(self, dbname='wherewolf', pgusername='postgres', pgpasswd='121314'):

    def __init__(self, host='wherewolf.cmhlzluxo6vv.us-west-2.rds.amazonaws.com', dbname='wherewolf', pgusername='daybreaklee', pgpasswd='12131415'):
        self.dbname = dbname
        self.host = host
        self.pgusername = pgusername
        self.pgpasswd = pgpasswd
        print ('connection to database {}, user: {}, password: {}'.format(dbname, pgusername, pgpasswd))

    def get_db(self):
        # return psycopg2.connect(database=self.dbname,user=self.pgusername,password=self.pgpasswd)
        return psycopg2.connect(host=self.host,database=self.dbname,user=self.pgusername,password=self.pgpasswd)

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

    def get_alive_nearby(self, username, game_id, radius):
        ''' returns all alive players near a player '''
        conn = self.get_db()
        result = []
        with conn:
            c = conn.cursor()
            sql_location = ('select lat, lng from player, gameuser where '
                           'player.player_id = gameuser.current_player '
                           'and gameuser.username=%s')
            c.execute(sql_location, (username,))
            location = c.fetchone()

            if location == None:
                return result

            # using the radius for lookups now, just show alive villages who is in radius
            sql = ('select player_id, '
                   'earth_distance( ll_to_earth(player.lat, player.lng), '
                   'll_to_earth(%s,%s) ) '
                   'from player where '
                   'earth_box(ll_to_earth(%s,%s),%s) '
                   '@> ll_to_earth(player.lat, player.lng) '
                   'and game_id=%s '
                   'and is_werewolf = 0 '
                   'and is_dead = 0')

            c.execute(sql, (location[0], location[1],
                            location[0], location[1],
                            radius, game_id))
            for row in c.fetchall():
                d = {}
                d["player_id"] = row[0]
                d["distance"] = row[1]
                #d["distance"] = row[1]
                #d["is_werewolf"] = row[2]
                result.append(d)
        return result

    def get_landmark_nearby(self, lat, lng, game_id):   #tested
        ''' returns all alive players near a player '''
        conn = self.get_db()
        result = []
        with conn:
            c = conn.cursor()
            # using the radius for lookups now, just show alive villages who is in radius
            sql = ('select landmark_id, type '
                   'from landmark where '
                   'earth_box(ll_to_earth(%s,%s),landmark.radius) '
                   '@> ll_to_earth(landmark.lat, landmark.lng) '
                   'and game_id=%s '
                   'and is_active = 1')
            c.execute(sql, (lat, lng, game_id))
            for row in c.fetchall():
                d = {}
                d["landmark_id"] = row[0]
                d["type"] = row[1]
                result.append(d)
        return result

    def add_treasure(self, player_id, landmark_id):
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            sql = ('select item_id, quantity from treasure where landmark_id=%s ')
            cur.execute(sql, (landmark_id, ))
            row = cur.fetchone()
            item_id = row[0]
            quantity = row[1]

            sqlupdate = ('UPDATE inventory SET quantity=quantity+%s '
                         'where itemid=%s and playerid=%s; ')
            sqlinsert = ('INSERT INTO inventory (playerid, itemid, quantity) '
                         'SELECT %s, %s, %s where not exists (select 1 from inventory where playerid=%s and itemid=%s)')
            cur.execute(sqlupdate + sqlinsert, (quantity, item_id, player_id, player_id, item_id, quantity, player_id, item_id))
            conn.commit()

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

    def set_user_achievements(self, user_id, achievement_id):
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            sqlinsert = ('INSERT INTO user_achievement (user_id, achievement_id) '
                         'SELECT %s, %s where not exists (select 1 from user_achievement where user_id=%s and achievement_id=%s)')
            cur.execute(sqlinsert, (user_id, achievement_id, user_id, achievement_id))
            conn.commit()

    def set_dead(self, player_id): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update player set is_dead=1 where player_id=%s')
            cur.execute(cmd, (player_id,))
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
            current_player_stats = cur.fetchone()
            if type(current_player_stats).__name__ != 'NoneType':
                currentplayer["playerid"] = current_player_stats[0]
                currentplayer["is_dead"] = current_player_stats[1]
                currentplayer["lat"] = float(current_player_stats[2])
                currentplayer["lng"] = float(current_player_stats[3])
                currentplayer["is_werewolf"] = current_player_stats[4]
                return currentplayer
            else:
                return None

        if type(current_game_id).__name__ != 'NoneType':
                current_game_id = current_game_id[0]

    def get_player_current_game_id(self, player_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('select game_id from player where player_id=%s ')
            cur.execute(cmd, (player_id,))
            current_game_id = cur.fetchone()
            if type(current_game_id).__name__ != 'NoneType':
                current_game_id = current_game_id[0]
            return current_game_id

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

    def set_player_stats(self, playerid, name='Kill', value=1):  # tested
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            if name == 'Kill':
                sqlupdate = ('UPDATE player_stat SET stat_value=stat_value+%s '
                             'where player_id=%s and stat_name=%s; ')
                sqlinsert = ('INSERT INTO player_stat (player_id, stat_name, stat_value) '
                             'SELECT %s, %s, %s where not exists (select 1 from player_stat where player_id=%s and stat_name=%s)')
                cur.execute(sqlupdate + sqlinsert, (value, playerid, name, playerid, name, value, playerid, name))
                conn.commit()
            elif name == 'CoolDown':

                sqlupdate = ('UPDATE player_stat SET stat_time=now() '
                             'where player_id=%s and stat_name=%s; ')
                sqlinsert = ('INSERT INTO player_stat (player_id, stat_name, stat_time) '
                             'SELECT %s, %s, now() where not exists (select 1 from player_stat where player_id=%s and stat_name=%s)')
                cur.execute(sqlupdate + sqlinsert, ( playerid, name, playerid, name, playerid, name))
                conn.commit()
            elif name == 'Protected':
                sqlinsert = ('INSERT INTO player_stat (player_id, stat_name, stat_value) '
                             'SELECT %s, %s, 1 where not exists (select 1 from player_stat where player_id=%s and stat_name=%s and stat_value=1)')
                cur.execute(sqlinsert, ( playerid, name, playerid, name))
                conn.commit()

    def delete_save_zone_protection(self, player_id, stat_name = "Protected"):
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            sql = ('delete from player_stat where player_id=%s and stat_name=%s')
            cur.execute(sql, (player_id, stat_name))
            conn.commit()

    def checking_ifProtected(self, player_id):
        conn = self.get_db()
        with conn:
            cur=conn.cursor()
            sql = ("select count(*) from player_stat where player_id=%s and stat_name='Protected'")
            cur.execute(sql, (player_id,))
            result = cur.fetchone()[0]
            return result == 1

    def get_player_stats(self,playerid, name='Kill'):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('SELECT stat_value, stat_time from player_stat '
                   ' where player_id=%s and stat_name=%s')
            cur.execute(sql, (playerid,name))
            row = cur.fetchone()
            if row != None:
                pStats = {"stat_value": row[0], "stat_time": row[1]}
                return pStats
            else:
                return None

    def get_userID(self, player_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('SELECT user_id from gameuser '
                   ' where current_player=%s')
            cur.execute(sql, (player_id,))
            row = cur.fetchone()
            if row != None:
                return row[0]
            else:
                return None

    def set_werewolf(self, game_id, werewolf_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update player set is_werewolf=1 where game_id=%s and player_id=%s')
            cur.execute(cmd, (game_id,werewolf_id))
            conn.commit()

    def get_player_names(self, gameid):
        conn = self.get_db()
        players = []
        with conn:
            cur = conn.cursor()
            cmd = ('select gameuser.username, gameuser.current_player from gameuser, '
                    'player where gameuser.current_player=player.player_id and '
                    'player.game_id=%s')
            cur.execute(cmd, (gameid,))
            for row in cur.fetchall():
                p = {}
                p["playername"] = row[0]
                p["playerid"] = row[1]
                players.append(p)
        return players

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
                # return true for now
                # cmd2 = ('update gameuser set current_player=%s where username=%s')
                # cur.execute(cmd2, (cur.fetchone()[0], username));
                # conn.commit()
                return True
                # return False
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

            sql_inventory = ('delete from inventory using player where '
                    'inventory.playerid=player.player_id and player.game_id=%s')
            cur.execute(sql_inventory, (game_id,))

            sql_player_stat = ('delete from player_stat using player where '
                    'player_stat.player_id=player.player_id and player.game_id=%s')
            cur.execute(sql_player_stat, (game_id,))

            sql_treasure = ('delete from treasure using landmark where '
                    'treasure.landmark_id=landmark.landmark_id and landmark.game_id=%s')
            cur.execute(sql_treasure, (game_id,))

            sql_vote = ('delete from vote where game_id=%s')
            cur.execute(sql_vote, (game_id,))

            sql_landmark = ('delete from landmark where game_id=%s')
            cur.execute(sql_landmark, (game_id,))

            if player_id == None:
                cmd = '''UPDATE player set game_id = null where game_id=%s'''
                cur.execute(cmd, (game_id,))

            else:
                cmd = '''UPDATE player set game_id = null where game_id=%s and player_id=%s'''
                cur.execute(cmd, (game_id, player_id))
                # conn.commit()

            sql_player = ('delete from player where game_id=%s')
            cur.execute(sql_player, (game_id,))

            sql_game = ('delete from game where game_id=%s')
            cur.execute(sql_game, (game_id,))

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

    def get_games(self):
        conn = self.get_db()
        with conn:
            allGames = []
            cur = conn.cursor()
            cmd = ("SELECT game.game_id, game.name, game.status, gameuser.username from game, gameuser where game.admin_id = gameuser.user_id")
            cur.execute(cmd)
            for row in cur.fetchall():
                gameEntry = {}
                gameEntry["game_id"] = row[0]
                gameEntry["name"] = row[1]
                gameEntry["status"] = row[2]
                gameEntry["admin_name"] = row[3]
                allGames.append(gameEntry)
        return allGames

    def set_game_status(self, username, game_id, status): #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE game set status=%s '
                   'where game_id=%s and admin_id=(select user_id from gameuser where username=%s)')
            cur.execute(cmd, (status, game_id, username))

    def set_game_current_time(self, username, game_id, current_time):  # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE game set currenttime=%s '
                   'where game_id=%s and admin_id=(select user_id from gameuser where username=%s)')
            cur.execute(cmd, (current_time, game_id, username))

    def vote(self, game_id, player_id, target_id): # tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('insert into vote '
                   '(game_id, player_id, target_id, cast_date) '
                   'values ( %s, %s, %s, '
                   ' now())')
            cur.execute(sql, (game_id, player_id, target_id))
            conn.commit()

    def get_vote_stats(self, game_id):   #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql =('Select target_id, count(target_id) from vote '
                  'where game_id=%s '
                  'group by (target_id)')
            cur.execute(sql, (game_id,))
            rows = cur.fetchall()
            if isinstance(rows, collections.Iterable):
                results = [{'playerid': row[0], 'votes': int(row[1])} for row in rows]
            else:
                results = []
            # # clear all votes info after pull over the stats
            # cur.execute('truncate vote Restart Identity cascade')
            return (results)

    def assign_lupus_and_clear_votes_table(self, game_id, death_player_id, stat_name='lupus', stat_value=1):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql =('Select player_id from vote '
                  'where game_id=%s and target_id=%s ')
            cur.execute(sql, (game_id, death_player_id))
            rows = cur.fetchall()

            # sqlinsert = ('INSERT INTO inventory (playerid, itemid, quantity) '
            #              'SELECT %s, %s, %s where not exists (select 1 from inventory where playerid=%s and itemid=%s)')

            for player in rows:
                sql = ('Insert Into player_stat (player_id, stat_name, stat_value)'
                       'SELECT %s, %s, %s where not exists (select 1 from player_stat where player_id=%s and stat_name=%s)')
                cur.execute(sql, (player[0], stat_name, stat_value, player[0], stat_name))
            # clear all votes info after pull over the stats
            cur.execute('truncate vote Restart Identity cascade')
            conn.commit()

    def assign_achievement(self):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql =('Select player_id, stat_name, stat_value from player_stat ')
            cur.execute(sql)
            rows = cur.fetchall()
            kill_results = {}
            lupus_results = []
            for ele in rows:
                if ele[1] == 'Kill':
                    kill_results[ele[0]] = ele[2]
                elif ele[1] == 'lupus':
                    lupus_results.append(ele[0])
                else:
                    pass
            max_killer_number = max(kill_results.iteritems(), key=operator.itemgetter(1))[1]
            max_killer_list = []
            for key in kill_results:
                if kill_results[key] == max_killer_number:
                    max_killer_list.append(key)

            for ele in max_killer_list:
                sql =('Insert into user_achievement (user_id, achievement_id) '
                      'select (select user_id from gameuser where current_player=%s) as uID, '
                      '2'
                )
                cur.execute(sql,(ele,))
            for ele in lupus_results:
                sql =('Insert into user_achievement (user_id, achievement_id) '
                      'select (select user_id from gameuser where current_player=%s) as uID, '
                      '4'
                )
                cur.execute(sql,(ele,))
            conn.commit()

    def get_all_achievement(self):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql =('Select gameuser.username, achievement.name '
                  'from user_achievement inner join achievement on user_achievement.achievement_id=achievement.achievement_id '
                  'inner join gameuser on user_achievement.user_id=gameuser.user_id ')
            cur.execute(sql)
            rows = cur.fetchall()
            if rows!= None:
                return rows
            else:
                return []

    def get_all_players_status(self, game_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql =('Select is_werewolf, count(is_dead) from player '
                  'where game_id=%s and is_dead=0'
                  'group by (is_werewolf) '
                  'order by is_werewolf DESC')
            cur.execute(sql, (game_id,))
            row = cur.fetchall()
            results = {}
            for ele in row:
                if ele[0] == 1:
                    results['alive_werewolf'] = int(ele[1])
                if ele[0] == 0:
                    results['alive_villager'] = int(ele[1])
            return (results)

    def set_landmark(self, game_id, lat, lng, radius, landmark_type, is_active = 1):  #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql = ('insert into landmark '
                   '(lat, lng, radius, type, game_id, is_active) '
                   'values ( %s, %s, %s, %s, %s, %s)')
            cur.execute(sql, (lat, lng, radius, landmark_type, game_id, is_active))
            conn.commit()

    def set_treasure(self, landmark_id, item_id, quantity):  #tested
        conn = self.get_db()
        with conn:
            cur = conn.cursor()

            sql = ('select type from landmark where landmark_id=%s' )
            cur.execute(sql, (landmark_id,))

            landmark_type = cur.fetchone()
            if landmark_type != None:
                typeChecker = landmark_type[0]

                if typeChecker == 1:  # do not add treasure to save zone
                    sql = ('insert into treasure '
                           '(landmark_id, item_id, quantity) '
                           'values ( %s, %s, %s)')
                    cur.execute(sql, (landmark_id, item_id, quantity))
                    conn.commit()

    def clear_tables(self):  # tested
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('truncate gameuser RESTART IDENTITY cascade')
            c.execute('truncate player RESTART IDENTITY cascade')
            c.execute('truncate game RESTART IDENTITY cascade')
            c.execute('truncate user_achievement RESTART IDENTITY cascade')
            c.execute('truncate landmark RESTART IDENTITY cascade')
            c.execute('truncate user_achievement RESTART IDENTITY cascade')
            c.execute('truncate treasure RESTART IDENTITY cascade')
            c.execute('truncate inventory RESTART IDENTITY cascade')
            c.execute('truncate player_stat RESTART IDENTITY cascade')
            c.execute('truncate user_stat RESTART IDENTITY cascade')
            c.execute('truncate vote RESTART IDENTITY cascade')
            c.execute('truncate game RESTART IDENTITY cascade')
            conn.commit()

    def clean_landmark_treasure(self):  # tested
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('truncate landmark RESTART IDENTITY cascade')
            c.execute('truncate treasure RESTART IDENTITY cascade')
            conn.commit()

#---------------------------Testing Each Functions used in WherewolfDao.py--------------------
# if __name__ == "__main__":
#     dao = WherewolfDao() # default password is '121314', change it if neeeded.
#     # dao.clear_tables()#clear gameuser, player and user_achievement table
#     # try:
#     #     dao.create_user('rfdickerson', 'awesome', 'Robert', 'Dickerson')
#     #     dao.create_user('oliver','furry','Oliver','Cat')
#     #     dao.create_user('vanhelsing', 'van', 'Van', 'Helsing')
#     #     print 'Created new players!'
#     # except UserAlreadyExistsException as e:
#     #     print e
#     # except Exception:
#     #     print 'General error happened'
#     #
#     # username = 'rfdickerson'
#     # correct_pass = 'awesome'
#     # incorrect_pass = 'scaley'
#     # print 'Logging in {} with {}'.format(username, correct_pass)
#     # print 'Result: {} '.format( dao.check_password(username, correct_pass ))
#     #
#     # print 'Logging in {} with {}'.format(username, incorrect_pass)
#     # print 'Result: {} '.format( dao.check_password(username, incorrect_pass ))
#     #
#     # game_id = dao.create_game('rfdickerson', 'TheGame')
#     # print "game_id {}".format(game_id)
#     # game_id = dao.create_game('rfdickerson', 'TheGame')
#     # print "game_id {}".format(game_id)
#     # game_id = dao.create_game('oliver', 'TheGame')
#     # print "game_id {}".format(game_id)
#     # dao.create_game('oliver', 'AnotherGame')
#     #
#     # dao.join_game('oliver', 1)
#     # dao.join_game('rfdickerson', 1)
#     # dao.join_game('vanhelsing', 1)
#     # print(dao.join_game('rfdickerson', 3))
#     #
#     # print(dao.delete_game('oliver',2))
#     # print(dao.delete_game('oliver',2))
#     # dao.create_game('vanhelsing', 'TheGame')
#     #
#     # print "Adding some items..."
#     # dao.add_item('rfdickerson', 'Silver Knife')
#     # dao.add_item('rfdickerson', 'Blunderbuss')
#     # dao.add_item('rfdickerson', 'Blunderbuss')
#     # dao.add_item('rfdickerson', 'Blunderbuss')
#     # dao.add_item('oliver', 'Blunderbuss')
#     # dao.remove_item('rfdickerson', 'Blunderbuss')
#     #
#     # print
#     # print 'rfdickerson items'
#     # print '--------------------------------'
#     # items = dao.get_items("rfdickerson")
#     # for item in items:
#     #     print item["name"] + "\t" + str(item["quantity"])
#     # print
#     #
#     # # location stuff
#     # dao.set_location('rfdickerson', 30.25, 97.75)
#     # dao.set_location('oliver', 30.3, 97.76)
#     # dao.set_location('vanhelsing', 30.2, 97.7)
#     # loc = dao.get_location('rfdickerson')
#     # loc2 = dao.get_location('oliver')
#     # print "rfdickerson at {}, {}".format(loc["lat"], loc["lng"])
#     # print "oliver at {}, {}".format(loc2["lat"], loc2["lng"])
#     #
#     # dao.award_achievement('rfdickerson', 'Children of the moon')
#     # dao.award_achievement('rfdickerson', 'A hairy situation')
#     # achievements = dao.get_achievements("rfdickerson")
#     #
#     # print
#     # print 'rfdickerson\'s achievements'
#     # print '--------------------------------'
#     # for a in achievements:
#     #     print "{} ({}) - {}".format(a["name"],a["description"],a["created_at"].strftime('%a, %H:%M'))
#     # print
#     #
#     # nearby = dao.get_alive_nearby('rfdickerson', 1, 700000)
#     # print ('Nearby players: ')
#     # for p in nearby:
#     #     print "{} is {} meters away".format(p["player_id"],p["distance"])
#     #
#     # landmark_nearby = dao.get_landmark_nearby(30, 97, 1)
#     # print ('Nearby landmark: ')
#     # for p in landmark_nearby:
#     #     print "id: {}, type: {}".format(p["landmark_id"],p["type"])
#     #
#     # dao.add_treasure(2,1)
#
#     # dao.vote(1, 3, 2)
#     # dao.get_vote_stats(1)
#     #
#     # print 'Players in game 1 are'
#     # pprint(dao.get_players(1))
#     #
#     # dao.set_dead('rfdickerson')
#     #
#     # print 'Games are'
#     # pprint(dao.get_games())
#     #
#     # print 'Leaving a game'
#     # dao.quit_game("oliver")
#     #
#     # print 'Game Info'
#     # pprint(dao.game_info(1))
#     #
#     # print 'Leave a game'
#     # dao.leave_game(1,1)
#     # dao.leave_game(1)
#     # print 'User Stats'
#     # pprint(dao.get_user_stats('rfdickerson'))
#     #
#     # print 'Player Stats'
#     # pprint(dao.get_player_stats('rfdickerson'))
#
#     # print 'get current player of a user'
#     # print(dao.get_current_player('safa'))
#
#     # print 'game info of a game'
#     # print(dao.game_info(1))
#     #
#     # print 'current game_id of a player'
#     # print(dao.get_player_current_game_id(0))
#
#     # print set player_stats
#     # dao.set_player_stats(2, "Protected")
#     # dao.set_player_stats(2, 'CoolDown')
#
#     # print "check CoolDown"
#     # print(dao.get_player_stats(2, 'CoolDown'))
#
#     # print set user_achievements
#     # dao.set_user_achievements(1,1)
#     # dao.set_user_achievements(1,1)
#     # dao.set_user_achievements(1,2)
#
#     # print get userId by playerid:
#     # print dao.get_userID(1)
#
#     # print set game status
#     # dao.set_game_status('michael',1,1)
#
#     # print "set game current time"
#     # dao.set_game_current_time('michael',1,'09:00:00')
#
#     # print "set werewolf"
#     # dao.set_werewolf(1,2)
#
#     # print "set treasure"
#     # dao.set_treasure(1,1,1)
#
#     # print 'get all players status'
#     # dao.get_all_players_status(1)
#     #
#     # print 'assign_lupus_and_clear_votes_table'
#     # dao.assign_lupus_and_clear_votes_table(1,1)
#
#     # print 'assigns assign_achievement'
#     # dao.assign_achievement()
#
#     # print 'get all assigned achievement from last game'
#     # dao.get_all_achievement()
#
#     # print 'delete save zone protection'
#     # dao.delete_save_zone_protection(2)
#
#     #print "checking if a player is protected"
#     dao.checking_ifProtected(2)

