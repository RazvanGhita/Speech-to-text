package com.wtztechnologies.speechtotext.models.team;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamListChild {

    Long teamId;
    String teamLeadUsername;
    String name;
    Integer memberNum;
}
