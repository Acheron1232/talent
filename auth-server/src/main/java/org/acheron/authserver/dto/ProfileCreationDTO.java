package org.acheron.authserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCreationDTO{
    Long id;
    String displayName;
    String tag;
    String profilePictureUrl;
    }



