package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.Rank;

public class RankDto {

    private int rank;
    private String teamId;
    private String team;
    private int points;

    public RankDto() {
    }

    public RankDto(Rank r) {
        this.rank = r.getRank();
        this.teamId = r.getTeam().getId();
        this.team = r.getTeam().getName();
        this.points = r.getPoints();
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
