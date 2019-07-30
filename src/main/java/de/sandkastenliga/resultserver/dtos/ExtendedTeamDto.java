package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.tools.projector.core.Projection;
import de.sandkastenliga.tools.projector.core.ProjectionType;

import java.util.ArrayList;
import java.util.List;

public class ExtendedTeamDto extends TeamDto {

    List<StrengthSnapshotDto> strengthSnapshots = new ArrayList<>();

    public List<StrengthSnapshotDto> getStrengthSnapshots() {
        return strengthSnapshots;
    }

    @Projection(value = ProjectionType.none)
    public void setStrengthSnapshots(List<StrengthSnapshotDto> strengthSnapshots) {
        this.strengthSnapshots = strengthSnapshots;
    }
}
