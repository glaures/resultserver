package de.sandkastenliga.resultserver.services.teamstrengths;

import de.sandkastenliga.resultserver.dtos.RankDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthSettingsDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthSnapshotDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthValueDto;
import de.sandkastenliga.resultserver.model.*;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.AbstractJpaDependentService;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

@Service
@Transactional(rollbackFor = {Throwable.class})
public class TeamStrengthService extends AbstractJpaDependentService {

    final
    EntityManager entityManager;
    final
    ChallengeRepository challengeRepository;
    final
    TeamRepository teamRepository;
    final MatchService matchService;

    @Autowired
    public TeamStrengthService(EntityManager entityManager, ChallengeRepository challengeRepository,
                               TeamRepository teamRepository,
                               MatchService matchService) {
        this.entityManager = entityManager;
        this.challengeRepository = challengeRepository;
        this.teamRepository = teamRepository;
        this.matchService = matchService;
    }

    public TeamStrengthSettingsDto getOrCreateTeamStrengthSettingsByChallenge(int challengeId) throws ServiceException {
        TeamStrengthSettings settings;
        try {
            settings = (TeamStrengthSettings) entityManager.createQuery(
                    "SELECT tss FROM TeamStrengthSettings tss " +
                            "WHERE tss.challenge.id=:challengeId")
                    .setParameter("challengeId", challengeId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            settings = new TeamStrengthSettings();
            settings.setChallenge(challengeRepository.getOne(challengeId));
            entityManager.persist(settings);
        }
        updateTeamStrengths(challengeId);
        return new TeamStrengthSettingsDto(settings);
    }

    public List<TeamStrengthSnapshotDto> getSnapshotsByChallenge(int challengeId) {
        List<TeamStrengthSnapshotDto> res = entityManager.createQuery(
                "SELECT new de.sandkastenliga.resultserver.dtos.TeamStrengthSnapshotDto(tss) " +
                        "FROM TeamStrengthSnapshot tss " +
                        "WHERE tss.challenge.id=:challengeId " +
                        "ORDER BY tss.snapshotDate ASC")
                .setParameter("challengeId", challengeId)
                .getResultList();

        for (TeamStrengthSnapshotDto tss : res) {
            tss.setValuesFromPersistedObjects(getValuesBySnapshot(tss.getId()));
        }
        return res;
    }

    public TeamStrengthSnapshotDto createSnapshot(int challengeId) throws ServiceException {
        Challenge c = getValid(challengeId, challengeRepository);
        TeamStrengthSnapshot latestSnapshot = getLatestSnapshotByChallenge(c);
        TeamStrengthSnapshot tss = new TeamStrengthSnapshot();
        tss.setChallenge(c);
        tss.setSnapshotDate(new Date());
        entityManager.persist(tss);
        List<TeamStrengthValueDto> values = new ArrayList<>();
        if (latestSnapshot != null) {
            List<TeamStrengthValue> lastValues = getValuesBySnapshot(latestSnapshot.getId());
            lastValues.forEach(lv -> {
                TeamStrengthValue newVal = new TeamStrengthValue();
                newVal.setValue(lv.getValue());
                newVal.setSnapshot(tss);
                newVal.setTeam(lv.getTeam());
                entityManager.persist(newVal);
                values.add(new TeamStrengthValueDto(newVal));
            });
        } else {
            List<RankDto> ranks = new ArrayList<>();
            // initialen Snapshot für Wettbewerb erstellen
            List<String> unfinishedRounds = challengeRepository.getUnfinishedRoundsForChallenge(c.getId());
            if (unfinishedRounds.size() > 0) {
                ranks.addAll(challengeRepository.getChallengeRanking(
                        c.getName(),
                        c.getRegion(),
                        Integer.parseInt(unfinishedRounds.get(0)),
                        Calendar.getInstance().get(Calendar.YEAR)));
            }
            if(ranks.size() == 0) {
                // kein ranking verfügbar -> teams holen und ranking erstellen
                List<Team> teams = teamRepository.findAllByChallenge(c.getId());
                for (Team t : teams) {
                    ranks.add(new RankDto(1, t.getId(), t.getName(), 0));
                }
            }
            for (RankDto r : ranks) {
                TeamStrengthValue tsv = new TeamStrengthValue();
                tsv.setSnapshot(tss);
                tsv.setTeam(teamRepository.getOne(r.getTeamId()));
                tsv.setValue(10);
                entityManager.persist(tsv);
                values.add(new TeamStrengthValueDto(tsv));
            }
        }
        TeamStrengthSnapshotDto res = new TeamStrengthSnapshotDto(tss);
        res.setValues(values);
        updateTeamStrengths(challengeId);
        return res;
    }

    public TeamStrengthSettingsDto updateTeamStrengthSettings(TeamStrengthSettingsDto settings) throws ServiceException {
        TeamStrengthSettings curr =
                entityManager.find(TeamStrengthSettings.class, settings.getId());
        curr.setMinStrength(settings.getMinStrength());
        curr.setMaxStrength(settings.getMaxStrength());
        curr.setFlatFactor(settings.getFlatFactor());
        entityManager.persist(curr);
        // set challenge level
        Challenge challenge = entityManager.find(Challenge.class, settings.getChallenge().getId());
        challenge.setLevel(settings.getChallenge().getLevel());
        entityManager.persist(challenge);
        updateTeamStrengths(settings.getChallenge().getId());
        return new TeamStrengthSettingsDto(curr);
    }

    public void updateTeamStrengthSnapshot(TeamStrengthSnapshotDto snapshot) throws ServiceException {
        try {
            final TeamStrengthSnapshot persistedSS = entityManager.find(TeamStrengthSnapshot.class, snapshot.getId());
            final List<TeamStrengthValue> persistedValues = getTeamStrengthValuesBySnapshot(persistedSS.getId());
            snapshot.getValues().forEach(tsv -> {
                Optional<TeamStrengthValue> pTsvOpt = persistedValues.stream().filter(ptsv -> ptsv.getId() == tsv.getId()).findFirst();
                if (pTsvOpt.isPresent()) {
                    final TeamStrengthValue pTsv = pTsvOpt.get();
                    pTsv.setValue(tsv.getValue());
                    entityManager.persist(pTsv);
                }
            });
            updateTeamStrengths(snapshot.getChallenge().getId());
        } catch (NoResultException nre) {
            throw new ServiceException("error.noSuchObject", "snapshot", "" + snapshot.getId());
        }
    }

    public List<TeamStrengthValue> getTeamStrengthValuesBySnapshot(int snapshotId) {
        return (List<TeamStrengthValue>) entityManager.createQuery(
                "SELECt tsv FROM TeamStrengthValue tsv " +
                        "WHERE tsv.snapshot.id=:snapshotId")
                .setParameter("snapshotId", snapshotId)
                .getResultList();
    }

    public int deleteSnapshot(int snapshotId) throws ServiceException {
        TeamStrengthSnapshot snapshot = entityManager.find(TeamStrengthSnapshot.class, snapshotId);
        int challengeId = snapshot.getChallenge().getId();
        entityManager.createQuery("DELETE FROM TeamStrengthValue WHERE snapshot.id=:id")
                .setParameter("id", snapshotId)
                .executeUpdate();
        entityManager.createQuery("DELETE FROM TeamStrengthSnapshot WHERE id=:id")
                .setParameter("id", snapshotId)
                .executeUpdate();
        updateTeamStrengths(challengeId);
        return challengeId;
    }

    private void updateTeamStrengths(int challengeId) throws ServiceException {
        final TeamStrengthSettings settings = (TeamStrengthSettings) entityManager.createQuery(
                "SELECT tss from TeamStrengthSettings tss " +
                        "WHERE tss.challenge.id=:challengeId")
                .setParameter("challengeId", challengeId)
                .getSingleResult();
        final int flatFactor = settings.getFlatFactor();
        float minFlattenedAvg = getFlattenedAvg(true, challengeId, flatFactor);
        float maxFlattenedAvg = getFlattenedAvg(false, challengeId, flatFactor);
        List<Object[]> allValues = entityManager.createQuery(
                "SELECT tsv.team, avg(tsv.value) FROM TeamStrengthValue tsv WHERE " +
                        "tsv.snapshot.challenge.id=:challengeId " +
                        "GROUP BY tsv.team")
                .setParameter("challengeId", challengeId)
                .getResultList();
        for (Object[] oa : allValues) {
            Team t = (Team) oa[0];
            float avg = flatten(((Double) oa[1]).floatValue(), flatFactor);
            avg -= minFlattenedAvg;
            float percentProjection = avg / (maxFlattenedAvg - minFlattenedAvg);
            float strength = (settings.getMaxStrength() -
                    (settings.getMaxStrength() - settings.getMinStrength()) * percentProjection);
            t.setCurrentStrength((int) strength);
            entityManager.persist(t);
            matchService.handleTeamStrengthUpdate(challengeId, t.getId());
        }
    }

    private float getFlattenedAvg(boolean min, int challengeId, int flatFactor) {
        List<Double> res = entityManager.createQuery(
                "SELECT avg(tsv.value) FROM TeamStrengthValue tsv WHERE " +
                        "tsv.snapshot.challenge.id=:challengeId " +
                        "GROUP BY tsv.team")
                .setParameter("challengeId", challengeId)
                .getResultList();
        double avg = min ? Float.MAX_VALUE : 0f;
        for (double f : res) {
            avg = min ? Math.min(avg, f) : Math.max(avg, f);
        }
        return flatten((float) avg, flatFactor);
    }

    private float flatten(float avg, int flatFactor) {
        float res = avg;
        for (int flatRun = 0; flatRun < flatFactor; flatRun++) {
            res = (float) Math.sqrt(res);
        }
        return res;
    }

    private List<TeamStrengthValue> getValuesBySnapshot(int snapshotId) {
        return entityManager.createQuery(
                "SELECT tsv FROM TeamStrengthValue tsv " +
                        "WHERE tsv.snapshot.id=:snapshotId ")
                .setParameter("snapshotId", snapshotId)
                .getResultList();
    }

    private TeamStrengthSnapshot getLatestSnapshotByChallenge(Challenge c) {
        return (TeamStrengthSnapshot) entityManager.createQuery(
                "SELECT tss FROM TeamStrengthSnapshot tss " +
                        "WHERE tss.challenge=:challenge " +
                        "ORDER BY tss.snapshotDate DESC")
                .setParameter("challenge", c)
                .setMaxResults(1)
                .getResultList().stream().findFirst().orElse(null);
    }

}
