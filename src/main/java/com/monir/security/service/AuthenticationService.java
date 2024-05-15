package com.monir.security.service;

import com.monir.security.config.JwtService;
import com.monir.security.dto.AuthenticationRequestDto;
import com.monir.security.dto.AuthenticationResponseDto;
import com.monir.security.dto.RegistrationRequestDto;
import com.monir.security.entity.User;
import com.monir.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponseDto register(RegistrationRequestDto requestDto) {
        var user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(requestDto.getRole())
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        // saveUserToken(savedUser, jwtToken); // Todo:: need to be implement in next
        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );
        var user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        // revokeAllUserTokens(user); // Todo:: need to be implement in next
        // saveUserToken(user, jwtToken); // Todo:: need to be implement in next
        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .build();
    }


}
