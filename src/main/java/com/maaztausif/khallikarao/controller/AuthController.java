package com.maaztausif.khallikarao.controller;

import com.maaztausif.khallikarao.dto.request.LoginRequest;
import com.maaztausif.khallikarao.dto.request.SignupRequest;
import com.maaztausif.khallikarao.dto.response.LoginResponse;
import com.maaztausif.khallikarao.dto.response.SignupResponse;
import com.maaztausif.khallikarao.service.AuthService.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService service;

    //Jwt service

    //Aunthentication Manager


    @PostMapping("api/auth/login")
    @ResponseBody
    public LoginResponse signIn(@RequestBody LoginRequest request){
        return service.login(request);
    }

    @PostMapping("api/auth/signup")
    @ResponseBody
    public SignupResponse signUp(@RequestBody SignupRequest request){
        return  service.registerUser(request);
    }

}
