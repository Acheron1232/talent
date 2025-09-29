//package com.mykyda.talantsocials.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//@Service
//public class ApisService {
//
//    public UserDataDTO request(String jwt) {
//        RestClient restClient = RestClient.create();
//        UserDataDTO dataDTO = restClient.get().uri("http://localhost:8080/user/userinfo").header("Authorization", "Bearer " + jwt)
//                .retrieve().body(UserDataDTO.class);
//        return dataDTO;
//    }
//
//    public record UserDataDTO(Long id, String username, String email){}
//}
