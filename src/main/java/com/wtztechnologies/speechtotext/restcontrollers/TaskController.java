package com.wtztechnologies.speechtotext.restcontrollers;

import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.TaskStatus;
import com.wtztechnologies.speechtotext.models.task.GenerateDocumentRequestModel;
import com.wtztechnologies.speechtotext.models.task.TaskUpdateModel;
import com.wtztechnologies.speechtotext.services.TaskService;
import com.wtztechnologies.speechtotext.services.TranscribeService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    private final TranscribeService transcribeService;

    @PreAuthorize("hasAnyAuthority('WORKER','TEAM_LEAD','TEMP_TEAM_LEAD')")
    @PostMapping("/create-task")
    public ResponseEntity<?> createTask(@RequestParam Long userId, @RequestBody MultipartFile audio)
            throws Exception {
        // 1. Primeste audio file
        // 2. Salveaza audio file in AUDIO_FILES
        // 3. Daca nu e .wav face conversie
        // 4. Trimite la python
        taskService.createTask(userId, audio);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/get-task-by-id")
    public ResponseEntity<?> getTaskById(@RequestParam Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping("/get-all-user-task")
    public ResponseEntity<?> getAllUserTasks(@RequestParam TaskStatus taskStatus, @RequestParam Long userId,
                                             Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllUserTasks(taskStatus, userId, pageable));
    }

    @PreAuthorize("hasAnyAuthority('SUPERVISOR','ADMIN')")
    @GetMapping("/get-all-workers-task")
    public ResponseEntity<?> getAllWorkersTasks(@RequestParam(required = false) TaskStatus taskStatus, Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllWorkersTasks(taskStatus, pageable));
    }

    @PreAuthorize("hasAnyAuthority('SUPERVISOR','ADMIN')")
    @GetMapping("/get-all-teamLead-task")
    public ResponseEntity<?> getAllTeamLeadTasks(@RequestParam(required = false) TaskStatus taskStatus, Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTeamLeadTasks(taskStatus, pageable));
    }

    @GetMapping("/get-all-team-task")
    public ResponseEntity<?> getAllTeamTasks(@RequestParam Long teamId,@RequestParam TaskStatus taskStatus, @RequestParam Long teamLeadId,
                                             Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTeamTasks(teamId,taskStatus, teamLeadId, pageable));
    }

    @GetMapping("/get-audio-from-task")
    public ResponseEntity<?> getAudio(@RequestParam Long taskId) {
        byte[] response = taskService.getAudio(taskId);
        return ResponseEntity.ok().contentLength(response.length).contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(response);
    }

    @PostMapping("/update-task-status")
    public ResponseEntity<?> updateTaskStatus(@RequestParam Long taskId, @RequestParam TaskStatus taskStatus) {
        taskService.updateTaskStatus(taskId, taskStatus);
        return ResponseEntity.ok("Task updated");
    }
    @PreAuthorize("hasAnyAuthority('WORKER','TEAM_LEAD')")
    @PutMapping("/update-task")
    public ResponseEntity<?> updateTask(@RequestBody TaskUpdateModel updateModel) {
        taskService.updateTask(updateModel);
        return ResponseEntity.ok("Task updated.");
    }

    @GetMapping("/get-task-num")
    public ResponseEntity<?> getTaskNum(@RequestParam Long userId) {
        return ResponseEntity.ok(taskService.getTaskNum(userId));
    }

    @GetMapping("/get-team-task-num")
    public ResponseEntity<?> getTeamTaskNum(@RequestParam Long teamLeadId,@RequestParam Long teamId) {
        return ResponseEntity.ok(taskService.getTeamTaskNum(teamLeadId,teamId));
    }

    @GetMapping("/get-role-task-num")
    public ResponseEntity<?> getRoleTaskNum(@RequestParam Role role) {
        return ResponseEntity.ok(taskService.getRoleTaskNum(role));
    }

    @PostMapping("/generate-document")
    public ResponseEntity<?> convert(@RequestBody GenerateDocumentRequestModel generateDocumentRequestModel) throws Exception {
        return ResponseEntity.ok(transcribeService.getPythonBase64Document(generateDocumentRequestModel));
    }
}
