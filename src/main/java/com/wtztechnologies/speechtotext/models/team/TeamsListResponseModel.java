package com.wtztechnologies.speechtotext.models.team;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamsListResponseModel {
    List<TeamListResponseChild> teamList;
}
