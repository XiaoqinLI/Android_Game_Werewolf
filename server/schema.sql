-- POSTgreSQL schema for the Wherewolf game

CREATE EXTENSION cube;
CREATE EXTENSION earthdistance;

DROP TABLE IF EXISTS gameuser cascade;
CREATE TABLE gameuser (
	user_id     serial primary key,
	firstname	varchar(80) NOT NULL,
	lastname	varchar(80) NOT NULL,
	created_at	timestamp DEFAULT CURRENT_TIMESTAMP,
	username	varchar(80) UNIQUE NOT NULL,
	password	varchar(128) NOT NULL,
	current_player  integer
);

DROP TABLE IF EXISTS game cascade;
CREATE TABLE game (
	game_id 	serial primary key,
	admin_id 	int NOT NULL REFERENCES gameuser,
	status 		int NOT NULL DEFAULT 0,
	name		varchar(80) NOT NULL,
	daybreak    time NOT NULL DEFAULT time '07:00:00',
	nightfall   time NOT NULL DEFAULT time '19:00:00',
	currenttime time DEFAULT CURRENT_TIME,
	description	text
);

DROP TABLE IF EXISTS player cascade;
CREATE TABLE player (
   	player_id	serial primary key,
   	is_dead		INTEGER NOT NULL,
  	lat		FLOAT	NOT NULL,
    lng		FLOAT	NOT NULL,
	is_werewolf	INTEGER NOT NULL DEFAULT 0,
	num_gold	INTEGER NOT NULL DEFAULT 0,
	game_id		INTEGER REFERENCES game
);

-----------create table for points of interest
DROP TABLE IF EXISTS landmark cascade;
CREATE TABLE landmark (
	landmark_id	serial primary key,
	lat		float NOT NULL,
	lng		float NOT NULL,
	radius		float NOT NULL,
	type		int NOT NULL,
	game_id		int NOT NULL REFERENCES game,
	is_active 	int NOT NULL DEFAULT 1,
	created_at	date
);

DROP TABLE IF EXISTS achievement cascade;
CREATE TABLE achievement (
	achievement_id	serial primary key,
	name		varchar(80) NOT NULL,
	description	text NOT NULL
);

DROP TABLE IF EXISTS user_achievement cascade;
CREATE TABLE user_achievement (
	user_id		INTEGER references gameuser,
	achievement_id	INTEGER references achievement,
	created_at	timestamp DEFAULT CURRENT_TIMESTAMP,
	primary key (user_id, achievement_id)
);

DROP TABLE IF EXISTS item cascade;
CREATE TABLE item (
	itemid 		serial primary key,
	name 		varchar(80) NOT NULL,
	description 	TEXT
);

DROP TABLE IF EXISTS inventory cascade;
CREATE TABLE inventory (
	playerid 	INTEGER REFERENCES player,
	itemid 		INTEGER REFERENCES item,
	quantity 	INTEGER,
	primary key (playerid, itemid)
);

DROP TABLE IF EXISTS treasure cascade;
CREATE TABLE treasure(
	landmark_id	INTEGER REFERENCES landmark,
	item_id	 	INTEGER references item,
	quantity  	INTEGER NOT NULL,
	primary key (landmark_id, item_id)
);

-- used to store number of kills in a game --
DROP TABLE IF EXISTS player_stat cascade;
CREATE TABLE player_stat (
	player_id 	INTEGER NOT NULL REFERENCES player,
	stat_name	varchar(80) NOT NULL,
	stat_value	INTEGER NOT NULL DEFAULT 0,
	stat_time	time DEFAULT CURRENT_TIME,
        primary key (player_id, stat_name)
);

-- used to store number of kills historically
DROP TABLE IF EXISTS user_stat cascade;
CREATE TABLE user_stat (
	user_id 	INTEGER NOT NULL,
	stat_name	varchar(80) NOT NULL,
	stat_value	varchar(80) NOT NULL,
        primary key (user_id, stat_name)
);

DROP TABLE IF EXISTS vote cascade;
CREATE TABLE vote (
       vote_id		serial primary key,
       game_id      integer references game,
       player_id    integer references player,
       target_id  	integer references player,
       cast_date	timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX playerindex ON inventory(playerid);
CREATE INDEX username ON gameuser(username);
CREATE INDEX indexitemname ON item(name);
-- adds an index so our lookups based on position will be exponentially faster
CREATE INDEX pos_index ON player USING gist (ll_to_earth(lat, lng));

-- insert default data
INSERT INTO achievement VALUES (1, 'Hair of the dog', 'Survive an attack by a werewolf');
INSERT INTO achievement VALUES (2, 'Leader of the Pack', 'have the most number of kills by the end of the game');
INSERT INTO achievement VALUES (3, 'Children of the moon', 'Stay alive and win the game as a werewolf');
INSERT INTO achievement VALUES (4, 'It is never Lupus', 'Vote someone to be a werewolf, when they were a townsfolk');
INSERT INTO achievement VALUES (5, 'A hairy situation', 'Been near 3 werewolves at once.');
INSERT INTO achievement VALUES (6, 'Call in the Exterminators', 'Kill off all the werewolves in the game');

INSERT INTO item VALUES (1, 'Invisibility potion', 'Makes a villager invisible for 10 minutes');
INSERT INTO item VALUES (2, 'Blunderbuss', 'A muzzle-loading firearm with a short, large caliber barrel.');
INSERT INTO item VALUES (3, 'Invisibility Potion', 'Makes the imbiber invisible for a short period of time.');
INSERT INTO item VALUES (4, 'Silver Knife', 'A blade made from the purest of silvers');
INSERT INTO item VALUES (5, 'Wolfsbane Potion', 'Protects the drinker from werewolf attacks');