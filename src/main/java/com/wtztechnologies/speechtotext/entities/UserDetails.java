package com.wtztechnologies.speechtotext.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetails extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String username;
    String password;
    Boolean hasTeam;
    Integer numberOfTeams = 0;
    Timestamp lastChangedPassTime;
    @JsonIgnore
    Integer loginAttempts = 5;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    UserStatus userStatus;

    Date vacationStart;
    Date vacationEnd;
 
}
