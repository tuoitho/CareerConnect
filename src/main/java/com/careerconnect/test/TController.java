package com.careerconnect.test;

import com.careerconnect.util.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("")
public class TController {
    @GetMapping("/api/test")
    public String test(Principal principal) {
        Logger.log("Principal: " + principal);
        Logger.log("Name: " + principal.getName());
        return "Test";
    }
    @GetMapping("/api/test2")
    public String test2(Principal principal) {
        Logger.log("Principal: " + principal);
        Logger.log("Name: " + principal.getName());
        return "Test2";
    }
}
