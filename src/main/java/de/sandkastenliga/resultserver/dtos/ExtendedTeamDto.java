package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;

import java.util.ArrayList;
import java.util.List;

public class ExtendedTeamDto extends TeamDto {

    private List<TeamStrengthSnapshotDto> strengthSnapshots = new ArrayList<TeamStrengthSnapshotDto>();

    public List<TeamStrengthSnapshotDto> getTeamStrengthSnapshots() {
        return strengthSnapshots;
    }

    @Projection(value = ProjectionType.none)
    public void setTeamStrengthSnapshots(List<TeamStrengthSnapshotDto> strengthSnapshots) {
        this.strengthSnapshots = strengthSnapshots;
    }
}
