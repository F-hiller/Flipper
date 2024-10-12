package com.ovg.flipper.repository;

public interface JwtRepository {

  void save(String token, Long userId);

  boolean exists(String token);

  void delete(String token);

  Long get(String token);
}