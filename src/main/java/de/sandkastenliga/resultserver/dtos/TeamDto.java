package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.Team;
import lombok.Data;

@Data
public class TeamDto {

    private String id;
    private String name;
    private int currentStrength;

    public TeamDto(){}

    public TeamDto(Team t){
        this.id = t.getId();
        this.name = t.getName();
        this.currentStrength = t.getCurrentStrength();
    }
}
