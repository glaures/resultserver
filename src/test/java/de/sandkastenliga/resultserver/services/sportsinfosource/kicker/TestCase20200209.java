package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchState;

import java.util.ArrayList;
import java.util.List;

public class TestCase20200209 extends AbstractTestCase {

    public int getYear() {
        return -1;
    }

    public int getMonth() {
        return -1;
    }

    public int getDate() {
        return -1;
    }

    public int getMatchCount() {
        return 89;
    }

    @Override
    public List<MatchInfoStruct> getMatchInfoStructs() {
        List<MatchInfoStruct> res = new ArrayList<>();
        res.add(new MatchInfoStruct("Deutschland", "Bundesliga", "Bor. Mönchengladbach", "1. FC Köln",
                MatchState.canceled, -1, -1));
        res.add(new MatchInfoStruct("Deutschland", "Bundesliga", "Bayern München", "RB Leipzig",
                MatchState.ready, -1, -1));
        return res;
    }

    @Override
    protected String getTestFilename() {
        return "2020-02-09.html";
    }
}
