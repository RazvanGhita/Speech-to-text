package com.wtztechnologies.speechtotext.models.task;

import com.wtztechnologies.speechtotext.enums.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponseModelWithExtension {

    Long id;
    String createdAt;
    String audioFileName;
    String audioExtension;
    String translate;
    String duration;
    String process;
    @Enumerated(EnumType.STRING)
    TaskStatus taskStatus;
    Date startProcess;
    Date endProcess;
}
