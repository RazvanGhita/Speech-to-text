package com.wtztechnologies.speechtotext.models.log;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogIntervalModel {
    Long id;
    Date startDate;
    Date endDate;
}
