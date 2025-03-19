package com.wtztechnologies.speechtotext.services;

import com.wtztechnologies.speechtotext.entities.Logs;
import com.wtztechnologies.speechtotext.entities.Team;
import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.entities.UsersOnVacation;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import com.wtztechnologies.speechtotext.models.user.UsersOnVacationMemberRequestModel;
import com.wtztechnologies.speechtotext.models.user.UsersOnVacationRequestModel;
import com.wtztechnologies.speechtotext.repositories.LogsRepository;
import com.wtztechnologies.speechtotext.repositories.TeamRepository;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;
import com.wtztechnologies.speechtotext.repositories.UsersOnVacationRepository;
import com.wtztechnologies.speechtotext.utils.PageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsersOnVacationService {
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final UsersOnVacationRepository usersOnVacationRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final PageConverter pageConverter;
    private final LogsRepository logsRepository;
    private final LogsService logsService;

    public Page<UsersOnVacation> getAllUsersOnVacation(String keyword, Pageable pageable) {
        Page<UsersOnVacation> usersOnVacation = usersOnVacationRepository.findAllByUsernameContainingIgnoreCase(keyword, pageable);
        return pageConverter.convert(() -> usersOnVacation, this::createUsersOnVacation);
    }

    public void scheduleVacation(UsersOnVacationMemberRequestModel usersOnVacationMemberRequestModel){
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de stabilire a unui concediu.");
        logsRepository.save(logs);

        UserDetails currentTeamLeader = userDetailsRepository.findByUsernameRequired(usersOnVacationMemberRequestModel.getUsername());
        currentTeamLeader.setUserStatus(UserStatus.VACATION);
        currentTeamLeader.setVacationStart(usersOnVacationMemberRequestModel.getVacationStart());
        currentTeamLeader.setVacationEnd(usersOnVacationMemberRequestModel.getVacationEnd());
        userDetailsRepository.save(currentTeamLeader);
    }

    public void scheduleTeamLeadVacation(UsersOnVacationRequestModel usersOnVacationRequestModel) throws Exception {
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de stabilire a unui concediu.");
        logsRepository.save(logs);

        // SET CURRENT TEAM LEAD STATUS AS: VACATION
        UserDetails currentTeamLeader = userDetailsRepository.findByUsernameRequired(usersOnVacationRequestModel.getUsername());
        currentTeamLeader.setUserStatus(UserStatus.VACATION);
        currentTeamLeader.setVacationStart(usersOnVacationRequestModel.getVacationStart());
        currentTeamLeader.setVacationEnd(usersOnVacationRequestModel.getVacationEnd());
        userDetailsRepository.save(currentTeamLeader);

        // GET TEMP TEAM LEAD USER DETAILS
        UserDetails tempTeamLead = userDetailsRepository.findByUsernameRequired(usersOnVacationRequestModel.getTempTeamLead());
        tempTeamLead.setRole(Role.TEMP_TEAM_LEAD);
        userDetailsRepository.save(tempTeamLead);

        // REMOVE ALL CONTAINING USER ON VACATION
        UsersOnVacation alreadyExistingEntry = usersOnVacationRepository.findByTeamIdAndUsername(usersOnVacationRequestModel.getTeamId(), usersOnVacationRequestModel.getUsername());
        if(Objects.nonNull(alreadyExistingEntry)) {
            usersOnVacationRepository.delete(alreadyExistingEntry);
        }

        UsersOnVacation usersOnVacation = modelMapper.map(usersOnVacationRequestModel, UsersOnVacation.class);
        usersOnVacationRepository.save(usersOnVacation);
    }


    private UsersOnVacation createUsersOnVacation(UsersOnVacation usersOnVacation) {
        log.info("Calling method -- createUsersOnVacation -- ");
        return modelMapper.map(usersOnVacation, UsersOnVacation.class);
    }
}
