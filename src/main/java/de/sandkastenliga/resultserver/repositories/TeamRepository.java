package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, String>{

    public Optional<Team> findTeamByName(String name);
}
