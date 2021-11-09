package io.solar.service;

import io.solar.controller.AuthController;
import io.solar.entity.User;
import io.solar.repository.UserRepository;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

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

    public Page<User> getAllUsers(User user, Transaction transaction, String login, String title, Pageable pageable) {
        boolean canEdit = AuthController.userCan(user, "edit-user", transaction);
        if(!canEdit) {
            login = null;
        } else if("".equals(login)) {
            login = null;
        }
        if("".equals(title)) {
            title = null;
        }

        PageRequest paging = PageRequest.of(pageable.getPage(), pageable.getPageSize());
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

        users.map(u -> mapUser(user, canEdit));

        return users;
    }

    public User getUserById(Long id, Transaction transaction, User userData) {
        boolean canEdit = AuthController.userCan(userData, "edit-user", transaction);
        User user;

        try {
            user = userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return null;
        }

        return mapUser(user, canEdit);
    }

    public User updateUserTitle(Long id, String title, boolean canEdit) {
        User user;
        try {
            user = userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("no user with such id");
        }

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
