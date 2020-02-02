package com.apploidxxx.crosssessioncookies.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Session(String userAgent, String ip, String token){

        this.userAgent = userAgent;
        this.ip = ip;
        this.token = token;
    }

    private String userAgent;
    private String ip;
    private String token;
}
