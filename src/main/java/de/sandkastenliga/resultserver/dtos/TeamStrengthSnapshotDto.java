package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.TeamStrengthValue;
import de.sandkastenliga.resultserver.model.TeamStrengthSnapshot;
import de.sandkastenliga.resultserver.rest.dtos.TeamStrengthDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamStrengthSnapshotDto {

    int id;
    Date snapshotDate;
    @Column(name = "_values")
    List<TeamStrengthValueDto> values = new ArrayList<>();
    ChallengeDto challenge;

    public TeamStrengthSnapshotDto(TeamStrengthSnapshot ss) {
        this.id = ss.getId();
        this.snapshotDate = ss.getSnapshotDate();
        this.challenge = new ChallengeDto(ss.getChallenge());
    }

    public void setValuesFromPersistedObjects(List<TeamStrengthValue> tsvList) {
        tsvList.forEach(tsv -> {
            this.values.add(new TeamStrengthValueDto(tsv));
        });
    }

}
