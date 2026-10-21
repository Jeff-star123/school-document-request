package com.phinma.docrequest.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final EmailService emailService;
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    public OtpService(EmailService emailService) {
        this.emailService = emailService;
    }

    private static class OtpData {
        String code;
        long expiryTime;

        OtpData(String code, long expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }

    public String generateAndSendOtp(String email, String purpose) {
        String code = String.format("%06d", new Random().nextInt(999999));
        // Valid for 5 minutes
        long expiry = System.currentTimeMillis() + (5 * 60 * 1000);
        otpStorage.put(email.toLowerCase().trim(), new OtpData(code, expiry));

        String message = "Greetings from Saint Jude College Manila (PHINMA Education).\n\n"
                + "Your One-Time Password (OTP) for " + purpose + " is: " + code + "\n\n"
                + "This OTP is valid for 5 minutes. Do not share this code with anyone.";
        emailService.sendEmail(email, "SJC Manila - Verification OTP: " + code, message);

        return code;
    }

    public boolean verifyOtp(String email, String inputCode) {
        if (email == null || inputCode == null) return false;
        OtpData data = otpStorage.get(email.toLowerCase().trim());
        if (data == null) return false;

        if (System.currentTimeMillis() > data.expiryTime) {
            otpStorage.remove(email.toLowerCase().trim());
            return false;
        }

        if (data.code.equals(inputCode.trim())) {
            otpStorage.remove(email.toLowerCase().trim());
            return true;
        }
        return false;
    }
}