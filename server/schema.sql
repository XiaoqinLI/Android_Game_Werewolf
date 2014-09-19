-- turn off the foreign keys so we can drop tables without 
-- it complaining
.header ON
.mode column
PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
	userid 		INTEGER PRIMARY KEY AUTOINCREMENT,
	firstname	TEXT NOT NULL,
	lastname	TEXT NOT NULL,
	created_at	DATE DEFAULT CURRENT_TIMESTAMP,
	username	TEXT UNIQUE NOT NULL,
	password	TEXT NOT NULL,
	current_player	INTEGER DEFAULT 1
);

DROP TABLE IF EXISTS player;
CREATE TABLE player (
	playerid	INTEGER PRIMARY KEY AUTOINCREMENT,
  userid		INTEGER NOT NULL,
  is_dead		INTEGER NOT NULL,
  lat			REAL	NOT NULL,
  lng			REAL	NOT NULL,
	is_werewolf	INTEGER NOT NULL DEFAULT 0,
	num_gold	INTEGER NOT NULL DEFAULT 0,
	game_id		INTEGER REFERENCES game
);

--adding point of interests and related table here
Drop TABLE IF EXISTS points_of_interest;
CREATE TABLE points_of_interest(
	pointid  INTEGER PRIMARY KEY AUTOINCREMENT,
	pointname  TEXT NOT NULL,
	lat			REAL	NOT NULL,
  lng			REAL	NOT NULL,
  radius   REAL  NOT NULL,
  pointattribute  INTEGER UNSIGNED NOT NULL DEFAULT 0 REFERENCES treature(treatureid)  -- 0 is saft place, positive int is a treature
);

Drop TABLE IF EXISTS treature;
CREATE TABLE treature(
	treatureid  INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1,
	treaturename  TEXT NOT NULL
);

DROP TABLE IF EXISTS treature_inventory;
CREATE TABLE treature_inventory (
	treatureid 	INTEGER REFERENCES treature,
	itemid 		INTEGER REFERENCES item,
	quantity 	INTEGER UNSIGNED NOT NULL,
	primary key (treatureid, itemid)
);
---------------------------------------------------

DROP TABLE IF EXISTS playerlook;
CREATE TABLE playerlook (
	playerlookid 	INTEGER PRIMARY KEY AUTOINCREMENT,
	playerid		INTEGER NOT NULL,
	picture			TEXT NOT NULL
);

DROP TABLE IF EXISTS game;
CREATE TABLE game (
	gameid 	INTEGER PRIMARY KEY AUTOINCREMENT,
	adminid INTEGER NOT NULL REFERENCES user,
	status 	INTEGER NOT NULL DEFAULT 0,
	name	TEXT NOT NULL
);


DROP TABLE IF EXISTS achievement;
CREATE TABLE achievement (
	achievementid	INTEGER PRIMARY KEY AUTOINCREMENT,
	name			TEXT NOT NULL,
	description		TEXT NOT NULL
);

DROP TABLE IF EXISTS user_achievement;
CREATE TABLE user_achievement (
	userid			INTEGER NOT NULL REFERENCES user,
	achievementid	INTEGER NOT NULL REFERENCES achievement,
	created_at		DATE DEFAULT CURRENT_TIMESTAMP NOT NULL,
	primary key(userid,achievementid)
);

DROP TABLE IF EXISTS item;
CREATE TABLE item (
	itemid 		INTEGER PRIMARY KEY AUTOINCREMENT,
	name 		TEXT NOT NULL,
	description TEXT
);

DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
	playerid 	INTEGER REFERENCES user,
	itemid 		INTEGER REFERENCES item,
	quantity 	INTEGER UNSIGNED,
	primary key (playerid, itemid)
);

-- used to store number of kills in a game --
DROP TABLE IF EXISTS player_stat;
CREATE TABLE player_stat (
	userid    INTEGER NOT NULL REFERENCES user,
	playerid 	INTEGER NOT NULL REFERENCES player,
	numKills	INTEGER
);

-- used to store number of kills historically
DROP TABLE IF EXISTS user_stat;
CREATE TABLE user_stat (
	userid 		INTEGER NOT NULL,
	statName	TEXT NOT NULL
);

-- creates a cascade delete so that all inventory items for the player
-- are automatically deleted

CREATE TRIGGER delete_inventory
BEFORE DELETE ON player
for each row
begin
	delete from inventory where playerid = 	old.playerid;
END;

CREATE INDEX playerindex ON inventory(playerid);
-- insert some data

PRAGMA foreign_keys = ON;

INSERT INTO user (userid, firstname, lastname, created_at, username, password) VALUES (1, 'Robert', 'Dickerson', '2014-08-30', 'rfdickerson', 'f96af09d8bd35393a14c456e2ab990b6');
INSERT INTO user (userid, firstname, lastname, created_at, username, password) VALUES (2, 'Abraham', 'Van Helsing', '2014-08-30', 'vanhelsing', 'be121740bf988b2225a313fa1f107ca1');

INSERT INTO player (playerid, userid, is_dead, lat, lng) VALUES (1, 1, 1, 38, 78);
INSERT INTO player (playerid, userid, is_dead, lat, lng) VALUES (2, 2, 1, 37, 76);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 38, 79);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 39, 81);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 41, 77);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 41, 79);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 42, 82);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 100, 100);
INSERT INTO player (userid, is_dead, lat, lng) VALUES (2, 1, 90, 90);

INSERT INTO game (gameid, adminid, status, name) VALUES (1, 1, 1, 'game1');
UPDATE player set game_id = 1 where userid = 1 and playerid = 1;
UPDATE player set game_id = 1 where userid = 2 and playerid = 2;

INSERT INTO achievement VALUES (1, 'Hair of the dog', 'Survive an attack by a werewolf');
INSERT INTO achievement VALUES (2, 'Top of the pack', 'Finish the game as a werewolf and receive the top number of kills');
INSERT INTO achievement VALUES (3, 'Children of the moon', 'Stay alive and win the game as a werewolf');
INSERT INTO achievement VALUES (4, 'It is never Lupus', 'Vote someone to be a werewolf, when they were a townsfolk');
INSERT INTO achievement VALUES (5, 'A hairy situation', 'Been near 3 werewolves at once.');
INSERT INTO achievement VALUES (6, 'Call in the Exterminators', 'Kill off all the werewolves in the game');
INSERT INTO achievement VALUES (7, 'AAA', 'AAA');
INSERT INTO achievement VALUES (8, 'BBB', 'BBB');
INSERT INTO achievement VALUES (9, 'CCC', 'CCC');
INSERT INTO achievement VALUES (10, 'DDD', 'DDD');
INSERT INTO achievement VALUES (11, 'EEE', 'EEE');
INSERT INTO achievement VALUES (12, 'FFF', 'FFF');
INSERT INTO achievement VALUES (13, 'GGG', 'GGG');
INSERT INTO achievement VALUES (14, 'HHH', 'HHH');
INSERT INTO achievement VALUES (15, 'III', 'III');
INSERT INTO achievement VALUES (16, 'JJJ', 'JJJ');
INSERT INTO achievement VALUES (17, 'KKK', 'KKK');

INSERT INTO user_achievement (userid, achievementid) VALUES (1, 1);

INSERT INTO item VALUES (1, 'Wolfsbane Potion', 'Protects the drinker from werewolf attacks');
INSERT INTO item VALUES (2, 'Blunderbuss', 'A muzzle-loading firearm with a short, large caliber barrel.');
INSERT INTO item VALUES (3, 'Invisibility Potion', 'Makes the imbiber invisible for a short period of time.');
INSERT INTO item VALUES (4, 'Silver Knife', 'A blade made from the purest of silvers');

INSERT INTO inventory VALUES (1, 2, 1);
INSERT INTO inventory VALUES (2, 1, 1);
INSERT INTO inventory VALUES (1, 3, 5);

INSERT INTO user_achievement (userid, achievementid) VALUES (1, 2);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 4);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 3);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 6);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 7);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 8);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 16);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 14);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 9);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 15);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 5);

INSERT INTO player_stat (userid, playerid, numKills) VALUES (1, 1, 20);

