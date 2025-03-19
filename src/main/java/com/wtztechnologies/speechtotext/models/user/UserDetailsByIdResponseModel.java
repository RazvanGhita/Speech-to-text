package com.wtztechnologies.speechtotext.models.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsByIdResponseModel {
    Long id;
    String username;
    String userStatus;
    String role;
    Integer numberOfTeams;
    Integer tasksDone;
    Date vacationStart;
    Date vacationEnd;
}
