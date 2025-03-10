package com.careerconnect.atest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControllerrrrrrrr {
//    private final JobService2 jobService2;
//
//    public TestControllerrrrrrrr(JobService2 jobService2) {
//        this.jobService2 = jobService2;
//    }

    @GetMapping("/api/tttt")
    public String test() {
//        jobService2.createJob();
        return "OK";
    }
}
