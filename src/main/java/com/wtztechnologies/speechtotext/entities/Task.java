package com.wtztechnologies.speechtotext.entities;

import com.wtztechnologies.speechtotext.enums.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Long userId;

    String audioFileName;

    String audioExtension;

    String duration;
    String process ;
    @Lob
    String translate;

    Date startProcess;
    Date endProcess;

    @Enumerated(EnumType.STRING)
    TaskStatus taskStatus;
}
