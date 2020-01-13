package de.sandkastenliga.resultserver.services.sportsinfosource.kicker;

import de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider;

public class SimpleChallengeRelevanceProvider  implements RegionRelevanceProvider {
    @Override
    public boolean isRelevantRegion(String region) {
        return false;
    }
}
