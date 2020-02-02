package com.apploidxxx.crosssessioncookies.controller;

import com.apploidxxx.crosssessioncookies.entity.Session;
import com.apploidxxx.crosssessioncookies.entity.SessionRepo;
import com.apploidxxx.crosssessioncookies.util.RequestUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserApi {
    private final SessionRepo sessionRepo;

    public UserApi(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @GetMapping
    public String getUser(
            @RequestParam("session") String token,
            HttpServletRequest request,
            @RequestHeader("User-Agent") String userAgent
    ){
        Optional<Session> sessionOptional;
        if ((sessionOptional = sessionRepo.findByToken(token)).isPresent()){

            Session session = sessionOptional.get();

            // I think it is good way to insert IP address check last
            if (session.getUserAgent().equals(userAgent) && session.getIp().equals(RequestUtil.getClientIpAddr(request))){
                return "Success";
            } else {
                return "You use another IP or another browser. Please re-auth.";
            }
        } else {
            return "Fail";
        }
    }
}
