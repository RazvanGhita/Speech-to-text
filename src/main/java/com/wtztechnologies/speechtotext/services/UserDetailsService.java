package com.wtztechnologies.speechtotext.services;

import java.sql.Timestamp;
import java.util.*;

import com.wtztechnologies.speechtotext.entities.*;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.SortOrder;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import com.wtztechnologies.speechtotext.models.team.TeamAvailableUsers;
import com.wtztechnologies.speechtotext.models.user.*;
import com.wtztechnologies.speechtotext.repositories.*;

import com.wtztechnologies.speechtotext.utils.PageConverter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService {
    private final UsersOnVacationRepository usersOnVacationRepository;

    private final ModelMapper modelMapper;
    private final FileSystemStorageService fileSystemStorageService;
    private final LogsService logsService;
    private final UserDetailsRepository userDetailsRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PageConverter pageConverter;
    private final TaskRepository taskRepository;
    private final LogsRepository logsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetailsResponseModel addMember(UserDetailsRequestModel userDetailsRequestModel) {
        log.info("Calling method -- addMember -- ");
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de adaugare membru.");
        logsRepository.save(logs);
        UserDetails existingUser = userDetailsRepository.findByUsername(userDetailsRequestModel.getUsername()).orElse(null);
        if (Objects.isNull(existingUser)) {
            var userDetails = modelMapper.map(userDetailsRequestModel, UserDetails.class);
            userDetails.setUserStatus(UserStatus.INACTIVE);
            userDetails.setHasTeam(false);
            userDetailsRepository.save(userDetails);
            return modelMapper.map(userDetails, UserDetailsResponseModel.class);
        } else {
            throw new RuntimeException("Username already exists.");
        }
    }

    public int getNumberOfTeams(UserDetails user){
        // GET USER TEAMS
        List<TeamMember> userTeamMemberList = teamMemberRepository.findTeamsByUserId(user.getId());
        List<Team> userTeams = new ArrayList<>();
        for(TeamMember member: userTeamMemberList){
            userTeams.add(member.getTeam());
        }

        // GET USER TEMP TEAM LEAD TEAMS
        List<UsersOnVacation> userTempTeamLeadTeams = usersOnVacationRepository.findAllByTempTeamLead(user.getUsername());
        for(UsersOnVacation userTempTeamLeadTeam: userTempTeamLeadTeams){
            Team t = teamRepository.findById(userTempTeamLeadTeam.getTeamId()).orElse(null);
            if(Objects.nonNull(t)){
                userTeams.add(t);
            }
        }

        // GET TEAMS WHERE USER IS TEAM LEAD
        List<Team> leadTeams = teamRepository.findAllByTeamLeadId(user.getId());
        for(Team team: leadTeams){
            userTeams.add(team);
        }

        // REMOVE DUPLICATES
        Set<Team> set = new HashSet<>(userTeams);
        userTeams.clear();
        userTeams.addAll(set);

        return userTeams.size();
    }

    @Transactional(readOnly = true)
    public UserDetailsByIdResponseModel getUserById(Long userId) {
        log.info("Calling method -- getUserById -- with param: {}", userId);

        UserDetails userDetails = userDetailsRepository.findByIdRequired(userId);
        UserDetailsByIdResponseModel responseModel = modelMapper.map(userDetails, UserDetailsByIdResponseModel.class);

        int numberOfTeams = getNumberOfTeams(userDetails);

        responseModel.setNumberOfTeams(numberOfTeams);
        responseModel.setTasksDone(getUserTasksDone(userDetails));
        return responseModel;

    }

    public void deactivateMember(Long userId) {
        log.info("Calling method -- deactivateMember -- with param: {}", userId);
        var userDetails = userDetailsRepository.findByIdRequired(userId);
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de deactivare a user-ului " + userDetails.getUsername());
        logsRepository.save(logs);
        userDetails.setUserStatus(UserStatus.DEACTIVATED);
        userDetailsRepository.save(userDetails);
    }

    @Transactional
    public void updateMember(UserDetailsUpdateModel userDetailsUpdateModel) {
        log.info("Calling method -- updateMember -- ");
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de updatare a user-ului cu id-ul" + userDetailsUpdateModel.getUsername());
        logsRepository.save(logs);
        var userDetails = userDetailsRepository.findByIdRequired(userDetailsUpdateModel.getId());
        modelMapper.map(userDetailsUpdateModel, userDetails);
        userDetailsRepository.save(userDetails);
    }

    public String verifyStatus(String username) {
        log.info("Calling method -- verifyStatus -- ");
        UserDetails userDetails = userDetailsRepository.findByUsernameRequired(username);
        return String.valueOf(userDetails.getUserStatus());

    }

    public String activateUser(UserActivationModel userActivationModel) {
        log.info("Calling method -- activateUser -- ");
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de activare user-ului " + userActivationModel.getUsername());
        logsRepository.save(logs);
        UserDetails user = userDetailsRepository.findByUsernameRequired(userActivationModel.getUsername());
        if ((user.getUserStatus().equals(UserStatus.INACTIVE))
                && (userActivationModel.getPassword().equals(userActivationModel.getConfirmPassword()))) {
            user.setUserStatus(UserStatus.ACTIVE);
            user.setPassword(bCryptPasswordEncoder.encode(userActivationModel.getPassword()));
            user.setLastChangedPassTime(new Timestamp(System.currentTimeMillis()));
            user.setLoginAttempts(5);
            user = userDetailsRepository.save(user);
        }
        return user.getUserStatus().toString();
    }

    public String inactivateUser(Long userId) throws Exception {
        UserDetails user = userDetailsRepository.findById(userId).orElse(null);
        if(Objects.nonNull(user)){
            log.info("Calling method -- inactivateUser -- ");
            Logs logs = new Logs();
            logsService.getUser(logs);
            logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de inactivare user-ului " + user.getUsername());
            logsRepository.save(logs);
            if(user.getUserStatus().equals(UserStatus.DEACTIVATED)){
                user.setUserStatus(UserStatus.INACTIVE);
                userDetailsRepository.save(user);
                return user.getUserStatus().toString();
            }else{
                throw new Exception("User status is not DEACTIVATED");
            }
        }else{
            throw new Exception("User not found");
        }
    }

    public void unblockUser(Long userId) {
        log.info("Calling method -- unblockUser -- with the id:" + userId);
        UserDetails userDetails = userDetailsRepository.findByIdRequired(userId);
        userDetails.setUserStatus(UserStatus.INACTIVE);
        userDetails.setPassword(null);
    }

    public TeamAvailableUsers getAvailableUsers(String keyword, SortOrder sortOrder) {
        log.info("Calling method -- getAvailableUsers -- ");
        TeamAvailableUsers response = new TeamAvailableUsers();
        List<UserDetails> workers = userDetailsRepository.findByUsernameOrdered(Role.WORKER.name(),
                UserStatus.ACTIVE.name(), false, keyword,
                String.valueOf(sortOrder));
        List<UserDetailsResponseModel> workersResponseModel = new ArrayList<UserDetailsResponseModel>();
        for (UserDetails worker : workers) {
            UserDetailsResponseModel model = modelMapper.map(worker, UserDetailsResponseModel.class);
            workersResponseModel.add(model);
        }
        response.setWorkers(workersResponseModel);
        return response;
    }

    public List<UserDetailsResponseModel> getAllWorkers(String keyword, SortOrder sortOrder) {
        try {
            log.info("Calling method -- getAllWorkers -- ");
            List<UserDetails> workers = userDetailsRepository.findByUsernameAndRoleAndUserStatus(Role.WORKER.name(), UserStatus.ACTIVE.name(), keyword);
            List<UserDetails> tempTeamLeads = userDetailsRepository.findByUsernameAndRoleAndUserStatus(Role.TEMP_TEAM_LEAD.name(), UserStatus.ACTIVE.name(), keyword);
            List<UserDetailsResponseModel> workersResponseModel = new ArrayList<UserDetailsResponseModel>();
            for (UserDetails worker : workers) {
                UserDetailsResponseModel model = modelMapper.map(worker, UserDetailsResponseModel.class);
                workersResponseModel.add(model);
            }
            for (UserDetails worker : tempTeamLeads) {
                UserDetailsResponseModel model = modelMapper.map(worker, UserDetailsResponseModel.class);
                workersResponseModel.add(model);
            }

            // REMOVE DUPLICATES
            Set<UserDetailsResponseModel> set = new HashSet<>(workersResponseModel);
            workersResponseModel.clear();
            workersResponseModel.addAll(set);

            return workersResponseModel;
        } catch (Exception e) {
            return null;
        }
    }

    public Page<UserDetailsResponseModel> getAllUsers(Role role, UserStatus userStatus, String keyword, Pageable pageable) {
        log.info("Calling method -- getAllUsers -- ");
        if (role == null && userStatus == null) {
            Page<UserDetails> users = userDetailsRepository.findByUsername(keyword, pageable);
            return pageConverter.convert(() -> users, this::createUserDetailsResponseModel);
        } else if (role != null && userStatus == null) {
            Page<UserDetails> users = userDetailsRepository.findByUsernameAndRole(role.name(), keyword, pageable);
            return pageConverter.convert(() -> users, this::createUserDetailsResponseModel);
        } else if (role == null && userStatus != null) {
            Page<UserDetails> users = userDetailsRepository.findByUsernameAndUserStatus(userStatus.name(), keyword, pageable);
            return pageConverter.convert(() -> users, this::createUserDetailsResponseModel);
        } else {
            Page<UserDetails> users = userDetailsRepository.findByUsernameAndRoleAndUserStatus(role.name(), userStatus.name(), keyword, pageable);
            return pageConverter.convert(() -> users, this::createUserDetailsResponseModel);
        }
    }




    @Transactional
    public boolean changePassword(UserPasswordChangeModel userPasswordChangeModel) {
        UserDetails userDetails = userDetailsRepository.findByUsernameRequired(userPasswordChangeModel.getUsername());
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + userPasswordChangeModel.getUsername() + " a apelat metoda de schimbare a parolei");
        logsRepository.save(logs);
        if (bCryptPasswordEncoder.matches(userPasswordChangeModel.getCurrentPassword(), userDetails.getPassword()) &&
                userPasswordChangeModel.getNewPassword().equals(userPasswordChangeModel.getConfirmPassword())) {
            userDetails.setPassword(bCryptPasswordEncoder.encode(userPasswordChangeModel.getNewPassword()));
            userDetails.setLastChangedPassTime(new Timestamp(System.currentTimeMillis()));
            userDetails.setUserStatus(UserStatus.ACTIVE);
            userDetailsRepository.save(userDetails);
            return true;
        }
        return false;
    }

    public boolean checkPasswordChangeDate(String username) {
        UserDetails userDetails = userDetailsRepository.findByUsernameRequired(username);
        if (userDetails.getLastChangedPassTime() == null) {
            return false;
        } else {
            final long passwordExpirationDate = 30L * 24L * 60L * 60L * 1000L;
//            final long passwordExpirationDate = 30L * 1000L;
            long currentTime = System.currentTimeMillis();
            long lastChangedTime = userDetails.getLastChangedPassTime().getTime();
            if (currentTime > lastChangedTime + passwordExpirationDate) {
                userDetails.setUserStatus(UserStatus.DISABLED);
                return true;
            } else {
                return false;
            }
        }
    }

    private UserDetailsResponseModel createUserDetailsResponseModel(UserDetails userDetails) {
        log.info("Calling method -- createUserDetailsResponseModel");
        UserDetailsResponseModel responseModel = modelMapper.map(userDetails, UserDetailsResponseModel.class);
        responseModel.setNumberOfTeams(userDetails.getNumberOfTeams());
        responseModel.setTasksDone(getUserTasksDone(userDetails));
        return responseModel;
    }

    private Integer getUserTasksDone(UserDetails userDetails) {
        return taskRepository.findAllByUserId(userDetails.getId()).size();
    }
}
