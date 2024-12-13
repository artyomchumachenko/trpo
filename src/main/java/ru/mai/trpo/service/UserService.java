package ru.mai.trpo.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.mai.trpo.model.User;
import ru.mai.trpo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Сервис для работы с пользователями.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *     <li>Сохранения пользователя</li>
 *     <li>Проверки существования пользователя по имени</li>
 *     <li>Извлечения пользователя по имени</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Сохраняет данные о пользователе в базе.
     *
     * @param user объект пользователя для сохранения
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Проверяет, существует ли пользователь с таким именем.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Получает пользователя по имени.
     *
     * @param username имя пользователя
     * @return объект {@link User}, если пользователь найден
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем " + username + " не найден"));
    }
}
