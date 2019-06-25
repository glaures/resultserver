package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.dtos.MatchInfo;
import de.sandkastenliga.resultserver.model.MatchState;
import org.junit.Test;

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
        return 138;
    }

    @Override
    public List<MatchInfoStruct> getMatchInfoStructs() {
        List<MatchInfoStruct> res = new ArrayList<>();
        res.add(new MatchInfoStruct("Afrika", "CAF Champions League", "Horoya AC Conakry", "AS Togo-Port", MatchState.finished, 2, 1));
        res.add(new MatchInfoStruct("Deutschland", "Landespokal Mecklenburg-Vorpommern", "SV Parkentin", "Doberaner FC", MatchState.ready, -1, -1));
        return res;
    }

}
