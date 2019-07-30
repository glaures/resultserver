package de.sandkastenliga.resultserver.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TeamStrengthSnapshot {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Team team;
    @Temporal(TemporalType.TIMESTAMP)
    private Date snapshotDate;
    private int strength;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

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
