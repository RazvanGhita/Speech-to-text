package com.wtztechnologies.speechtotext.models.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPasswordChangeModel {
    String username;
    String currentPassword;
    String newPassword;
    String confirmPassword;
}
