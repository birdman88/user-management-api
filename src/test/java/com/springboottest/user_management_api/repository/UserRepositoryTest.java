package com.springboottest.user_management_api.repository;

import com.springboottest.user_management_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User activeUser;
    private User deletedUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .ssn("0000000000001234")
                .firstName("John")
                .familyName("Doe")
                .isActive(true)
                .build();

        deletedUser = User.builder()
                .ssn("0000000000005678")
                .firstName("Jane")
                .familyName("Smith")
                .isActive(false)
                .deletedTime(Instant.now())
                .build();

        userRepository.save(activeUser);
        userRepository.save(deletedUser);
    }

    @Test
    void findAllActiveUsers_shouldReturnOnlyActiveNonDeletedUsers() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> result = userRepository.findAllActiveUsers(pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .first()
                .extracting(User::getSsn)
                .isEqualTo("0000000000001234");
    }

    @Test
    void findActiveUserById_shouldReturnActiveUser_whenExists() {
        Optional<User> result = userRepository.findActiveUserById(activeUser.getId());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getSsn)
                .isEqualTo("0000000000001234");
    }

    @Test
    void findActiveUserById_shouldReturnEmpty_whenUserIsDeleted() {
        Optional<User> result = userRepository.findActiveUserById(deletedUser.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findAllUserById_shouldReturnUser_evenIfDeleted() {
        Optional<User> result = userRepository.findAllUserById(deletedUser.getId());
        assertThat(result).isPresent();
    }

    @Test
    void existsBySsn_shouldReturnTrue_whenSsnExists() {
        boolean exists = userRepository.existsBySsn("0000000000001234");
        assertThat(exists).isTrue();
    }

    @Test
    void existsBySsn_shouldReturnFalse_whenSsnDoesNotExist() {
        boolean exists = userRepository.existsBySsn("9999999999999999");
        assertThat(exists).isFalse();
    }
}
