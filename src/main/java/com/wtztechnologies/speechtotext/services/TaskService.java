package com.wtztechnologies.speechtotext.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.wtztechnologies.speechtotext.entities.*;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.TaskStatus;
import com.wtztechnologies.speechtotext.models.task.*;
import com.wtztechnologies.speechtotext.repositories.LogsRepository;
import com.wtztechnologies.speechtotext.repositories.TaskRepository;
import com.wtztechnologies.speechtotext.repositories.TeamRepository;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;
import com.wtztechnologies.speechtotext.utils.Constants;
import com.wtztechnologies.speechtotext.utils.PageConverter;

import org.apache.poi.util.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TeamRepository teamRepository;

    private final ModelMapper modelMapper;
    private final UserDetailsRepository userDetailsRepository;
    private final TaskRepository taskRepository;
    private final PageConverter pageConverter;
    private final TranscribeService transcribeService;
    private final FileSystemStorageService fileSystemStorageService;
    private final TeamService teamService;
    private final LogsService logsService;
    private final LogsRepository logsRepository;

    public void createTask(Long userId, MultipartFile file)
            throws Exception {
        log.info("Calling method -- createTask -- ");
        UserDetails userDetails = userDetailsRepository.findByIdRequired(userId);
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de creare a unui task pentru user-ul " + userDetails.getUsername());
        logsRepository.save(logs);
        String filename = fileSystemStorageService.storeAudioMultipartFile(file, Constants.AUDIO_FILES_SECTION);
        if (fileSystemStorageService.getFileExtension(file).equals("wav")) {
            Task newTask = new Task();
            newTask.setTaskStatus(TaskStatus.NEW);
            newTask.setProcess("Pending");
            newTask.setUserId(userId);
            newTask.setAudioExtension(fileSystemStorageService.getFileExtension(file));
            newTask.setDuration(fileSystemStorageService.getAudioFileDuration(file));
            newTask.setAudioFileName(filename);
            taskRepository.save(newTask);
            if (newTask.getId() != null) {
                CompletableFuture.runAsync(
                        () -> transcribeService.transcribeAudio(fileSystemStorageService.loadAsResourceAudioFile(
                                newTask.getAudioFileName(), Constants.AUDIO_FILES_SECTION), newTask.getId()));
            }
        } else {
            MultipartFile fileWav = fileSystemStorageService.ConvertFileToWAVE(file);
            Task newTask = new Task();
            newTask.setTaskStatus(TaskStatus.NEW);
            newTask.setProcess("Pending");
            newTask.setUserId(userId);
            newTask.setAudioExtension("wav");
            filename = fileSystemStorageService.storeAudioMultipartFile(fileWav, Constants.AUDIO_FILES_SECTION);
            newTask.setDuration(fileSystemStorageService.getAudioFileDuration(fileWav));
            newTask.setAudioFileName(filename);
            taskRepository.save(newTask);
            if (newTask.getId() != null) {
                CompletableFuture.runAsync(
                        () -> transcribeService.transcribeAudio(fileSystemStorageService.loadAsResourceAudioFile(
                                newTask.getAudioFileName(), Constants.AUDIO_FILES_SECTION), newTask.getId()));
            }
        }

    }

    @Transactional(readOnly = true)
    public TaskResponseModel getTaskById(Long taskId) {
        log.info("Calling method -- getTaskById -- with param: {}", taskId);
        Task task = taskRepository.findByIdRequired(taskId);
        return modelMapper.map(task, TaskResponseModel.class);
    }

    public Page<TaskResponseModelWithExtension> getAllUserTasks(TaskStatus taskStatus, Long userId, Pageable pageable) {
        log.info("Calling method -- getAllUserTasks -- ");
        Page<Task> tasks = taskRepository.findAllByTaskStatusAndUserId(taskStatus, userId,
                pageable);

        return pageConverter.convert(() -> tasks, this::createTaskResponseModelWithExtension);
    }

    public Page<TaskResponseModelWithExtension> getAllWorkersTasks(TaskStatus taskStatus, Pageable pageable) {
        log.info("Calling method -- getAllWorkersTasks -- ");
        List<UserDetails> workers = userDetailsRepository.findAllByRole(Role.WORKER);
        List<UserDetails> workers2 = userDetailsRepository.findAllByRole(Role.TEMP_TEAM_LEAD);
        List<UserDetails> workersResponse = new ArrayList<>();
        workersResponse.addAll(workers);
        workersResponse.addAll(workers2);
        List<Task> tasks = new ArrayList<>();
        for (UserDetails worker : workersResponse) {
            tasks.addAll(taskRepository.findAllByTaskStatusAndUserId(taskStatus, worker.getId()));
        }
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), tasks.size());

//        PageImpl<Task> tasksRepsponseImpl = new PageImpl<>(tasks.subList(start, end), pageable, tasks.size());
        Page<Task> tasksResponse = new PageImpl<>(tasks.subList(start, end), pageable, tasks.size());
        return pageConverter.convert(() -> tasksResponse, this::createTaskResponseModelWithExtension);
    }

    public Page<TaskResponseModelWithExtension> getAllTeamLeadTasks(TaskStatus taskStatus, Pageable pageable) {
        log.info("Calling method -- getAllTeamLeadTasks -- ");
        List<UserDetails> workers = userDetailsRepository.findAllByRole(Role.TEAM_LEAD);
        List<Task> tasks = new ArrayList<>();
        for (UserDetails worker : workers) {
            tasks.addAll(taskRepository.findAllByTaskStatusAndUserId(taskStatus, worker.getId()));
        }
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), tasks.size());
        Page<Task> tasksResponse = new PageImpl<>(tasks.subList(start, end), pageable, tasks.size());
        return pageConverter.convert(() -> tasksResponse, this::createTaskResponseModelWithExtension);
    }

    public Page<TaskResponseModelWithExtension> getAllTeamTasks(Long teamId, TaskStatus taskStatus, Long teamLeadId,
                                                                Pageable pageable) {
        log.info("Calling method -- getAllTeamTasks -- ");
        List<Task> teamTasks = new ArrayList<Task>();
        Team team = teamRepository.findByIdRequired(teamId);
        teamTasks.addAll(taskRepository.findAllByTaskStatusAndUserId(taskStatus, teamLeadId));
        List<TeamMember> members = teamService.getTeamMembersByTeamId(teamId);
        for (TeamMember teamMember : members) {
            teamTasks.addAll(taskRepository.findAllByTaskStatusAndUserId(taskStatus, teamMember.getUser().getId()));
        }
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), teamTasks.size());
        Page<Task> tasks = new PageImpl<>(teamTasks.subList(start, end), pageable, teamTasks.size());

        return pageConverter.convert(() -> tasks, this::createTaskResponseModelWithExtension);
    }

    public TasksCount getTaskNum(Long userId) {
        log.info("Calling method -- getTaskNum");
        TasksCount tasksCountResponse = new TasksCount();
        List<Task> tasksNew = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.NEW, userId);
        tasksCountResponse.setTasksNew(tasksNew.size());
        List<Task> tasksInProgress = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.INPROGRESS, userId);
        tasksCountResponse.setTasksInProgress(tasksInProgress.size());
        List<Task> tasksVerified = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.VERIFIED, userId);
        tasksCountResponse.setTasksVerified(tasksVerified.size());
        List<Task> tasksAborted = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.ABORTED, userId);
        tasksCountResponse.setTasksAborted(tasksAborted.size());
        return tasksCountResponse;
    }

    public TasksCount getTeamTaskNum(Long teamLeadId,Long teamId) {
        log.info("Calling method -- getTeamTaskNum");
        TasksCount tasksCountResponse = new TasksCount();
        List<Task> tasksNew = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.NEW, teamLeadId);
        List<TeamMember> members = teamService.getTeamMembersByTeamId(teamId);
        for (TeamMember teamMember : members) {
            tasksNew.addAll(taskRepository.findAllByTaskStatusAndUserId(TaskStatus.NEW, teamMember.getUser().getId()));
        }
        tasksCountResponse.setTasksNew(tasksNew.size());
        List<Task> tasksInProgress = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.INPROGRESS, teamLeadId);
        for (TeamMember teamMember : members) {
            tasksInProgress.addAll(
                    taskRepository.findAllByTaskStatusAndUserId(TaskStatus.INPROGRESS, teamMember.getUser().getId()));
        }
        tasksCountResponse.setTasksInProgress(tasksInProgress.size());
        List<Task> tasksVerified = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.VERIFIED, teamLeadId);
        for (TeamMember teamMember : members) {
            tasksVerified.addAll(
                    taskRepository.findAllByTaskStatusAndUserId(TaskStatus.VERIFIED, teamMember.getUser().getId()));
        }
        tasksCountResponse.setTasksVerified(tasksVerified.size());
        List<Task> tasksAborted = taskRepository.findAllByTaskStatusAndUserId(TaskStatus.ABORTED, teamLeadId);
        for (TeamMember teamMember : members) {
            tasksAborted.addAll(
                    taskRepository.findAllByTaskStatusAndUserId(TaskStatus.ABORTED, teamMember.getUser().getId()));
        }
        tasksCountResponse.setTasksAborted(tasksAborted.size());
        return tasksCountResponse;
    }

    public TasksCount getRoleTaskNum(Role role) {
        log.info("Calling method -- getWorkersTaskNum");
        TasksCount tasksCountResponse = new TasksCount();
        List<UserDetails> workers = userDetailsRepository.findAllByRole(role);
        List<Task> tasksNew = new ArrayList<>();
        for (UserDetails worker : workers) {
            tasksNew.addAll(taskRepository.findAllByTaskStatusAndUserId(TaskStatus.NEW, worker.getId()));
        }
        tasksCountResponse.setTasksNew(tasksNew.size());
        List<Task> tasksInprogress = new ArrayList<>();
        for (UserDetails worker : workers) {
            tasksInprogress.addAll(taskRepository.findAllByTaskStatusAndUserId(TaskStatus.INPROGRESS, worker.getId()));
        }
        tasksCountResponse.setTasksInProgress(tasksInprogress.size());
        List<Task> tasksVerified = new ArrayList<>();
        for (UserDetails worker : workers) {
            tasksVerified.addAll(taskRepository.findAllByTaskStatusAndUserId(TaskStatus.VERIFIED, worker.getId()));
        }
        tasksCountResponse.setTasksVerified(tasksVerified.size());
        List<Task> tasksAborted = new ArrayList<>();
        for (UserDetails worker : workers) {
            tasksAborted.addAll(taskRepository.findAllByTaskStatusAndUserId(TaskStatus.ABORTED, worker.getId()));
        }
        tasksCountResponse.setTasksAborted(tasksAborted.size());
        return tasksCountResponse;
    }

    public byte[] getAudio(Long taskId) {
        log.info("Calling method -- getAudio -- with param: {}", taskId);
        Task task = taskRepository.findByIdRequired(taskId);
        byte[] audio = null;
        try {

            audio = IOUtils
                    .toByteArray(fileSystemStorageService.loadAsResourceAudioFile(task.getAudioFileName(),
                            Constants.AUDIO_FILES_SECTION).getInputStream());
        } catch (IOException e) {
            System.out.println("ERR");

        }
        return audio;
    }

    public void updateTaskStatus(Long taskId, TaskStatus taskStatus) {
        log.info("Calling method -- updateTaskStatus -- ");
        Task task = taskRepository.findByIdRequired(taskId);
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de modificare a statusului task-ului " + task.getAudioFileName() + " in statusul " + taskStatus);
        logsRepository.save(logs);
        task.setTaskStatus(taskStatus);
        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(TaskUpdateModel updateModel) {
        log.info("Calling method -- updateTask with id  -- " + updateModel.getId());
        Task task = taskRepository.findByIdRequired(updateModel.getId());
        Logs logs = new Logs();
        logsService.getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de modificare a task-ului " + task.getAudioFileName());
        logsRepository.save(logs);
        modelMapper.map(updateModel, task);
        taskRepository.save(task);
    }


    private TaskResponseModelWithExtension createTaskResponseModelWithExtension(Task task) {
        log.info("Calling method -- createTaskResponseModel -- ");
        TaskResponseModelWithExtension taskResponse = modelMapper.map(task, TaskResponseModelWithExtension.class);
        return taskResponse;
    }

}
