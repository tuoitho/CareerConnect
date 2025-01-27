package com.careerconnect.service;

import com.careerconnect.dto.common.MailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service

public class MailService {
	@Autowired
	private JavaMailSender javaMailSender;

    public void send(MailDTO mailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailDTO.getFrom());
        message.setTo(mailDTO.getTo());
        message.setSubject(mailDTO.getSubject());
        message.setText(mailDTO.getText());
        javaMailSender.send(message);
    }
}