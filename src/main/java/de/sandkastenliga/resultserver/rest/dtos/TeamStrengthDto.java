package de.sandkastenliga.resultserver.rest.dtos;

import de.sandkastenliga.resultserver.dtos.TeamDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamStrengthDto extends TeamDto {

    List<Float> values = new ArrayList<>();

    public TeamStrengthDto(){}

    public TeamStrengthDto(TeamDto t){
        setId(t.getId());
        setName(t.getName());
        setCurrentStrength(t.getCurrentStrength());
    }

    public void addValue(float v){
        this.values.add(v);
    }
}
