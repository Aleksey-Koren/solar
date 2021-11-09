package io.solar.repository;

import io.solar.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByLogin(String login);

    Page<User> findByLoginStartingWith(Pageable paging, String login);
    Page<User> findByTitleStartingWith(Pageable paging, String title);
    Page<User> findByLoginStartingWithAndTitleStartingWith(Pageable paging, String login, String title);
}
