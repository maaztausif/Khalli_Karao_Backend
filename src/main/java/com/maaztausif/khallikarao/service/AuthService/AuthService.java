package com.maaztausif.khallikarao.service.AuthService;

import com.maaztausif.khallikarao.dto.request.LoginRequest;
import com.maaztausif.khallikarao.dto.response.LoginResponse;
import com.maaztausif.khallikarao.dto.response.SignupResponse;
import com.maaztausif.khallikarao.dto.request.SignupRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    LoginResponse login(LoginRequest request);
    SignupResponse registerUser(SignupRequest request);


}
