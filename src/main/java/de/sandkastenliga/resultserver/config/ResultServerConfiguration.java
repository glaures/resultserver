package de.sandkastenliga.resultserver.config;

import de.sandkastenliga.resultserver.services.sportsinfosource.SportsInfoSource;
import de.sandkastenliga.resultserver.services.sportsinfosource.fifaranking.FifaRankingService;
import de.sandkastenliga.resultserver.services.sportsinfosource.kicker.KickerSportsInfoSource;
import de.sandkastenliga.tools.projector.core.Projector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
public class ResultServerConfiguration {

    private final static Log log = LogFactory.getLog(ResultServerConfiguration.class);
    private final Projector projector = new Projector();

    @Bean
    public Projector projector() {
        return this.projector;
    }

    @Bean
    public SportsInfoSource sportsInfoSource() throws IOException {
        return new KickerSportsInfoSource(new FifaRankingService());
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); //single threaded by default
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }


    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        log.info("Time zone set to :" + TimeZone.getDefault().getDisplayName());
        log.info("Current time is: " + new SimpleDateFormat().format(new Date()));
    }
}
