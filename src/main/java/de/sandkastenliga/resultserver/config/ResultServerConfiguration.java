package de.sandkastenliga.resultserver.config;

import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import de.sandkastenliga.resultserver.services.sportsinfosource.kicker.KickerSportsInfoSource;
import de.sandkastenliga.tools.projector.core.Projector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ResultServerConfiguration {

    private final Projector projector = new Projector();

    @Bean
    public Projector projector() {
        return this.projector;
    }

    @Bean
    public SportsInfoSource sportsInfoSource() throws IOException {
        return new KickerSportsInfoSource(new FifaRankingService());
    }
}
