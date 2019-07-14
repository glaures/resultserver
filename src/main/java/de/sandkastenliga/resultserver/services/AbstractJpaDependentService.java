package de.sandkastenliga.resultserver.services;

import de.sandkastenliga.resultserver.model.Challenge;
import de.sandkastenliga.resultserver.model.Match;
import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.ChallengeRepository;
import de.sandkastenliga.resultserver.repositories.MatchRepository;
import de.sandkastenliga.resultserver.repositories.TeamRepository;

import java.util.Optional;

public abstract class AbstractJpaDependentService {

    protected Challenge getValid(Integer id, ChallengeRepository challengeRepository) throws ServiceException {
        Optional<Challenge> res = challengeRepository.findById(id);
        if (!res.isPresent())
            throw new ServiceException("error.noSuchObject", "challenge", "" + id);
        return res.get();
    }

    protected Match getValid(Integer id, MatchRepository matchRepository) throws ServiceException {
        Optional<Match> res = matchRepository.findById(id);
        if (!res.isPresent())
            throw new ServiceException("error.noSuchObject", "match", "" + id);
        return res.get();
    }

    protected Team getValid(String name, TeamRepository teamRepository) throws ServiceException {
        Optional<Team> res = teamRepository.findById(name);
        if (!res.isPresent())
            throw new ServiceException("error.noSuchObject", "team", "" + name);
        return res.get();
    }


}
