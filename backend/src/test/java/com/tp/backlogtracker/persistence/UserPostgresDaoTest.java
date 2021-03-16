package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("daoTesting")
class UserPostgresDaoTest {

    @Autowired
    UserDao toTest;

    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void setup() {
        template.update("truncate \"UserFriends\",\"UserGames\",\"GameGenres\",\"Games\",\"Genres\",\"Users\" restart identity;");
        template.update("insert into \"Users\" (\"userID\",\"name\",\"avatarSrc\") values('1','testUser','testImage'),('2','noGames','testImage2');");
        template.update("insert into \"Games\" (\"gameID\",\"name\") values('1','testGame'),('2','testGame2'),('3','testGame3');\n" +
                "insert into \"Genres\" (\"genreID\",\"name\") values('1','testGenre'),('2','testGenre2');\n" +
                "insert into \"GameGenres\" (\"gameID\",\"genreID\") values('1','1'),('2','2'),('3','1');\n" +
                "insert into \"UserGames\" (\"userID\",\"gameID\",\"completed\",\"playTime\") values('1','1','true','10'),('1','2','false','20'),('1','3','false','15');");
    }

    @Test
    public void testAddUserGoldenPath() {
        User user = null;
        try {
            user = toTest.getUserByID(toTest.addUser("3", "testAdd", "testImage"));
        } catch (InvalidUserIDException | InvalidUserNameException | NoChangesMadeException ex) {
            fail();
        }
        assertEquals("3", user.getUserID());
        assertEquals("testAdd", user.getName());
    }

    @Test
    public void testAddUserNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.addUser(null, "no", "testImage"));
    }

    @Test
    public void testAddUserNullUserName() {
        assertThrows(InvalidUserNameException.class, () -> toTest.addUser("99", null, "testImage"));
    }

    @Test
    public void testAddUserEmptyUserName() {
        assertThrows(InvalidUserNameException.class, () -> toTest.addUser("99", "", "testImage"));
    }

    @Test
    public void testAddUserUserIDAlreadyExists() {
        assertThrows(NoChangesMadeException.class, () -> toTest.addUser("1", "no", "testImage"));
    }

    @Test
    public void testGetUserByIDGoldenPath() {
        User user = null;
        try {
            user = toTest.getUserByID("1");
        } catch (InvalidUserIDException ex) {
            fail();
        }
        assertEquals("1", user.getUserID());
        assertEquals("testUser", user.getName());
    }

    @Test
    public void testGetUserByIDNullID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserByID(null));
    }

    @Test
    public void testGetUserByIDNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserByID("-1"));
    }

    @Test
    public void testAddFriendGoldenPath() {
        User friend = null;
        try {
            friend = toTest.getUserByID(toTest.addFriend("1", "2"));
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            fail();
        }
        assertEquals("2", friend.getUserID());
        assertEquals("noGames", friend.getName());
    }

    @Test
    public void testAddFriendsNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.addFriend(null, "1"));
    }

    @Test
    public void testAddFriendsNullFriendID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.addFriend("1", null));
    }

    @Test
    public void testAddFriendsIdenticalIDs() {
        assertThrows(InvalidUserIDException.class, () -> toTest.addFriend("1", "1"));
    }

    @Test
    public void testAddFriendsAddingAgain() {
        User friend = null;
        try {
            friend = toTest.getUserByID(toTest.addFriend("1", "2"));
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            fail();
        }
        try {
            friend = toTest.getUserByID(toTest.addFriend("1","2"));
            fail();
        } catch (InvalidUserIDException ex) {
            fail();
        } catch (NoChangesMadeException ex) {

        }
    }

/*    @Test
    public void testGetUserFriendsGoldenPath() {
        List<User> friends = new ArrayList<>();
        try {
            toTest.addFriend("1","2");
            friends = toTest.getUserFriends("1");
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            fail();
        }
        assertEquals(1, friends.size());
        User newFriend = friends.get(0);
        assertEquals("2", newFriend.getUserID());
        assertEquals("noGames",newFriend.getName());
    }

    @Test
    public void testGetUserFriendsNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserFriends(null));
    }

    @Test
    public void testGetUserFriendsNoFriendsFound() {
        try {
            List<User> friends = toTest.getUserFriends("3");
            assertEquals(0,friends.size());
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }
    */

    @Test
    public void testCheckIfUserOwnsGameGoldenPath() {
        boolean gameOwned = false;
        try {
            gameOwned = toTest.checkIfUserOwnsGame("1","1");
        } catch (InvalidUserIDException | InvalidGameIDException ex) {
            fail();
        }
        assertTrue(gameOwned);

        try {
            gameOwned = toTest.checkIfUserOwnsGame("1","99");
        } catch (InvalidUserIDException | InvalidGameIDException ex) {
            fail();
        }
        assertFalse(gameOwned);

        try {
            gameOwned = toTest.checkIfUserOwnsGame("99","1");
        } catch (InvalidUserIDException | InvalidGameIDException ex) {
            fail();
        }
        assertFalse(gameOwned);
    }

    @Test
    public void testCheckIfUserOwnsGameNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.checkIfUserOwnsGame(null, "1"));
    }

    @Test
    public void testCheckIfUserOwnsGameNullGameID() {
        assertThrows(InvalidGameIDException.class, () -> toTest.checkIfUserOwnsGame("1", null));
    }

    @Test
    public void testUpdateUserInfoGoldenPath() {
        User updatedUser = null;
        try {
            toTest.updateUserInfo("1", "updatedUser", "updatedImage");
            updatedUser = toTest.getUserByID("1");
        } catch (InvalidUserIDException | InvalidUserNameException | InvalidAvatarException ex) {
            fail();
        }

        assertEquals("1", updatedUser.getUserID());
        assertEquals("updatedUser", updatedUser.getName());
        assertEquals("updatedImage", updatedUser.getAvatarSrc());
    }

    @Test
    public void testUpdateUserInfoNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.updateUserInfo(null, "no", "no"));
    }

    @Test
    public void testUpdateUserInfoNullUsername() {
        assertThrows(InvalidUserNameException.class, () -> toTest.updateUserInfo("1", null, "no"));
    }

    @Test
    public void testUpdateUserInfoNullAvatar() {
        assertThrows(InvalidAvatarException.class, () -> toTest.updateUserInfo("1", "no", null));
    }
}