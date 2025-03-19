package com.wtztechnologies.speechtotext.restcontrollers;

import com.wtztechnologies.speechtotext.models.user.UsersOnVacationMemberRequestModel;
import com.wtztechnologies.speechtotext.models.user.UsersOnVacationRequestModel;
import com.wtztechnologies.speechtotext.services.UsersOnVacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vacation")
public class UsersOnVacationController {

    private final UsersOnVacationService usersOnVacationService;

    @GetMapping("/get-all-users-on-vacation")
    public ResponseEntity<?> getAllUsersOnVacation(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(usersOnVacationService.getAllUsersOnVacation(keyword, pageable));
    }

    @PostMapping("/schedule-vacation")
    public ResponseEntity<?> scheduleVacation(@RequestBody UsersOnVacationRequestModel usersOnVacationRequestModel) throws Exception {
        usersOnVacationService.scheduleTeamLeadVacation(usersOnVacationRequestModel);
        return ResponseEntity.ok("Successfully scheduled vacation.");
    }

    @PostMapping("/schedule-member-vacation")
    public ResponseEntity<?> scheduleMemberVacation(@RequestBody UsersOnVacationMemberRequestModel usersOnVacationMemberRequestModel){
        usersOnVacationService.scheduleVacation(usersOnVacationMemberRequestModel);
        return ResponseEntity.ok("Successfully scheduled vacation.");
    }
}
