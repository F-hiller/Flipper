package com.ovg.flipper.repository;

public interface JwtRepository {
    void save(String userName, String token);
    boolean exists(String userId);
    void delete(String userId);
    String get(String userId);
}
