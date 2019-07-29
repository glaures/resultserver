package de.sandkastenliga.resultserver.repositories;

import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.MatchState;
import de.sandkastenliga.resultserver.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    @Query("select m from Match m where m.challenge=:c and ((m.team1=:t1 and m.team2=:t2) or (m.team2=:t1 and m.team1=:t2)) and round like :r and m.start>:minStart and m.start<:maxStart")
    List<Match> findMatchByChallengeAndTeamsAndTimeframe(@Param("c") Challenge c, @Param("t1") Team t1, @Param("t2") Team t2, @Param("r") String round, @Param("minStart") Date minDate, @Param("maxStart") Date maxDate);

    @Query("select m from Match m where m.team1.name like :t1 and m.team2.name like :t2 order by m.start desc")
    List<Match> findClosestMatchesByTeams(@Param("t1") String team1Name, @Param("t2") String team2Name);

    List<Match> findMatchesByStartAfterAndStartBeforeAndStateInOrderByStartDesc(Date start, Date end, MatchState[] states);

    @Query("select m from Match m where m.challenge=:c and m.state=1 order by start asc")
    List<Match> getReadyMatches(@Param("c") Challenge c);

    @Query("select m from Match m where id in (:idList)")
    List<Match> getMatchesByIdList(@Param("idList") List<Integer> idList);

    @Query("select m from Match m where m.start>=:startDate and m.start<=:endDate order by start desc")
    List<Match> getAllMatchesAtDays(@Param("startDate") Date start, @Param("endDate") Date end);

    List<Match> findMatchesByChallenge_RegionAndChallenge_NameAndStart(String country, String challengeName, Date date);

    @Query("select distinct(m.challenge) from Match m where m.state in (:matchStateList)")
    List<Challenge> getAllChallengesWithOpenMatches(@Param("matchStateList") MatchState[] unfinishedMatchSates);

    Optional<Match> findMatchByCorrelationId(String correlationId);
}
