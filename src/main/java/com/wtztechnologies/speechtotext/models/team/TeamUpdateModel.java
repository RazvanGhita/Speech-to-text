package com.wtztechnologies.speechtotext.models.team;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamUpdateModel {
    Long id;
    String name;
    Long teamLeadId;
    List<Long> workers;
}
