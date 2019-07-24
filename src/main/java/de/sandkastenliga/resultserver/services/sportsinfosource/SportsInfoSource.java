package de.sandkastenliga.resultserver.services.sportsinfosource;

import de.sandkastenliga.resultserver.model.MatchInfo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface SportsInfoSource {

	public List<MatchInfo> getMatchInfoForDay(Date date) throws IOException;

	public List<String[]> getAllKoChallenges() throws IOException;

	public Map<String, Integer> getTeamRankings(String urlStr) throws IOException;
}
