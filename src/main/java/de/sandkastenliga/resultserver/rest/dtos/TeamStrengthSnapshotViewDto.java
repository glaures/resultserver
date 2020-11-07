package de.sandkastenliga.resultserver.rest.dtos;

import de.sandkastenliga.resultserver.dtos.TeamStrengthSettingsDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthSnapshotDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamStrengthSnapshotViewDto {

    TeamStrengthSettingsDto settings;
    List<TeamStrengthDto> teams = new ArrayList<>();
    List<TeamStrengthSnapshotDto> snapshots;

    public void addTeam(TeamStrengthDto t){
        this.teams.add(t);
    }
}
