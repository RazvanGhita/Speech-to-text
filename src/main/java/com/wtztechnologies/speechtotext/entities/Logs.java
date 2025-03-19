package com.wtztechnologies.speechtotext.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String log;
    String username;
    LocalDateTime date;
}
