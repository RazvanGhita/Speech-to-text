package com.wtztechnologies.speechtotext.models.team;

import com.wtztechnologies.speechtotext.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamMemberResponseModel {
    Long id;
    String username;
    Role role;
}
