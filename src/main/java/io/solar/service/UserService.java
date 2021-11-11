package io.solar.service;

import io.solar.entity.User;
import io.solar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById (Long id) {
        return userRepository.findById(id);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login);
    }

    public boolean matchPasswords(User userFromUI, User userFromDb) {
        String passFromUI = passwordEncoder.encode(userFromUI.getPassword());
        return passwordEncoder.matches(userFromUI.getPassword(), userFromDb.getPassword());
    }

    public Page<User> getAllUsers(PageRequest paging, String login, String title, boolean canEdit) {
        /*boolean canEdit = true AuthController.userCan(user, "edit-user", transaction);*/

        if(!canEdit) {
            login = null;
        } else if("".equals(login)) {
            login = null;
        }
        if("".equals(title)) {
            title = null;
        }

        Page<User> users;
        if (login != null && title != null) {
            users = userRepository.findByLoginStartingWithAndTitleStartingWith(paging, login, title);
        } else if (login != null) {
            users = userRepository.findByLoginStartingWith(paging, login);
        } else if (title != null) {
            users = userRepository.findByTitleStartingWith(paging, title);
        } else {
            users = userRepository.findAll(paging);
        }
        users.map(u -> mapUser(u, canEdit));
        return users;
    }

    public User getUserById(Long id, boolean canEdit) {
        /*boolean canEdit = true AuthController.userCan(userData, "edit-user", transaction);*/

        Optional<User> user = userRepository.findById(id);

        return mapUser(
                user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id")),
                canEdit);
    }

    public User updateUserTitle(Long id, String title, boolean canEdit) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id"));

        user.setTitle(title);
        user = userRepository.save(user);
        return mapUser(user, canEdit);
    }

    private User mapUser(User user, boolean canEdit) {
        if (user.getTitle() == null || user.getTitle().equals("")) {
            String log = user.getLogin();
            int index = log.indexOf("@");
            user.setTitle(index > -1 ? log.substring(0, user.getLogin().indexOf("@")) : log);
        }
        user.setPassword("");
        if (!canEdit) {
            user.setLogin("");
        }
        return user;
    }
}
