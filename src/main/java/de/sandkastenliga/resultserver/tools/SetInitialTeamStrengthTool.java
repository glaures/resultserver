package de.sandkastenliga.resultserver.tools;

import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.team.TeamService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

@Component
public class SetInitialTeamStrengthTool {

    @Autowired
    private TeamService teamService;

    @PostConstruct
    public void setInitialTeamStrengths() throws IOException {
        Resource initialStrengthResource = new ClassPathResource("/initialstrengths.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(initialStrengthResource.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            StringTokenizer tok = new StringTokenizer(line, ";", false);
            int strength = Integer.parseInt(tok.nextToken());
            String teamName = tok.nextToken().trim();
            try {
                System.out.println("setting team strength of " + teamName + " to " + strength);
                teamService.setTeamStrength(teamName, strength);
            } catch (ServiceException se) {
                System.out.println(se.getMessage());
            }
        }
    }

}
