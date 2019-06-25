package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchState;

import java.util.ArrayList;
import java.util.List;

public class TestCase20190624 extends AbstractTestCase {

    public int getYear() {
        return 2019;
    }

    public int getMonth() {
        return 6;
    }

    public int getDate() {
        return 24;
    }

    public int getMatchCount() {
        return 21;
    }

    @Override
    public List<MatchInfoStruct> getMatchInfoStructs() {
        List<MatchInfoStruct> res = new ArrayList<>();
        res.add(new MatchInfoStruct(null, null, "Elfenbeinküste", "Südafrika", MatchState.running, 0, 0));
        res.add(new MatchInfoStruct(null, null, "Spanien", "USA", MatchState.ready, -1, -1));
        return res;
    }

}
