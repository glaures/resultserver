package de.sandkastenliga.resultserver.dtos;

import java.util.Date;

public class StrengthSnapshotDto {

    private Date snapshotDate;
    private int strength;

    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
