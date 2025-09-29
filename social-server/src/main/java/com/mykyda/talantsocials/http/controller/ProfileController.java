package com.mykyda.talantsocials.http.controller;

import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import com.mykyda.talantsocials.dto.ProfileDTO;
//import com.mykyda.talantsocials.service.ApisService;
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

    @GetMapping("/{profileTag}")
    public ProfileDTO getProfile(@PathVariable(required = false) String profileTag) {
        return profileTag == null ? profileService.getCurrentProfile() : profileService.getProfileByTag(profileTag);
    }

    //kafka event !
    @PostMapping
    public ResponseEntity<String> createProfile(@RequestBody ProfileCreationDTO profileCreationDTO, @AuthenticationPrincipal Jwt jwt) {
        jwt.getClaims().forEach((a,b)-> System.out.println(a + " " + b));
        return new ResponseEntity<>(creationImitatorService.createProfile(Long.valueOf(jwt.getClaims().get("id").toString()),profileCreationDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/patch")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileDTO profileDTO) {
        profileService.patchProfileById(profileDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patchTag")
    public ResponseEntity<String> updateTag(@RequestBody ProfileDTO profileDTO) {
        profileService.patchTagById(profileDTO);
        return ResponseEntity.noContent().build();
    }
}
