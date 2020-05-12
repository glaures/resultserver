package de.sandkastenliga.resultserver.dtos;

public class RankDto {

    private int rank;
    private String teamId;
    private String team;
    private int points;

    public RankDto(int rank, String teamId, String team, int points) {
        this.rank = rank;
        this.teamId = teamId;
        this.team = team;
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
