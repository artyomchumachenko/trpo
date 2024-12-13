package ru.mai.trpo.configuration.jwt.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mai.trpo.model.User;
import ru.mai.trpo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Сервис для загрузки деталей пользователя по его имени пользователя.
 * <p>
 * Данный класс реализует интерфейс {@link org.springframework.security.core.userdetails.UserDetailsService}
 * и обеспечивает интеграцию с репозиторием пользователей. При загрузке пользователя по имени (логину),
 * если пользователь найден – возвращает объект {@link UserDetails}, в противном случае выбрасывает
 * {@link org.springframework.security.core.userdetails.UsernameNotFoundException}.
 * <p>
 * Обратите внимание, что роли у пользователя не устанавливаются (передается пустой список).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает данные пользователя из базы данных по указанному имени пользователя.
     *
     * @param username имя пользователя (логин)
     * @return объект {@link UserDetails}, содержащий имя и пароль пользователя
     * @throws UsernameNotFoundException если пользователь с таким именем не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с именем: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of()
        );
    }
}
