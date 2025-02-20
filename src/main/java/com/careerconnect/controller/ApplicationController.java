package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoint.PREFIX+"/application")
@RequiredArgsConstructor
public class ApplicationController {
}
