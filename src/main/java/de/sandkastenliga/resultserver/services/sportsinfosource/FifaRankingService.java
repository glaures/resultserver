package de.sandkastenliga.resultserver.services.sportsinfosource;

import de.sandkastenliga.resultserver.model.Team;
import de.sandkastenliga.resultserver.repositories.TeamRepository;
import de.sandkastenliga.resultserver.services.error.ErrorHandlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class FifaRankingService {

    private final Logger logger = LoggerFactory.getLogger(FifaRankingService.class);
    @Value("${resultserver.nationstrengths.file}")
    private String nationStrengthsFile;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ErrorHandlingService errorHandlingService;

    public FifaRankingService() throws IOException {
    }

    @Transactional
    public void update() {
        Map<String, Integer> teamPoints = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(nationStrengthsFile)));
        String line = null;
        int maxPoints = 0;
        int minPoints = 0;
        try {
            while ((line = br.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(line, ";", false);
                String teamName = tok.nextToken().trim();
                List<Team> storedTeams = teamRepository.findTeamsByName(teamName);
                if (storedTeams.size() > 0) {
                    Integer points = Integer.parseInt(tok.nextToken());
                    maxPoints = (int) Math.max(maxPoints, points);
                    minPoints = (int) Math.min(minPoints, points);
                    teamPoints.put(teamName, points);
                } else {
                    logger.warn("Nation " + teamName + " unknown to the system. Please fix the team's name or ignore. " +
                            "Strength is set to 0");
                }
            }
            for (String teamName : teamPoints.keySet()) {
                int strength = (int) ((teamPoints.get(teamName) / (float) maxPoints * 100f));
                List<Team> teamsWithThatName = teamRepository.findTeamsByName(teamName);
                for (Team t : teamsWithThatName) {
                    t.setCurrentStrength(strength);
                    teamRepository.save(t);
                }
            }
        } catch(IOException ioex){
            errorHandlingService.handleError(ioex);
        }
    }

}