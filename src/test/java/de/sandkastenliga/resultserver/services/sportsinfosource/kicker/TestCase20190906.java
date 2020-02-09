package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchState;

import java.util.ArrayList;
import java.util.List;

public class TestCase20190906 extends AbstractTestCase {

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
        return 77;
    }

    @Override
    public List<MatchInfoStruct> getMatchInfoStructs() {
        List<MatchInfoStruct> res = new ArrayList<>();
        res.add(new MatchInfoStruct("Europa", "EM-QUALIFIKATION", "Deutschland", "Niederlande",
                MatchState.ready, -1, -1));
        res.add(new MatchInfoStruct("Europa", "EM-QUALIFIKATION U 21", "Tschechien", "Litauen",
                MatchState.running, 2, 0));
        res.add(new MatchInfoStruct("Weltweit", "Nationalteams Freundschaftsspiele", "Peru", "Ecuador",
                MatchState.finished, 0, 1));
        return res;
    }

    @Override
    protected String getTestFilename() {
        return "2019-09-06.html";
    }
}
