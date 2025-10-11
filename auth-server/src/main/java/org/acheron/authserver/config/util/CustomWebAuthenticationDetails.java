package org.acheron.authserver.config.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private String verificationCode;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        verificationCode = request.getParameter("2facode");
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}