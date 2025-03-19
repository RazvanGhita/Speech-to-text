package com.wtztechnologies.speechtotext.models.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsRequestModel {
    String username;
    String userStatus;
    String role;
}
