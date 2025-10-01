package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.ProfileDTO;
import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import com.mykyda.talantsocials.service.CreationImitatorService;
import com.mykyda.talantsocials.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    private final CreationImitatorService creationImitatorService;

//    private final ApisService apisService;

    @GetMapping()
    public ProfileDTO getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {
        return profileService.getCurrentProfile(Long.valueOf(jwt.getClaims().get("id").toString()));
    }

    @GetMapping("/get-by-tag/{profileTag}")
    public ProfileDTO getProfile(@PathVariable(required = false) String profileTag) {
        return profileService.getProfileByTag(profileTag);
    }

    //kafka event !
    @PostMapping
    public void createProfile(@RequestBody ProfileCreationDTO profileCreationDTO,
                                                @AuthenticationPrincipal Jwt jwt) {
        creationImitatorService.createProfile(profileCreationDTO);
    }

    @PatchMapping("/patch-profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileDTO profileDTO,
                                                @AuthenticationPrincipal Jwt jwt) {
        profileService.patchProfileByUserId(Long.valueOf(jwt.getClaims().get("id").toString()), profileDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patch-tag")
    public ResponseEntity<String> updateTag(@RequestBody ProfileDTO profileDTO,
                                            @AuthenticationPrincipal Jwt jwt) {
        profileService.patchTagByUserId(Long.valueOf(jwt.getClaims().get("id").toString()), profileDTO);
        return ResponseEntity.noContent().build();
    }
}
