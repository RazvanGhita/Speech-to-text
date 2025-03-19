package com.wtztechnologies.speechtotext.models.team;

import com.wtztechnologies.speechtotext.models.user.UserDetailsResponseModel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamVacationModel {

    Long teamId;
    UserDetailsResponseModel teamLead;
}
