package com.example.bpa.modules.authorizationserver.repository;

import com.example.bpa.modules.authorizationserver.entity.Oauth2User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2UserRepository extends JpaRepository<Oauth2User, Long> {

  Optional<Oauth2User> findByUsername(String username);
}
