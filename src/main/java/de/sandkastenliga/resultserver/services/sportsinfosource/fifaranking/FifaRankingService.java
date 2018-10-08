package de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FifaRankingService {

    private List<String> ranking = new ArrayList<String>();

    public FifaRankingService() throws IOException {
        update();
    }

    public List<String> getRanking() {
        return ranking;
    }

    public void update() throws IOException {
        ranking = new FifaRankingUpdater().getUpdatedRanking();
    }

}