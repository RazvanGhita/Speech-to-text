package com.wtztechnologies.speechtotext.restcontrollers;

import com.wtztechnologies.speechtotext.enums.Role;
import com.wtztechnologies.speechtotext.enums.SortOrder;
import com.wtztechnologies.speechtotext.enums.UserStatus;
import com.wtztechnologies.speechtotext.models.user.UserActivationModel;
import com.wtztechnologies.speechtotext.models.user.UserDetailsRequestModel;
import com.wtztechnologies.speechtotext.models.user.UserDetailsUpdateModel;
import com.wtztechnologies.speechtotext.models.user.UserPasswordChangeModel;
import com.wtztechnologies.speechtotext.services.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;


    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/add-member")
    public ResponseEntity<?> addMember(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        return ResponseEntity.ok(userDetailsService.addMember(userDetailsRequestModel));
    }


    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/unblock-user")
    public ResponseEntity<?> unblockUser(@RequestParam Long userId) {
        userDetailsService.unblockUser(userId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PutMapping("/deactivate-member")
    public void deactivateMember(@RequestParam("userId") Long userId) throws Exception {
        try {
            userDetailsService.deactivateMember(userId);
        } catch (Exception e) {
            throw new Exception("Could not deactivate the user with id " + userId);
        }
    }

    @PutMapping("/update-member")
    public void updateMember(@RequestBody UserDetailsUpdateModel userDetailsUpdateModel) {

        userDetailsService.updateMember(userDetailsUpdateModel);

    }

    @GetMapping("/verify-member-status")
    public ResponseEntity<String> verifyStatus(@RequestParam("username") String username) {
        return ResponseEntity.ok(userDetailsService.verifyStatus(username));
    }

    @PostMapping("/activate-user")
    public ResponseEntity<?> activateUser(@RequestBody UserActivationModel userActivationModel) {
        return ResponseEntity.ok(userDetailsService.activateUser(userActivationModel));
    }

    @PostMapping("/inactivate-user")
    public ResponseEntity<?> inactivateUser(@RequestParam("userId") Long userId) throws Exception {
        return ResponseEntity.ok(userDetailsService.inactivateUser(userId));
    }


    @GetMapping("/get-available-users")
    public ResponseEntity<?> getAvailableUsers(@RequestParam("keyword") String keyword, @RequestParam SortOrder sortOrder) {
        return ResponseEntity.ok(userDetailsService.getAvailableUsers(keyword, sortOrder));
    }

    @GetMapping("/get-all-workers")
    public ResponseEntity<?> getAllWorkers(@RequestParam("keyword") String keyword, @RequestParam SortOrder sortOrder) {
        return ResponseEntity.ok(userDetailsService.getAllWorkers(keyword, sortOrder));
    }

    @GetMapping("/get-user-by-id")
    public ResponseEntity<?> getUserById(@RequestParam Long userId) {
        return ResponseEntity.ok(userDetailsService.getUserById(userId));
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) Role role, @RequestParam(required = false) UserStatus userStatus, @RequestParam(required = false) String keyword, Pageable pageable) {
        return ResponseEntity.ok(userDetailsService.getAllUsers(role, userStatus, keyword, pageable));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserPasswordChangeModel userPasswordChangeModel) {
        if (userDetailsService.changePassword(userPasswordChangeModel)) {
            return ResponseEntity.ok("Password changed!");
        } else {
            return new ResponseEntity<>("Passwords do not match!",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
