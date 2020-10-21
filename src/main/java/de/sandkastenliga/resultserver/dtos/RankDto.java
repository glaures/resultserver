package de.sandkastenliga.resultserver.dtos;

import lombok.Data;

@Data
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

}
