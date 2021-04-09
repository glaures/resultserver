package de.sandkastenliga.resultserver.rest.resources;

import de.sandkastenliga.resultserver.dtos.TeamStrengthSettingsDto;
import de.sandkastenliga.resultserver.dtos.TeamStrengthSnapshotDto;
import de.sandkastenliga.resultserver.repositories.TeamStrengthSnapshotRepository;
import de.sandkastenliga.resultserver.rest.dtos.TeamStrengthDto;
import de.sandkastenliga.resultserver.rest.dtos.TeamStrengthSnapshotViewDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import de.sandkastenliga.resultserver.services.teamstrengths.TeamStrengthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TeamStrengthsResource {

    private final TeamStrengthSnapshotRepository teamStrengthSnapshotRepository;
    private final TeamStrengthService teamStrengthService;

    public TeamStrengthsResource(TeamStrengthSnapshotRepository teamStrengthSnapshotRepository, TeamStrengthService teamStrengthService) {
        this.teamStrengthSnapshotRepository = teamStrengthSnapshotRepository;
        this.teamStrengthService = teamStrengthService;
    }

    @GetMapping("/teamstrengthsview")
    public TeamStrengthSnapshotViewDto getTeamStrengthView(@RequestParam("c") Integer challengeId) throws ServiceException {
        TeamStrengthSnapshotViewDto res = new TeamStrengthSnapshotViewDto();
        res.setSettings(teamStrengthService.getOrCreateTeamStrengthSettingsByChallenge(challengeId));
        List<TeamStrengthSnapshotDto> snapshots = teamStrengthService.getSnapshotsByChallenge(challengeId);
        if (snapshots.size() == 0) {
            snapshots.add(teamStrengthService.createSnapshot(challengeId));
        }
        res.setSnapshots(snapshots);
        // fill team list
        if (snapshots.size() > 0) {
            snapshots.get(0).getValues().forEach(v -> {
                res.addTeam(new TeamStrengthDto(v.getTeam()));
            });
        }
        /**
         * TODO: assuming that the teams are the same for every snapshot. Ensure that
         * in case a new season starts, all snapshots are deleted or an initial snapshot
         * with the new teams is created per challenge
         **/
        for (int snapshotIdx = 0; snapshotIdx < snapshots.size(); snapshotIdx++) {
            TeamStrengthSnapshotDto snapshot = snapshots.get(snapshotIdx);
            for (int teamIdx = 0; teamIdx < snapshot.getValues().size(); teamIdx++) {
                TeamStrengthDto ts = res.getTeams().get(teamIdx);
                ts.addValue(snapshot.getValues().get(teamIdx).getValue());
            }
        }
        ;
        return res;
    }

    @PostMapping("/teamstrengthsettings")
    public TeamStrengthSettingsDto updateTeamStrengthSettings(@RequestBody TeamStrengthSettingsDto settings) throws ServiceException {
        return teamStrengthService.updateTeamStrengthSettings(settings);
    }

    @PostMapping("/teamstrengthsnapshots")
    public TeamStrengthSnapshotViewDto updateTeamStrengthSettings(@RequestBody TeamStrengthSnapshotDto snapshot) throws ServiceException {
        teamStrengthService.updateTeamStrengthSnapshot(snapshot);
        return getTeamStrengthView(snapshot.getChallenge().getId());
    }

    @PutMapping("/teamstrengthsnapshots")
    public TeamStrengthSnapshotViewDto createNewSnapshot(@RequestParam("c") int challengeId) throws ServiceException {
        teamStrengthService.createSnapshot(challengeId);
        return getTeamStrengthView(challengeId);
    }

    @DeleteMapping("/teamstrengthsnapshots")
    public TeamStrengthSnapshotViewDto deleteSnapshot(@RequestParam("s") int snapshotId) throws ServiceException {
        int challengeId = teamStrengthService.deleteSnapshot(snapshotId);
        return getTeamStrengthView(challengeId);
    }
}
