package de.sandkastenliga.resultserver.dtos;

import de.sandkastenliga.resultserver.model.TeamStrengthSettings;
import lombok.Data;

@Data
public class TeamStrengthSettingsDto {

    int id;
    ChallengeDto challenge;
    int maxStrength;
    int minStrength;
    int flatFactor;

    public TeamStrengthSettingsDto() {
    }

    public TeamStrengthSettingsDto(TeamStrengthSettings tss) {
        this.id = tss.getId();
        this.challenge = new ChallengeDto(tss.getChallenge());
        this.minStrength = tss.getMinStrength();
        this.maxStrength = tss.getMaxStrength();
        this.flatFactor = tss.getFlatFactor();
    }
}
