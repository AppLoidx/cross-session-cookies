package com.apploidxxx.crosssessioncookies.controller;

import com.apploidxxx.crosssessioncookies.entity.Session;
import com.apploidxxx.crosssessioncookies.entity.SessionRepo;
import com.apploidxxx.crosssessioncookies.util.RequestUtil;
import com.apploidxxx.crosssessioncookies.util.TokenCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthApi {

    private final SessionRepo sessionRepo;

    public AuthApi(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @GetMapping
    public String getSessionCookie(
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        log.info("New request received");
        log.info("User-Agent: " + userAgent);
        String ip = RequestUtil.getClientIpAddr(request);
        log.info("User IP Address: " + ip);
        Session session = new Session(userAgent, ip, TokenCodeGenerator.generateToken());
        sessionRepo.save(session);

        return session.getToken();
    }
}
