package com.wtztechnologies.speechtotext.repositories;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.exceptions.SpeechPlatformException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    default UserDetails findByIdRequired(Long id) {
        return findById(id).orElseThrow(() -> new SpeechPlatformException(HttpStatus.NOT_FOUND,
                "The user with id: " + id + ", doesn't exist. "));
    }



    default UserDetails findByUsernameRequired(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new SpeechPlatformException(HttpStatus.NOT_FOUND,
                        "The user with username: " + username + ", doesn't exist. "));
    }

    @Query(value = "Select * from speech_to_text.user_details where convert(username, binary) = convert( ?1, binary)", nativeQuery = true)
    Optional<UserDetails> findByUsername(String username);

    @Query(value = "SELECT * FROM speech_to_text.user_details where role = ?1 and user_status = ?2 and has_team = ?3 and (username like '%' ?4 '%') "
            +
            "ORDER BY CASE WHEN ?5 = 'ASC'  THEN username END ASC"
            + ", CASE WHEN ?5 = 'DESC' THEN username END DESC", nativeQuery = true)
    List<UserDetails> findByUsernameOrdered(String role, String userStatus, Boolean hasTeam, String keyword,
                                                       String sortOrder);

    List<UserDetails> findAllByRole(Role role);

    @Query(value =  "SELECT * FROM speech_to_text.user_details where role = ?1 and user_status = ?2 and (username like '%' ?3 '%')",nativeQuery = true)
    Page<UserDetails> findByUsernameAndRoleAndUserStatus(String role,String userStatus,String keyword,Pageable pageable);

    @Query(value =  "SELECT * FROM speech_to_text.user_details where role = ?1 and user_status = ?2 and (username like '%' ?3 '%')",nativeQuery = true)
    List<UserDetails> findByUsernameAndRoleAndUserStatus(String role,String userStatus,String keyword);

    @Query(value =  "SELECT * FROM speech_to_text.user_details where user_status = ?1 and (username like '%' ?2 '%')",nativeQuery = true)
    Page<UserDetails> findByUsernameAndUserStatus(String userStatus,String keyword,Pageable pageable);

    @Query(value =  "SELECT * FROM speech_to_text.user_details where role = ?1  and (username like '%' ?2 '%')",nativeQuery = true)
    Page<UserDetails> findByUsernameAndRole(String role,String keyword,Pageable pageable);

    @Query(value =  "SELECT * FROM speech_to_text.user_details where username like '%' ?1 '%'",nativeQuery = true)
    Page<UserDetails> findByUsername(String keyword,Pageable pageable);

}
