package com.example.bpa.modules.authorizationserver.service;

import com.example.bpa.modules.authorizationserver.entity.Oauth2User;
import com.example.bpa.modules.authorizationserver.repository.Oauth2UserRepository;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Oauth2UserDetailsService implements UserDetailsService, CommandLineRunner {
  private final PasswordEncoder passwordEncoder;
  private final Oauth2UserRepository userRepository;

  public Oauth2UserDetailsService(
      PasswordEncoder passwordEncoder, Oauth2UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    Oauth2User user = new Oauth2User();
    user.setUsername("admin");
    user.setPassword(passwordEncoder.encode("admin")); // Hash the password
    user.setRoles("ROLE_ADMIN");
    if (userRepository.findByUsername("admin").isEmpty()) {
      userRepository.save(user);
    }
  }

  /**
   * Locates the user based on the username. In the actual implementation, the search may be
   * case-sensitive, or case-insensitive depending on how the implementation instance is configured.
   * In this case, the <code>UserDetails</code> object that comes back may have a username that is
   * of a different case than what was actually requested.
   *
   * @param username the username identifying the user whose data is required.
   * @return a fully populated user record (never <code>null</code>)
   * @throws UsernameNotFoundException if the user could not be found or the user has no
   *     GrantedAuthority
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.userRepository
        .findByUsername(username)
        .map(
            oauth2User ->
                new User(
                    oauth2User.getUsername(),
                    oauth2User.getPassword(),
                    Arrays.stream(oauth2User.getRoles().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList()))
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
