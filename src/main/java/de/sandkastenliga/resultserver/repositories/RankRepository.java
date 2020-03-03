package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Rank;
import de.sandkastenliga.resultserver.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    public Rank getRankByChallengeAndRoundAndYearAndTeam(Challenge challenge, int round, int year, Team team);

    public List<Rank> getRanksByChallengeAndRoundAndYearOrderByRankAsc(long challengeId, int round, int year);

}
