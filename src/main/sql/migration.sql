alter table challenge change `rankUrl` `rank_url` varchar(255);
alter table challenge change `rankUrlDate` `rank_url_date` datetime;
alter table challenge change `challengeMode` `challenge_mode` int(11);
alter table rmatch change `goalsTeam1` `goals_team1` int(11);
alter table rmatch change `goalsTeam2` `goals_team2` int(11);
alter table rmatch change `posTeam1` `pos_team1` int(11);
alter table rmatch change `posTeam2` `pos_team2` int(11);
alter table team change `mainChallengePos` `main_challenge_pos` int(11);
alter table team change `mainChallenge_id` `main_challenge_id` int(11);
ALTER TABLE rmatch ADD COLUMN help bit(1);
update rmatch set help=true where featured=1;
update rmatch set help=false where featured=0;
alter table rmatch drop column `featured`;
alter table rmatch change `help` `featured` bit(1);
update hibernate_sequence set next_val=(select (max(id) + 1) from rmatch);

