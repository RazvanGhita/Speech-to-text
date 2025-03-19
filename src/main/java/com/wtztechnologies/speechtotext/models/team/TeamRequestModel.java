package com.wtztechnologies.speechtotext.models.team;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamRequestModel {

    Long id;
    String name;
    List<Long> workers;
}
