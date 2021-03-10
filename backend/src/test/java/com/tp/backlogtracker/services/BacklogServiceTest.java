package com.tp.backlogtracker.services;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;
import com.tp.backlogtracker.models.User;
import com.tp.backlogtracker.persistence.GameInMemDao;
import com.tp.backlogtracker.persistence.UserInMemDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("serviceTesting")
class BacklogServiceTest {

    @Autowired
    BacklogService toTest;

    @Autowired
    JdbcTemplate template;

    public void addMoreGames() {
        Game game2 = new Game();
        game2.setGameID("2");
        game2.setName("New Vegas");
        game2.setHoursPlayed(40);
        game2.setUserID("1");
        game2.setGenres(new ArrayList<>());
        game2.getGenres().add("RPG");
        game2.setCompleted(true);
        try {
            toTest.gameDao.addGame(game2);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        Game game3 = new Game();
        game3.setGameID("3");
        game3.setName("Half-Life 3");
        game3.setHoursPlayed(99);
        game3.setUserID("1");
        game3.setGenres(new ArrayList<>());
        game3.getGenres().add("Shooter");
        game3.setCompleted(false);
        try {
            toTest.gameDao.addGame(game3);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }
    }

    @BeforeEach
    public void setup() {
        toTest.rand = new Random(1);
        toTest.gameDao = new GameInMemDao();
        toTest.userDao = new UserInMemDao();

        Game game = new Game();
        game.setGameID("1");
        game.setName("testGame");
        game.setHoursPlayed(10);
        game.setUserID("1");
        game.setGenres(new ArrayList<>());
        game.getGenres().add("Testgenre");
        game.setCompleted(true);
        try {
            toTest.gameDao.addGame(game);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }
        addMoreGames();

        try {
            toTest.userDao.addUser("1", "testUser");
            toTest.userDao.addUser("2", "noGames");
        } catch (InvalidUserIDException | InvalidUserNameException | NoChangesMadeException ex) {
            fail();
        }
    }

    @Test
    public void testAddUserGoldenPath() {
        User user = null;
        try {
            user = toTest.addUser("3", "testAdd");
        } catch (InvalidUserIDException | InvalidUserNameException | NoChangesMadeException ex) {
            fail();
        }
        assertEquals("3", user.getUserID());
        assertEquals("testAdd", user.getName());
    }

    @Test
    public void testAddUserNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.addUser(null, "no"));
    }

    @Test
    public void testAddUserNullUserName() {
        assertThrows(InvalidUserNameException.class, () -> toTest.addUser("99", null));
    }

    @Test
    public void testAddUserEmptyUserName() {
        assertThrows(InvalidUserNameException.class, () -> toTest.addUser("99", ""));
    }

    @Test
    public void testAddUserUserIDAlreadyExists() {
        assertThrows(NoChangesMadeException.class, () -> toTest.addUser("1", "no"));
    }

    @Test
    public void testAddFriendGoldenPath() {
        User friend = null;
        try {
            friend = toTest.addFriend("1", "2");
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
            friend = toTest.addFriend("1", "2");
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            fail();
        }
        try {
            friend = toTest.addFriend("1","2");
            fail();
        } catch (InvalidUserIDException ex) {
            fail();
        } catch (NoChangesMadeException ex) {

        }
    }

    @Test
    public void testGetGamesByUserIDGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getGamesByUserID("1");
        } catch (InvalidUserIDException ex) {
            fail();
        }
        assertEquals(3, games.size());
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());
    }

    @Test
    public void testGetGamesByUserIDNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getGamesByUserID(null));
    }

    @Test
    public void testGetGamesByUserIDNoGamesFound() {
        try {
            assertEquals(0, toTest.getGamesByUserID("-1").size());
        } catch (InvalidUserIDException ex) {
            fail();
        }
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
        assertNotNull(user.getLibrary());
        Game game = user.getLibrary().get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());


        assertEquals(Math.round(149/3.0 * 100) / 100.0, user.getAvgPlayTime());
        assertEquals(1, user.getNumUncompletedGames());
        assertEquals(Math.round(2.0/3 * 100) / 100.0,user.getPercentCompleted());
    }

    @Test
    public void testGetUserByIDNullID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserByID(null));
    }

    @Test
    public void testGetUserByIDNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserByID("-1"));
    }

    /*@Test
    public void testSortUserGamesByGenreGoldenPath() {
        List<Game> sortedGenreGames = null;
        try {
            sortedGenreGames = toTest.sortUserGamesByGenre(1).getLibrary();
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals("New Vegas",sortedGenreGames.get(0).getName());
        assertEquals("Half-Life 3",sortedGenreGames.get(1).getName());
        assertEquals("testGame",sortedGenreGames.get(2).getName());
    }

    @Test
    public void testSortUserGamesByGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.sortUserGamesByGenre(null));
    }

    @Test
    public void testSortUserGamesByGenreNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.sortUserGamesByGenre(-1));
    }

    @Test
    public void testSortUserGamesByGenreNoGamesFound() {
        try {
            assertEquals(0,toTest.sortUserGamesByGenre(2).getLibrary().size());
        } catch (InvalidUserIDException | NoGamesFoundException ex) {
            fail();
        }
    }*/

/*    @Test
    public void testGetUserGamesByGenreGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getUserGamesByGenre("1", "testGenre").getLibrary();
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());
    }

    @Test
    public void testGetUserGamesByGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesByGenre(null, "testGenre"));
    }

    @Test
    public void testGetUserGamesByGenreNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesByGenre("-1", "testGenre"));
    }

    @Test
    public void testGetUserGamesByGenreNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesByGenre("1", "no"));
    }

    @Test
    public void testGetUserGamesByGenreBadGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesByGenre("1", "noGenre"));
    }*/

    @Test
    public void testSortUserGamesByHoursPlayedGoldenPath() {
        List<Game> sortedPlayTimeGames = null;
        try {
            sortedPlayTimeGames = toTest.sortUserGamesByHoursPlayed("1").getLibrary();
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals("testGame",sortedPlayTimeGames.get(0).getName());
        assertEquals("New Vegas",sortedPlayTimeGames.get(1).getName());
        assertEquals("Half-Life 3",sortedPlayTimeGames.get(2).getName());
    }

    @Test
    public void testSortUserGamesByHoursPlayedNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.sortUserGamesByHoursPlayed(null));
    }

    @Test
    public void testSortUserGamesByHoursPlayedNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.sortUserGamesByHoursPlayed("-1"));
    }

    @Test
    public void testSortUserGamesByHoursPlayedNoGamesFound() {
        try {
            assertEquals(0, toTest.sortUserGamesByHoursPlayed("2").getLibrary().size());
        } catch (InvalidUserIDException | NoGamesFoundException ex) {
            fail();
        }
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedGoldenPath() {
        addMoreGames();
        List<Game> games = null;
        try {
            games = toTest.getUserGamesUnderHoursPlayed("1", 11.0).getLibrary();
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesUnderHoursPlayed(null, 11.0));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNoUserFound() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesUnderHoursPlayed("-1", 11.0));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed("2", 0.0));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNoGamesUnderHours() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed("1", 0.0));
    }

/*    @Test
    public void testGetLeastPlayedGameInGenreGoldenPath() {
        Game newGame = new Game();
        newGame.setGameID("4");
        newGame.setName("Ultrakill");
        newGame.setHoursPlayed(20);
        newGame.setUserID("1");
        newGame.setGenres(new ArrayList<>());
        newGame.getGenres().add("Shooter");
        newGame.setCompleted(false);
        try {
            toTest.gameDao.addGame(newGame);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        Game toCheck = null;
        try {
            toCheck = toTest.getLeastPlayedGameInGenre("1", "shooter");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }

        assertEquals(4, toCheck.getGameID());
        assertEquals("Ultrakill", toCheck.getName());
        assertEquals(20, toCheck.getHoursPlayed());
        assertEquals("1", toCheck.getUserID());
        assertEquals("Shooter", toCheck.getGenres().get(0));
        assertEquals(false, toCheck.isCompleted());
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getLeastPlayedGameInGenre(null, "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("1", null));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("-1", "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNoGamesFoundInGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("1", "no"));
    }*/

    @Test
    public void testChangeCompletedStatusGoldenPath() {
        Game game = null;
        try {
            game = toTest.changeCompletedStatus(toTest.getUserByID("1").getLibrary().get(0));
        } catch (NoGamesFoundException | InvalidUserIDException | NullGameException ex) {
            fail();
        }
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertFalse(game.isCompleted());

        try {
            game = toTest.changeCompletedStatus(toTest.getUserByID("1").getLibrary().get(0));
        } catch (NoGamesFoundException | InvalidUserIDException | NullGameException ex) {
            fail();
        }
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());
    }

    @Test
    public void testChangeCompletedStatusNullGame() {
        assertThrows(NullGameException.class, () -> toTest.changeCompletedStatus(null));
    }

/*    @Test
    public void testChangeCompletedStatusNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus("-1","1"));
    }

    @Test
    public void testChangeCompletedStatusNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus("1","-1"));
    }*/

    @Test
    public void testPickRandomGameGoldenPath() {
        Game game = null;
        try {
            game = toTest.pickRandomGame("1");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("Testgenre", game.getGenres().get(0));
        assertTrue(game.isCompleted());
    }

    @Test
    public void testPickRandomGameNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.pickRandomGame(null));
    }

    @Test
    public void testPickRandomGameNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.pickRandomGame("99"));
    }
}