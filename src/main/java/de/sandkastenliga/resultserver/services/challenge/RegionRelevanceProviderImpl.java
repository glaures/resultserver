package de.sandkastenliga.resultserver.services.challenge;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RegionRelevanceProviderImpl implements de.sandkastenliga.resultserver.services.sportsinfosource.RegionRelevanceProvider {

    private final static String[] RELEVANT_REGIONS = new String[]{
            "Deutschland",
            "England",
            "Spanien",
            "Frankreich",
            "Italien",
            "Österreich",
            "Schweiz",
            "Weltweit",
            "Europa",
            "(Weltweit)",
            "(Europa)"};

    public boolean isRelevantRegion(String region) {
        return Arrays.asList(RELEVANT_REGIONS).contains(region);
    }

    @Override
    public String[] getRelevantRegions() {
        return RELEVANT_REGIONS;
    }

}
