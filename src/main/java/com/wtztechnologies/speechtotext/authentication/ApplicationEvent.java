package com.wtztechnologies.speechtotext.authentication;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationEvent {

    private final UserDetailsRepository userDetailsRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {

        if (notExists()) {
            var appUser = UserDetails.builder()
                    .username("A-SpeechToTextAdmin")
                    .password("$2a$04$XgKPjglsMc9y6ga60yxvCO2bUlurXNPCuGaI6wEVI3hz9FSoLztJS")
                    .role(Role.ADMIN)
                    .hasTeam(true)
                    .userStatus(UserStatus.ACTIVE)
                    .loginAttempts(5)
                    .numberOfTeams(0)
                    .build();
            userDetailsRepository.save(appUser);
        }

    }

    private boolean notExists() {
        return userDetailsRepository.findByUsername("A-SpeechToTextAdmin").isEmpty();
    }
}
