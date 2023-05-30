package ru.graduatework.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.AuthenticationResponseDto;
import ru.graduatework.controller.dto.AuthorRequestDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.jooq.tables.records.AuthorRecord;
import ru.graduatework.mapper.UserDtoMapper;
import ru.graduatework.repository.AuthorRepository;
import ru.graduatework.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final PostgresOperatingDb db;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;

    private final UserDtoMapper userDtoMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public Mono<AuthenticationResponseDto> createAuthor(String authToken, AuthorRequestDto requestDto) {

        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        var newUserId = UUID.randomUUID().getLeastSignificantBits();

        //потом добавить кто создал
        AuthorRecord newAuthor = new AuthorRecord();

        newAuthor.setId((UUID.randomUUID().getLeastSignificantBits()));
        newAuthor.setEmail(requestDto.getEmail());
        newAuthor.setFirstName(requestDto.getFirstname());
        newAuthor.setLastName(requestDto.getLastname());
        newAuthor.setBirthday(requestDto.getBirthday());
        newAuthor.setDescription(requestDto.getDescription());
        newAuthor.setCity(requestDto.getCity());
        newAuthor.setSocialNetwork(requestDto.getSocialNetwork());

        newAuthor.setUserId(newUserId);


        var newUserRecord = userDtoMapper.mapFromAuthor(requestDto);

        newUserRecord.setId(newUserId);

        var user = db.execute(ctx -> userRepository.save(ctx, newUserRecord));
        var newUser = userDtoMapper.map(user);

        db.execute(ctx-> authorRepository.createAuthor(ctx, newAuthor));

        var jwtAccessToken = jwtService.generateAccessToken(newUser);
        var jwtRefreshToken = jwtService.generateRefreshToken(newUser);

        return Mono.just(AuthenticationResponseDto.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build());
    }

    public AuthorRecord getByUserId(Long userId) {
        return db.execute(ctx -> authorRepository.getByUserId(ctx, userId));
    }

}
