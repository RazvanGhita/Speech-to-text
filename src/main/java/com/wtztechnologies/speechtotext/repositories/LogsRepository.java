package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.Logs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface LogsRepository extends JpaRepository<Logs, Long> {

    Page<Logs> findAll(Pageable pageable);

    @Query(value = "Select * from speech_to_text.logs where username = ?1 and date >= ?2", nativeQuery = true)
    Page<Logs> findAllByStartDateGreaterThanEqual(String username,Date startDate, Pageable pageable);
    @Query(value = "Select * from speech_to_text.logs where username = ?1 and date <= ?2", nativeQuery = true)
    Page<Logs> findAllByEndDateLessThanEqual(String username, Date endDate, Pageable pageable);
    @Query(value = "Select * from speech_to_text.logs where username = ?1 and date >= ?2 and date <= ?3", nativeQuery = true)
    Page<Logs> findAllByDateBetween(String username,Date startDate,Date endDate, Pageable pageable);

    Page<Logs> findAllByUsername(String username, Pageable pageable);
}
