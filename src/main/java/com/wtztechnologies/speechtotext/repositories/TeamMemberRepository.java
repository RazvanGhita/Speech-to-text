package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.Team;
import com.wtztechnologies.speechtotext.entities.TeamMember;
import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.exceptions.SpeechPlatformException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    default TeamMember findByIdRequired(Long id) {
        return findById(id).orElseThrow(() ->
                new SpeechPlatformException(HttpStatus.NOT_FOUND,
                        "The team member with the id: " + id + ", doesn't exist. "));
    }

    default TeamMember findByUserIdRequired(Long id) {
        return findByUserId(id).orElseThrow(() ->
                new SpeechPlatformException(HttpStatus.NOT_FOUND,
                        "The team member with the userId: " + id + ", doesn't exist. "));
    }


    Optional<TeamMember> findByUserId(Long userId);

    List<TeamMember> findByUser(UserDetails user);

    @Query(value = "SELECT *  FROM speech_to_text.team_member where team_id =?1 ;", nativeQuery = true)
    List<TeamMember> findAllMembersByTeam(Long teamId);

    List<TeamMember> findAllByTeam(Team team);

    @Query(value = "SELECT * FROM speech_to_text.team_member where user_id =?1",nativeQuery = true)
    List<TeamMember> findTeamsByUserId(Long userId);
}
