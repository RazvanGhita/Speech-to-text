package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.UsersOnVacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersOnVacationRepository extends JpaRepository<UsersOnVacation,Long> {
    UsersOnVacation findByTeamIdAndUsername(Long teamId,String username);
    List<UsersOnVacation> findAllByTempTeamLead(String username);

    Page<UsersOnVacation> findAllByUsernameContainingIgnoreCase(String keyword, Pageable pageable);

    List<UsersOnVacation> findAllByUsername(String username);

    UsersOnVacation findByTempTeamLead(String tempTeamLead);
}
