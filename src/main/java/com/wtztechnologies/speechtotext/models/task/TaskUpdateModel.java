package com.wtztechnologies.speechtotext.models.task;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskUpdateModel {
    Long id;
    String translate;
}
