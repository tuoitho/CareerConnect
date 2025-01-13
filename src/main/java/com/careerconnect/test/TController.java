package com.careerconnect.test;

import com.careerconnect.util.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test")
public class TController {
    @GetMapping("")
    public String test(Principal principal) {
        Logger.log("Principal: " + principal);
        Logger.log("Name: " + principal.getName());
        return "Test";
    }
}
