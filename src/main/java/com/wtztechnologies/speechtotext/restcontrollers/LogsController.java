package com.wtztechnologies.speechtotext.restcontrollers;

import com.wtztechnologies.speechtotext.models.task.TaskClickModel;
import com.wtztechnologies.speechtotext.services.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/logs")
public class LogsController {

    private final LogsService logsService;


    @PreAuthorize("hasAuthority('AUDIT')")
    @GetMapping("/get-logs-by-username")
    public ResponseEntity<?> getLogsByUserId(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endDate,
                                             @RequestParam String username, Pageable pageable) {
        return ResponseEntity.ok(logsService.getLogsByUser(startDate,endDate, username, pageable));
    }

    @PostMapping("/task-click-log")
    public ResponseEntity<?> taskClickLog(@RequestBody TaskClickModel taskClickModel) {
        logsService.taskClickLog(taskClickModel);
        return ResponseEntity.ok("Log created.");
    }
}
