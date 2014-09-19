-- address of my Bitbucket repository: https://bitbucket.org/daybreaklee/wherewolf

--every query has been tested on my local mechaine.

-- Return the top 5 players nearest a certain position if you know the lat, lng pair, assuming (40,80) is the known point
select * from player order by (lat-40)*(lat-40) + (lng-80)*(lng-80) ASC LIMIT 5

--Update the position of the player if you only know the username as input,  assuming (50,60) is the target position
UPDATE player set lat = 50, lng = 60 where userid IN (select userid from user where username = 'rfdickerson')

--Get the last 10 achievements a user has won if you only know the username as input, assuming username input is 'rfdickerson'
select A.achievementid, A.name, A.description, UA.created_at from achievement as A 
	join user_achievement as UA on A.achievementid = UA.achievementid
	join user as U on U.userid = UA.userid
	where username = 'rfdickerson'
	order by UA.created_at DESC
	limit 10

--Add an item to player's inventory by either creating a new record or incrementing the quantity
-- firstly, trying to update, if updated successfully (adding number to original value, not replace it), then insert will be ignore; otherwise insert will be implemented
-- assuming quantity is 5 and playerid is 1, itemid is 3
UPDATE inventory set quantity = quantity + 5 where playerid = 1 and itemid = 3;
INSERT OR IGNORE INTO inventory VALUES (1, 3, 5);

--Remove an item from a player's inventory by either decrementing the quantity or leaving the quantity set to zero
-- firstly, decreamenting any quantity as needed, then if quantity is negative, then set back to zero.
UPDATE inventory set quantity = quantity - 5 where playerid = 1 and itemid = 3;
UPDATE inventory set quantity = 0 where playerid = 1 and itemid = 3 and quantity < 0;