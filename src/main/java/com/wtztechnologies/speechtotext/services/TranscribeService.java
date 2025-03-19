package com.wtztechnologies.speechtotext.services;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.models.task.GenerateDocumentRequestModel;
import com.wtztechnologies.speechtotext.repositories.TaskRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TranscribeService {
    private final TaskRepository taskRepository;

    @Value("${paths.pythonGenerateDocumentUrl}")
    String pythonGenerateDocumentUrl;

    @Value("${paths.pythonTranscribeUrl}")
    String pythonTranscribeUrl;

    public TranscribeService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Map<String, Object> transcribeAudio(Resource file, Long taskId) {
        try {
            log.info("Calling method -- transcribeAudio --");
            System.out.println("Start Translate " + taskId.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body
                    = new LinkedMultiValueMap<>();
            body.add("audio", file);

            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            LocalDateTime startProcess = LocalDateTime.now();
            ResponseEntity<String> result = restTemplate.postForEntity(pythonTranscribeUrl, requestEntity, String.class);
            LocalDateTime endProcess = LocalDateTime.now();
            System.out.println(taskId.toString() + " Translate Done");
            updateTaskWithTranslate(taskId, result.getBody(), startProcess, endProcess);
            return new HashMap<String, Object>();
        } catch (Exception e) {
            System.out.println("Translate Failed.");
            updateTaskFailed(taskId);
            return null;
        }
    }

    public JSONObject getPythonBase64Document(GenerateDocumentRequestModel generateDocumentRequestModel) throws Exception {
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", generateDocumentRequestModel.getTitle());
            jsonObject.put("logs", generateDocumentRequestModel.getLogs());

            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), httpHeaders);
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonGenerateDocumentUrl, entity, Map.class);

            JSONObject json = new JSONObject(response.getBody());
            return json;
        }catch(Exception e){
            throw new Exception("Failed generate document");
        }
    }

    private void updateTaskWithTranslate(Long taskId, String translate, LocalDateTime startProcess, LocalDateTime endProcess) {
        Task task = taskRepository.findByIdRequired(taskId);

        task.setProcess("Successful");
        task.setTranslate(translate);
        Timestamp start = Timestamp.valueOf(startProcess.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Date dateStart = new Date(start.getTime());
        task.setStartProcess(dateStart);
        Timestamp end = Timestamp.valueOf(endProcess.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Date dateEnd = new Date(end.getTime());
        task.setEndProcess(dateEnd);

        taskRepository.save(task);
    }

    private void updateTaskFailed(Long taskId) {
        Task task = taskRepository.findByIdRequired(taskId);
        task.setProcess("Failed");
        taskRepository.save(task);
    }
}
