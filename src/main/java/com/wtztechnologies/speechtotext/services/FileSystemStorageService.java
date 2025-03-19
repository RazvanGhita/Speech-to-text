package com.wtztechnologies.speechtotext.services;

import java.io.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.sound.sampled.*;

import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.enums.AudioExtention;
import com.wtztechnologies.speechtotext.exceptions.FileNotFoundException;
import com.wtztechnologies.speechtotext.exceptions.StorageException;

import com.wtztechnologies.speechtotext.repositories.TaskRepository;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.util.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



@Service
@ConfigurationProperties(prefix = "storage")
public class FileSystemStorageService {

    private String location;

    @Value("${ffmpegPath}")
    private String ffmpegPath;

    private Path rootLocation;

    private final TaskRepository taskRepository;

    private Date today = new Date();

    public FileSystemStorageService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(location);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public String getFileExtension(MultipartFile multipartFile) {
        return FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    }

    public String storeAudioMultipartFile(MultipartFile file, String section) {
        try {
            rootLocation = Paths.get(File.separator + section);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
        String filename = "";
        if (taskRepository.findTopByAudioFileName(file.getOriginalFilename()) == null) {
            filename = StringUtils
                    .cleanPath(file.getOriginalFilename());
        } else {
            Task task = taskRepository.findTopByAudioFileName(file.getOriginalFilename());
            if (task.getAudioFileName().isEmpty()) {
                filename = StringUtils
                        .cleanPath(file.getOriginalFilename());
            } else {
                int i = 1;
                filename = StringUtils
                        .cleanPath(file.getOriginalFilename());
                int indexLastDot = filename.lastIndexOf('.');
                String aux = filename.substring(0, indexLastDot);
                String extension = filename.substring(indexLastDot);
                filename = aux + "(" + i + ")" + extension;
                Task task1 = taskRepository.findTopByAudioFileName(filename);
                if (task1 != null) {
                    while (task1 != null) {
                        i++;
                        indexLastDot = filename.lastIndexOf('.');
                        if (i <= 10) {
                            aux = filename.substring(0, indexLastDot - 3);
                            extension = filename.substring(indexLastDot);
                            filename = aux + "(" + i + ")" + extension;
                            task1 = taskRepository.findTopByAudioFileName(filename);
                        } else {
                            aux = filename.substring(0, indexLastDot - 4);
                            extension = filename.substring(indexLastDot);
                            filename = aux + "(" + i + ")" + extension;
                            task1 = taskRepository.findTopByAudioFileName(filename);
                        }
                    }
                }
            }
        }
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory " + filename);
            }
            File storedFile = new File(location+ filename);
            if (!storedFile.exists()) {
                try (InputStream inputStream = file.getInputStream()) {
                    file.transferTo(storedFile);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }

    public Resource loadAsResourceAudioFile(String filename, String section) {
        try {
            rootLocation = Paths.get(location + File.separator);
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public String getAudioFileDuration(MultipartFile file)
            throws IOException, SAXException, TikaException, UnsupportedAudioFileException {
        String ext = getFileExtension(file);
        AudioInputStream audioInputStream = null;
        InputStream stream = file.getInputStream();
        InputStream buuf = new BufferedInputStream(stream);
        if (ext.equals(AudioExtention.wav.name())) {

            audioInputStream = AudioSystem.getAudioInputStream(buuf);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            Double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            Long longDuration = Double.valueOf(durationInSeconds).longValue();
            String formattedDuration = String.format("%02d:%02d:%02d", longDuration / 3600, (longDuration % 3600) / 60,
                    (longDuration % 60));
            return formattedDuration;
        } else if (ext.equals(AudioExtention.mp3.name())) {
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(buuf, handler, metadata, parseCtx);
            buuf.close();
            Long duration = Long.parseLong(metadata.get("xmpDM:duration").split("\\.")[0]);
            String formattedDuration = String.format("%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60,
                    (duration % 60));
            return formattedDuration;
        } else {
            return "jhbjh";
        }

    }

    public MultipartFile ConvertFileToWAVE(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        int indexLastDot = filename.lastIndexOf('.');
        String aux = filename.substring(0, indexLastDot);
        String p = ffmpegPath + " -i " + file.getOriginalFilename() + " -vn " + aux + ".wav";

        Process process = Runtime.getRuntime().exec(p, null, new File(location));
        synchronized (process) {
            try {
                process.wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        File file1 = new File(location + aux + ".wav");
        FileItem fileItem = new DiskFileItem("morav", Files.probeContentType(file1.toPath()), false, file1.getName(), 1024 * 1024 * 1024, file1.getParentFile());
        try {
            InputStream input = new FileInputStream(file1);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
//            new FileInputStream(file1).transferTo(fileItem.getOutputStream());
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }

        return new CommonsMultipartFile(fileItem);
    }

}
