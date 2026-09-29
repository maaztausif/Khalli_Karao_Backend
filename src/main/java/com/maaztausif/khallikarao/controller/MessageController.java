package com.maaztausif.khallikarao.controller;

import com.maaztausif.khallikarao.service.Home.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService service;

    public List<MessageService.CategoryResponse> categories(){
        return service.categories;
    }

}
