package com.wtztechnologies.speechtotext.models.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsResponseModel {
    Long id;
    String username;
    String userStatus;
    String role;
    Integer numberOfTeams;
    Integer tasksDone;
    Date vacationStart;
    Date vacationEnd;
}
