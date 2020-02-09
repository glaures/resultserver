package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.model.MatchState;

import java.util.ArrayList;
import java.util.List;

public class TestCase20180817 extends AbstractTestCase {

    public int getYear() {
        return 2018;
    }

    public int getMonth() {
        return 8;
    }

    public int getDate() {
        return 17;
    }

    public int getMatchCount() {
        return 61;
    }

    @Override
    public List<MatchInfoStruct> getMatchInfoStructs() {
        List<MatchInfoStruct> res = new ArrayList<>();
        res.add(new MatchInfoStruct("Deutschland", "Landespokal Mecklenburg-Vorpommern", "SV Parkentin", "Doberaner FC", MatchState.ready, -1, -1));
        return res;
    }

}
