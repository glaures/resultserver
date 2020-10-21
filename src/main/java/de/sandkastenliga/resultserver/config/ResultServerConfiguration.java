package de.sandkastenliga.resultserver.config;

import de.sandkastenliga.resultserver.services.sportsinfosource.FifaRankingService;
import de.sandkastenliga.resultserver.services.sportsinfosource.KickerSportsInfoSource;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class ResultServerConfiguration {

    private final static Log log = LogFactory.getLog(ResultServerConfiguration.class);

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
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }
}
