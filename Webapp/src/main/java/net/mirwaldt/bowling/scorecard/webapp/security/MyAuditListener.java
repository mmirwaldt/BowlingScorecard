package net.mirwaldt.bowling.scorecard.webapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyAuditListener {
    Logger logger = LoggerFactory.getLogger("audit.log");

    @EventListener(condition = "#event.auditEvent.type == 'AUTHENTICATION_FAILURE'")
    public void onAuthFailure(AuditApplicationEvent event) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        logger.info("Auth failure from remote IP address: " + ipAddress);
    }
}