package com.wtztechnologies.speechtotext.authentication.controller;

import com.wtztechnologies.speechtotext.authentication.JwtTokenUtil;
import com.wtztechnologies.speechtotext.authentication.model.JwtRequest;
import com.wtztechnologies.speechtotext.authentication.model.JwtResponse;
import com.wtztechnologies.speechtotext.authentication.service.JwtUserDetailsService;
import com.wtztechnologies.speechtotext.entities.Logs;
import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.entities.UsersOnVacation;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import com.wtztechnologies.speechtotext.repositories.LogsRepository;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;

import com.wtztechnologies.speechtotext.repositories.UsersOnVacationRepository;
import com.wtztechnologies.speechtotext.services.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/auth")
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserDetailsService userService;
    @Autowired
    private LogsRepository logsRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UsersOnVacationRepository usersOnVacationRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
            throws AuthenticationException {
        UserDetails userDetails = userDetailsRepository.findByUsernameRequired(authenticationRequest.getUsername());
        Logs logs = new Logs();
        logs.setLog("User-ul " + authenticationRequest.getUsername() + " a apelat metoda de login");
        logs.setUsername(authenticationRequest.getUsername());
        logs.setDate(LocalDateTime.now());
        logsRepository.save(logs);
        if (!userDetails.getRole().equals(Role.ADMINISTRATOR)) {
            if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword())) {
                if (userDetails.getLoginAttempts() > 1) {
                    userDetails.setLoginAttempts(userDetails.getLoginAttempts() - 1);
                    userDetailsRepository.save(userDetails);
                    return ResponseEntity.badRequest().body("Parola gresita. Mai ai " + userDetails.getLoginAttempts() + " incercari ramase.");
                } else {
                    userDetails.setUserStatus(UserStatus.BLOCKED);
                    userDetailsRepository.save(userDetails);
                    return ResponseEntity.badRequest().body("Ai introdus gresit parola de prea multe ori. Contul tau a fost blocat.");
                }
            }
        }
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        userDetails.setLoginAttempts(5);
        if (!userDetails.getRole().equals(Role.ADMINISTRATOR)) {
            if (userDetails.getRole().equals(Role.TEAM_LEAD) && userDetails.getVacationEnd() != null) {
                long currentTime = System.currentTimeMillis();
                long vacationEnd = userDetails.getVacationEnd().getTime();
                List<UsersOnVacation> tempTeamLeadsList = usersOnVacationRepository.findAllByUsername(userDetails.getUsername());
                if (currentTime > vacationEnd) {
                    userDetails.setUserStatus(UserStatus.ACTIVE);
                    userDetails.setVacationStart(null);
                    userDetails.setVacationEnd(null);
                    userDetailsRepository.save(userDetails);
                }
                for (UsersOnVacation member : tempTeamLeadsList) {
                    usersOnVacationRepository.deleteById(member.getId());
                    List<UsersOnVacation> tempLeadsList = usersOnVacationRepository.findAllByTempTeamLead(member.getTempTeamLead());
                    if (tempLeadsList.size() == 0) {
                        UserDetails tempTeamLead = userDetailsRepository.findByUsernameRequired(member.getTempTeamLead());
                        tempTeamLead.setRole(Role.WORKER);
                        userDetailsRepository.save(tempTeamLead);
                    }
                }
            }
            if (userService.checkPasswordChangeDate(authenticationRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Parola ta a expirat. Schimba parola.");
            } else if (!userDetails.getUserStatus().equals(UserStatus.BLOCKED) && !userDetails.getUserStatus().equals(UserStatus.DISABLED)) {
                final String token = jwtTokenUtil.generateToken(authentication);
                return ResponseEntity.ok(new JwtResponse(token));

            } else {
                return ResponseEntity.badRequest().body("Contul tau este blocat.");
            }
        } else {

            final String token = jwtTokenUtil.generateToken(authentication);
            return ResponseEntity.ok(new JwtResponse(token));
        }
    }


    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout() {
        Logs logs = new Logs();
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de login");
        logs.setUsername(logs.getUsername());
        logs.setDate(LocalDateTime.now());
        logsRepository.save(logs);
        return ResponseEntity.ok("Successfully logged out.");
    }
}