package com.merchant.management.service;

import com.merchant.management.beans.Merchant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BackgroundJobService {

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundJobService.class);

    private final ScheduledExecutorService executorServiceForEmail = Executors.newScheduledThreadPool(5);
    private final ScheduledExecutorService executorServiceForNotification = Executors.newScheduledThreadPool(5);

    private final EmailService emailService;

    public void sendOtpEmail(Merchant merchant) {
        executorServiceForEmail.schedule(() -> {
            try {
                emailService.sendEmail(merchant.getEmailAddress(), "OTP Verification", "In order to verify your status please use OTP:" + merchant.getOtp());
                LOG.info("Email sent");
            } catch (MessagingException e) {
                LOG.warn("Exception occurred while sending email", e);
                throw new RuntimeException(e);
            }
        }, 0, TimeUnit.SECONDS);
    }

    public void sendWebhookNotification(Merchant merchant) {
        executorServiceForNotification.schedule(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> payload = new HashMap<>();
                payload.put("merchant", merchant.toJson());

                HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
                restTemplate.postForObject(merchant.getWebhookUrl(), request, String.class);
            } catch (Exception e) {
                LOG.warn("Exception occurred while sending notification to webhookURL", e);
            }
        }, 0, TimeUnit.SECONDS);
    }
}
