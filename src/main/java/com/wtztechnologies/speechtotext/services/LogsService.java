package com.wtztechnologies.speechtotext.services;

import com.wtztechnologies.speechtotext.entities.Logs;
import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.models.log.LogIntervalModel;
import com.wtztechnologies.speechtotext.models.task.TaskClickModel;
import com.wtztechnologies.speechtotext.repositories.LogsRepository;
import com.wtztechnologies.speechtotext.repositories.UserDetailsRepository;
import com.wtztechnologies.speechtotext.utils.PageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LogsService {
    private final ModelMapper modelMapper;
    private final LogsRepository logsRepository;
    private final PageConverter pageConverter;

    public Page<Logs> getLogsByUser(Date startDate, Date endDate, String username, Pageable pageable) {
        log.info("Calling method -- getAllLogs -- ");
        Logs logs = new Logs();
        getUser(logs);
        logs.setLog("User-ul " + logs.getUsername() + " a apelat metoda de observare a log-urilor user-ului " + username);
        logsRepository.save(logs);
        if (startDate == null && endDate == null) {
            Page<Logs> logsPage = logsRepository.findAllByUsername(username, pageable);
            return pageConverter.convert(() -> logsPage, this::createLog);
        } else if (startDate != null && endDate == null) {
            Page<Logs> logsPage = logsRepository.findAllByStartDateGreaterThanEqual(username, startDate, pageable);
            return pageConverter.convert(() -> logsPage, this::createLog);
        } else if (startDate == null && endDate != null) {
            Page<Logs> logsPage = logsRepository.findAllByEndDateLessThanEqual(username, endDate, pageable);
            return pageConverter.convert(() -> logsPage, this::createLog);
        } else {
            Page<Logs> logsPage = logsRepository.findAllByDateBetween(username, startDate, endDate, pageable);
            return pageConverter.convert(() -> logsPage, this::createLog);
        }
    }

    public void taskClickLog(TaskClickModel taskClickModel) {
        log.info("Calling method -- taskClick");
        Logs logs = new Logs();
        getUser(logs);
        logs.setLog("User-ul " + logs.getUsername()
                + " a apasat "
                + taskClickModel.getAction()
                + " la minutul "
                + taskClickModel.getDuration()
                + " pentru task-ul "
                + taskClickModel.getAudioName()
                + " .");
        logsRepository.save(logs);
    }

    public void getUser(Logs logs) {
        UserDetails user = modelMapper.map(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), UserDetails.class);
        logs.setUsername(user.getUsername());
        logs.setDate(LocalDateTime.now());
    }

    private Logs createLog(Logs logs) {
        log.info("Calling method -- createLog -- ");
        return modelMapper.map(logs, Logs.class);
    }
}
