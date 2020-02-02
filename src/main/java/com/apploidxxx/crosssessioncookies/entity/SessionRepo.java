package com.apploidxxx.crosssessioncookies.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepo extends JpaRepository<Session, Long> {
    Optional<Session> findByToken(String token);
}
