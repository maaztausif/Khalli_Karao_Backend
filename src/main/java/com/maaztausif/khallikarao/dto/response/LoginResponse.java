package com.maaztausif.khallikarao.dto.response;

import com.maaztausif.khallikarao.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private boolean status;
    private String message;
    private Boolean otpVerified;
    private UserData userData;

    @Data
    @AllArgsConstructor
    public static class UserData{
        private Long userId;
        private String email;
        private String fullName;
        private String token;
    }

}
