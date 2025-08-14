package org.example.notifycentral.service;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.init();
    }

    @Test
    void findAll_shouldReturnThreeUsers() {
        // Act
        List<UserMock> allUsers = userService.findAll();

        // Assert
        assertEquals(3, allUsers.size(), "Should contain 3 users");
    }

    @Test
    void findByCategory_shouldReturnOnlyUsersSubscribedToSports() {
        // Act
        List<UserMock> sportsUsers = userService.findByCategory(MessageCategory.SPORTS);

        // Assert
        assertFalse(sportsUsers.isEmpty(), "There should be users subscribed to SPORTS");
        assertTrue(sportsUsers.stream().allMatch(u -> u.getSubscribed().contains(MessageCategory.SPORTS)));
        assertEquals(2, sportsUsers.size());
    }

    @Test
    void findByCategory_shouldReturnOnlyUsersSubscribedToFinance() {
        // Act
        List<UserMock> financeUsers = userService.findByCategory(MessageCategory.FINANCE);

        // Assert
        assertFalse(financeUsers.isEmpty(), "There should be users subscribed to FINANCE");
        assertTrue(financeUsers.stream().allMatch(u -> u.getSubscribed().contains(MessageCategory.FINANCE)));
        assertEquals(2, financeUsers.size());
    }

}