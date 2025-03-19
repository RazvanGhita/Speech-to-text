package com.wtztechnologies.speechtotext.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.wtztechnologies.speechtotext.entities.*;
import com.wtztechnologies.speechtotext.models.team.*;
import com.wtztechnologies.speechtotext.repositories.*;

import com.wtztechnologies.speechtotext.utils.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FileSystemStorageService fileSystemStorageService;
    private final LogsRepository logsRepository;
    private final LogsService logsService;
    private final PageConverter pageConverter;
    private final UsersOnVacationRepository usersOnVacationRepository;

    @Transactional
    public Long createNewTeam(TeamRequestModel teamRequestModel) {
        log.info("Calling method -- createNewTeam -- ");
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de creare a unei noi echipe.");
        logsRepository.save(logs);
        UserDetails userDetails = userDetailsRepository.findByUsernameRequired(logs.getUsername());

        setTeamLeaderTeam(userDetails.getId());
        userDetails.setNumberOfTeams(userDetails.getNumberOfTeams() + 1);
        userDetailsRepository.save(userDetails);
        Team team = modelMapper.map(teamRequestModel, Team.class);
        team.setTeamLeadId(userDetails.getId());
        team = teamRepository.save(team);
        addTeamMembers(team, teamRequestModel.getWorkers());
        return team.getId();
    }

    @Transactional
    private void setTeamLeaderTeam(Long teamLeaderId) {
        log.info("Calling method -- setTeamLeaderTeam -- with param: {}", teamLeaderId);
        UserDetails teamLeader = userDetailsRepository.findByIdRequired(teamLeaderId);
        teamLeader.setHasTeam(true);
        userDetailsRepository.save(teamLeader);
    }

    private void setTeamLeaderFalse(Long teamLeaderId) {
        log.info("Calling method -- setTeamLeaderFalse -- with param: {}", teamLeaderId);
        UserDetails teamLeader = userDetailsRepository.findByIdRequired(teamLeaderId);
        teamLeader.setHasTeam(false);
        userDetailsRepository.save(teamLeader);
    }

    @Transactional
    private void addTeamMembers(Team team, List<Long> workerIds) {
        log.info("Calling method -- addTeamMember -- ");
        for (Long workerId : workerIds) {
            UserDetails worker = userDetailsRepository.findByIdRequired(workerId);
            teamMemberRepository.save(TeamMember.builder()
                    .team(team)
                    .user(worker)
                    .build());
            worker.setHasTeam(true);
            worker.setNumberOfTeams(worker.getNumberOfTeams() + 1);
            userDetailsRepository.save(worker);

        }
    }

    public Page<TeamListChild> getAllTeams(String keyword, Pageable pageable) {
        log.info("Calling method -- getAllTeams -- ");
        Page<Team> teamList = teamRepository.findAllByTeamName(keyword, pageable);
        return pageConverter.convert(() -> teamList, this::createTeamListChildModel);
    }

    public List<TeamListResponseChild> getAllTeamsByTeamLeaderId(Long teamLeaderId) {
        List<Team> teamList = teamRepository.findAllByTeamLeadId(teamLeaderId);
        List<TeamListResponseChild> teamsListResponseModel = new ArrayList<>();
        for (Team team : teamList) {
            TeamListResponseChild child = modelMapper.map(team, TeamListResponseChild.class);
            List<TeamMember> teamMembers = teamMemberRepository.findAllMembersByTeam(team.getId());
            List<TeamMemberResponseModel> workerList = new ArrayList<>();
            for (TeamMember member : teamMembers) {
                TeamMemberResponseModel teamMemberResponseModel = modelMapper.map(member.getUser(), TeamMemberResponseModel.class);
                workerList.add(teamMemberResponseModel);
            }
            child.setTeamName(team.getName());
            child.setTeamId(team.getId());
            child.setMembers(workerList);
            teamsListResponseModel.add(child);
        }

        UserDetails tempTeamLead = userDetailsRepository.findById(teamLeaderId).orElse(null);
        List<Team> tempTeams = new ArrayList<>();
        if(Objects.nonNull(tempTeamLead)){
            List<UsersOnVacation> tempTeamList = usersOnVacationRepository.findAllByTempTeamLead(tempTeamLead.getUsername());

            for(UsersOnVacation usersOnVacation: tempTeamList){
                Team team = teamRepository.findById(usersOnVacation.getTeamId()).orElse(null);
                if(Objects.nonNull(team)){
                    tempTeams.add(team);
                }
            }

            for (Team team : tempTeams) {
                TeamListResponseChild child = modelMapper.map(team, TeamListResponseChild.class);
                List<TeamMember> teamMembers = teamMemberRepository.findAllMembersByTeam(team.getId());
                List<TeamMemberResponseModel> workerList = new ArrayList<>();
                for (TeamMember member : teamMembers) {
                    TeamMemberResponseModel teamMemberResponseModel = modelMapper.map(member.getUser(), TeamMemberResponseModel.class);
                    workerList.add(teamMemberResponseModel);
                }
                child.setTeamName(team.getName());
                child.setTeamId(team.getId());
                child.setMembers(workerList);
                teamsListResponseModel.add(child);
            }
        }

        return teamsListResponseModel;
    }

    public TeamResponseModel getTeamById(Long teamId) {
        log.info("Calling method -- getTeamById -- with param: {}", teamId);
        Team team = teamRepository.findByIdRequired(teamId);
        TeamResponseModel responseModel = modelMapper.map(team, TeamResponseModel.class);
        UserDetails teamLeader = userDetailsRepository.findByIdRequired(team.getTeamLeadId());
        TeamMemberResponseModel tl = modelMapper.map(teamLeader, TeamMemberResponseModel.class);
        responseModel.setTeamLeader(tl);
        List<TeamMemberResponseModel> workerList = new ArrayList<>();
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeam(team);
        for (TeamMember member : teamMembers) {
            TeamMemberResponseModel teamMemberResponseModel = modelMapper.map(member.getUser(),
                    TeamMemberResponseModel.class);
            workerList.add(teamMemberResponseModel);
        }
        responseModel.setWorkers(workerList);
        return responseModel;
    }

    @Transactional
    public Long updateTeam(TeamUpdateModel teamUpdateModel) {
        log.info("Calling method -- updateTeam -- ");
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de updatare a echipei " + teamUpdateModel.getName());
        logsRepository.save(logs);
        // LOGIC OF METHOD:
        // 1. Get team
        // 2. Remove team leader
        // 3. Remove team leader details from userDetails
        // 4. Remove existing team workers
        // 5. Add new team leader
        // 6. Add team leader details in userDetails
        // 7. Add new team workers

        // NEW IMPLEMENTATION
        // *** GET TEAM ***
        Team team = teamRepository.findByIdRequired(teamUpdateModel.getId());

        // *** GET TEAM LEADER ***
        UserDetails teamLeaderDetails = userDetailsRepository.findByIdRequired(team.getTeamLeadId());

        // *** EXCLUDE TEAM LEADER FROM TEAM ***
        team.setTeamLeadId(null);
        teamLeaderDetails.setNumberOfTeams(teamLeaderDetails.getNumberOfTeams() - 1);

        // *** GET TEAM WORKERS ***
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeam(team);

        // *** REMOVE TEAM WORKERS ***
        for(TeamMember teamMember : teamMembers){
            teamMemberRepository.delete(teamMember);
        }

        // *** ADD NEW TEAM LEADER ***
        UserDetails newTeamLeaderDetails = userDetailsRepository.findByIdRequired(teamUpdateModel.getTeamLeadId());
        newTeamLeaderDetails.setNumberOfTeams(newTeamLeaderDetails.getNumberOfTeams() + 1);

        // *** ADD NEW TEAM WORKERS ***
        modelMapper.map(teamUpdateModel, team);
        addTeamMembers(team, teamUpdateModel.getWorkers());
        teamRepository.save(team);
        return team.getId();

    }

    @Transactional
    public void deleteTeam(Long teamId) {
        log.info("Calling method -- deleteTeam -- with param: {}", teamId);
        Team team = teamRepository.findByIdRequired(teamId);
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de stergere a echipei " + team.getName());
        logsRepository.save(logs);
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeam(team);
        setTeamLeaderFalse(team.getTeamLeadId());
        UserDetails userDetails = userDetailsRepository.findByIdRequired(team.getTeamLeadId());
        userDetails.setNumberOfTeams(userDetails.getNumberOfTeams() - 1);
        userDetailsRepository.save(userDetails);
        for (TeamMember member : teamMembers) {
            member.getUser().setNumberOfTeams(member.getUser().getNumberOfTeams() - 1);
            if (member.getUser().getNumberOfTeams() == 0) {
                member.getUser().setHasTeam(false);
            }
            userDetailsRepository.save(member.getUser());
            teamMemberRepository.deleteById(member.getId());
        }
        teamRepository.deleteById(team.getId());
    }



    public List<TeamMember> getTeamMembersByTeamId(Long teamId) {
        Team team = teamRepository.findByIdRequired(teamId);
        return teamMemberRepository.findAllByTeam(team);

    }

    private @NotNull TeamListChild createTeamListChildModel(Team team) {
        log.info("Calling method -- createTeamResponseModel -- ");
        TeamListChild teamListChild = modelMapper.map(team, TeamListChild.class);
        List<TeamMember> teamMembers = teamMemberRepository.findAllMembersByTeam(team.getId());
        UserDetails userDetails = userDetailsRepository.findByIdRequired(team.getTeamLeadId());
        teamListChild.setTeamLeadUsername(userDetails.getUsername());
        teamListChild.setMemberNum(teamMembers.size());
        return teamListChild;
    }
}
