package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.enums.TaskStatus;
import com.wtztechnologies.speechtotext.exceptions.SpeechPlatformException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {

    default Task findByIdRequired(Long id) {
        return findById(id).orElseThrow(() -> new SpeechPlatformException(HttpStatus.NOT_FOUND,
                "The task with id: " + id + ", doesn't exist. "));
    }
    List<Task> findAllByUserId(Long userId);
    Page<Task> findAllByTaskStatusAndUserId(TaskStatus taskStatus, Long userId, Pageable pageable);

    List<Task> findAllByTaskStatusAndUserId(TaskStatus taskStatus, Long userId);

    Task findTopByAudioFileName(String filename);


}
