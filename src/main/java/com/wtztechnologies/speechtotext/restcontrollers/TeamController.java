package com.wtztechnologies.speechtotext.restcontrollers;

import com.wtztechnologies.speechtotext.models.team.TeamListChild;
import com.wtztechnologies.speechtotext.models.team.TeamRequestModel;
import com.wtztechnologies.speechtotext.models.team.TeamUpdateModel;
import com.wtztechnologies.speechtotext.services.TeamService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @PreAuthorize("hasAuthority('TEAM_LEAD')")
    @PostMapping("/create-new-team")
    public ResponseEntity<?> createNewTeam(@RequestBody TeamRequestModel teamRequestModel) {
        return ResponseEntity.ok(teamService.createNewTeam(teamRequestModel));
    }

    @GetMapping("/get-all-teams")
    public Page<TeamListChild> getAllTeams(@RequestParam String keyword, Pageable pageable) {
        return teamService.getAllTeams(keyword,pageable);
    }

    @GetMapping("/get-team-by-id")
    public ResponseEntity<?> getTeamById(@RequestParam Long teamId) {
        return ResponseEntity.ok(teamService.getTeamById(teamId));
    }

    @GetMapping("/get-all-teams-by-team-lead-id")
    public ResponseEntity<?> getAllTeamsByTeamLeadId(@RequestParam Long teamLeadId){
        return ResponseEntity.ok(teamService.getAllTeamsByTeamLeaderId(teamLeadId));
    }


    @PutMapping("/update-team")
    public ResponseEntity<?> updateTeam(@RequestBody TeamUpdateModel teamUpdateModel){
        return ResponseEntity.ok(teamService.updateTeam(teamUpdateModel));
    }

    @DeleteMapping("/delete-team")
    public ResponseEntity<?> deleteTeam(@RequestParam Long teamId){
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok("Team deleted.");
    }

}
