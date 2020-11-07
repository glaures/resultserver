package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.TeamStrengthValue;
import lombok.Data;

@Data
public class TeamStrengthValueDto {

    long id;
    float value;
    TeamDto team;

    public TeamStrengthValueDto() {
    }

    public TeamStrengthValueDto(TeamStrengthValue tsq) {
        this.id = tsq.getId();
        this.value = tsq.getValue();
        this.team = new TeamDto(tsq.getTeam());
    }

}
