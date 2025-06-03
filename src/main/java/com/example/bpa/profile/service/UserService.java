package com.example.bpa.profile.service;

import com.example.bpa.profile.dto.UserDto;
import com.example.bpa.profile.entity.Users;
import com.example.bpa.profile.repository.UserRepository;
import com.example.bpa.utils.PasswordHasher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDto save(UserDto user) {
        Users users = modelMapper.map(user, Users.class);
        final String hashPassword = PasswordHasher.hashPassword("123");
        users.setPassword(hashPassword);
        final Users saved = userRepository.save(users);
        return modelMapper.map(saved, UserDto.class);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDto.class))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    public void delete(Long id) {
        userRepository.findById(id)
                .ifPresent(userRepository::delete);
    }
}
