package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchState;

public class MatchInfoStruct {

    public String challenge;
    public String region;
    public String team1;
    public String team2;
    public int goalsTeam1;
    public int goalsTeam2;
    public MatchState state;
    public String dateString;
    public String timeString;

    public MatchInfoStruct(String region, String challenge, String team1, String team2, MatchState state, int goalsTeam1, int goalsTeam2) {
        this.challenge = challenge;
        this.team1 = team1;
        this.region = region;
        this.team2 = team2;
        this.state = state;
        this.goalsTeam1 = goalsTeam1;
        this.goalsTeam2 = goalsTeam2;
    }

    public MatchInfoStruct(String region, String challenge, String team1, String team2, MatchState state, int goalsTeam1, int goalsTeam2, String date, String time) {
        this(region, challenge, team1, team2, state, goalsTeam1, goalsTeam2);
        this.dateString = date;
        this.timeString = time;
    }

    @Override
    public String toString() {
        return "MatchInfoStruct{" +
                "challenge='" + challenge + '\'' +
                ", region='" + region + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", goalsTeam1=" + goalsTeam1 +
                ", goalsTeam2=" + goalsTeam2 +
                ", state=" + state +
                ", dateString='" + dateString + '\'' +
                ", timeString='" + timeString + '\'' +
                '}';
    }
}
