package com.wtztechnologies.speechtotext.models.team;

import java.util.List;

import com.wtztechnologies.speechtotext.models.user.UserDetailsResponseModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamAvailableUsers {
    List<UserDetailsResponseModel> workers;
}
