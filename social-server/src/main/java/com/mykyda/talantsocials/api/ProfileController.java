package com.mykyda.talantsocials.api;

import com.mykyda.talantsocials.dto.patch.ProfilePatchDTO;
import com.mykyda.talantsocials.dto.patch.ProfilePatchTagDTO;
import com.mykyda.talantsocials.dto.response.ProfileDTO;
import com.mykyda.talantsocials.dto.create.ProfileCreationDTO;
import com.mykyda.talantsocials.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

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
    public ResponseEntity<String> createProfile(@RequestBody ProfileCreationDTO profileCreationDTO) {
        profileService.createProfile(profileCreationDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patch-profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfilePatchDTO profileDTO,
                                                @AuthenticationPrincipal Jwt jwt) {
        profileService.patchProfile(Long.valueOf(jwt.getClaims().get("id").toString()), profileDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/patch-tag")
    public ResponseEntity<String> updateTag(@RequestBody ProfilePatchTagDTO profileDTO,
                                            @AuthenticationPrincipal Jwt jwt) {
        profileService.patchTag(Long.valueOf(jwt.getClaims().get("id").toString()), profileDTO);
        return ResponseEntity.noContent().build();
    }


}
