package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.Team;
import com.wtztechnologies.speechtotext.exceptions.SpeechPlatformException;
import com.wtztechnologies.speechtotext.models.user.UserDetailsResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    default Team findByIdRequired(Long id) {
        return findById(id).orElseThrow(() ->
                new SpeechPlatformException(HttpStatus.NOT_FOUND,
                        "The team with id: " + id + ", doesn't exist. "));
    }
    default Team findByTeamLeadIdRequired(Long id) {
        return findByTeamLeadId(id).orElseThrow(() ->
                new SpeechPlatformException(HttpStatus.NOT_FOUND,
                        "The team with teamLeadId: " + id + ", doesn't exist. "));
    }
    List<Team> findAllByTeamLeadId(Long teamLeadId);
     Optional<Team> findByTeamLeadId(Long teamLeadId);

     @Query(value = "SELECT * FROM speech_to_text.team where name like '%' ?1 '%'",nativeQuery = true)
     Page<Team> findAllByTeamName(String keyword, Pageable pageable);
    UserDetailsResponseModel findTeamLeadById(Long teamId);
}
