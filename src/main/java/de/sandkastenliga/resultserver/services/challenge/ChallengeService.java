package de.sandkastenliga.resultserver.services.challenge;

import de.sandkastenliga.resultserver.dtos.ChallengeDto;
import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.ChallengeMode;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.tools.projector.core.Projector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private Projector projector;

    public List<ChallengeDto> getAllChallenges() {
        return challengeRepository.findAll().stream().map(c -> projector.project(c, ChallengeDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public Challenge getOrCreateChallenge(String region, String challenge, String challengeRankingUrl,
                                          Date challengeRankingUrlDate) {
        Challenge c = challengeRepository.findChallengeByRegionAndName(region, challenge);
        if (c == null) {
            c = new Challenge();
            c.setRegion(region);
            c.setName(challenge);
            challengeRepository.save(c);
        }
        Date now = new Date();
        if (challengeRankingUrl != null && challengeRankingUrlDate != null) {
            if (c.getRankUrlDate() == null
                    || (c.getRankUrlDate().before(challengeRankingUrlDate))) {
                c.setRankUrl(challengeRankingUrl);
                c.setRankUrlDate(challengeRankingUrlDate);
            }
        }
        challengeRepository.saveAndFlush(c);
        return c;
    }

    @Transactional
    public void markKoChallenge(String region, String challengeName) {
        Challenge c = getOrCreateChallenge(region, challengeName, null, null);
        c.setChallengeMode(ChallengeMode.ko);
    }

}
