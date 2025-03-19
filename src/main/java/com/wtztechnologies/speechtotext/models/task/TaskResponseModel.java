package com.wtztechnologies.speechtotext.models.task;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponseModel {

    Long id;
    String createdAt;
    String audioFileName;
    String translate;
    String process;
    Date startProcess;
    Date endProcess;

}
