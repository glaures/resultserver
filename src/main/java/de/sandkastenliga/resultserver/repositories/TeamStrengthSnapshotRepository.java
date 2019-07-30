package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.model.TeamStrengthSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamStrengthSnapshotRepository extends JpaRepository<TeamStrengthSnapshot, Integer> {

    public List<TeamStrengthSnapshot> findAllByTeamOrderBySnapshotDateDesc(Team t);
}
