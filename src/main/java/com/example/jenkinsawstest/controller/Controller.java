package com.example.jenkinsawstest.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compounder")
public class Controller {


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public String test(){
        return "tets";
    }
}
