package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("daoTesting")
class GamePostgresDaoTest {

    @Autowired
    GameDao toTest;

    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void setup() {
        template.update("truncate \"UserFriends\",\"UserGames\",\"GameGenres\",\"Games\",\"Genres\",\"Users\" restart identity;");
        template.update("insert into \"Users\" (\"userID\",\"name\") values('1','testUser'),('2','noGames');");
        template.update("insert into \"Games\" (\"gameID\",\"name\") values('1','testGame'),('2','testGame2'),('3','testGame3');\n" +
                "insert into \"Genres\" (\"genreID\",\"name\") values('1','testGenre'),('2','testGenre2');\n" +
                "insert into \"GameGenres\" (\"gameID\",\"genreID\") values('1','1'),('2','2'),('3','1');\n" +
                "insert into \"UserGames\" (\"userID\",\"gameID\",\"completed\",\"playTime\") values('1','1','true','10'),('1','2','false','20'),('1','3','false','15');");
    }

    @Test
    public void testAddGameGoldenPath() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");

        Game addedGame = new Game();
        try {
            addedGame = toTest.addGame(newGame);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        assertEquals("99", addedGame.getGameID());
        assertEquals("testAdd", addedGame.getName());
        assertEquals("testImage", addedGame.getImg());
    }

    @Test
    public void testAddGameNullGame() {
        assertThrows(NullGameException.class, () -> toTest.addGame(null));
    }

    @Test
    public void testAddGameDuplicateGameID() {
        Game duplicate = new Game();
        duplicate.setGameID("1");
        duplicate.setName("testAdd");
        duplicate.setImg("testImage");
        assertThrows(NoChangesMadeException.class, () -> toTest.addGame(duplicate));
    }

    @Test
    public void testAddGameToUserGoldenPath() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID("1");
        newGame.setHoursPlayed(20);

        try {
            toTest.addGame(newGame);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        try {
            toTest.addGameToUser(newGame);
        } catch (NullGameException | NoChangesMadeException | InvalidUserIDException ex) {
            fail();
        }

        List<Game> games = new ArrayList<>();
        try {
            games = toTest.getGamesByUserID("1");
        } catch (InvalidUserIDException ex) {
            fail();
        }

        Game addedGame = games.get(games.size() - 1);
        assertEquals(4, games.size());
        assertEquals("99", addedGame.getGameID());
        assertEquals("testAdd", addedGame.getName());
        assertEquals("testImage", addedGame.getImg());
        assertEquals("1", addedGame.getUserID());
        assertEquals(20, addedGame.getHoursPlayed());
    }

    @Test
    public void testAddGameToUserNullGame() {
        assertThrows(NullGameException.class, () -> toTest.addGameToUser(null));
    }

    @Test
    public void testAddGameToUserNullUserID() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID(null);
        newGame.setHoursPlayed(20);

        assertThrows(InvalidUserIDException.class, () -> toTest.addGameToUser(newGame));
    }

    @Test
    public void testAddGameToUserEmptyUserID() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID("");
        newGame.setHoursPlayed(20);

        assertThrows(InvalidUserIDException.class, () -> toTest.addGameToUser(newGame));
    }

    @Test
    public void testAddGameToUserGameNotInDatabase() {
        Game newGame = new Game();
        newGame.setGameID("100");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID("1");
        newGame.setHoursPlayed(20);

        assertThrows(NoChangesMadeException.class, () -> toTest.addGameToUser(newGame));
    }

    @Test
    public void testAddGameToUserUserNotInDatabase() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID("99");
        newGame.setHoursPlayed(20);

        try {
            toTest.addGame(newGame);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        assertThrows(NoChangesMadeException.class, () -> toTest.addGameToUser(newGame));
    }

    @Test
    public void testAddGameToUserAddDuplicateGameToUser() {
        Game newGame = new Game();
        newGame.setGameID("99");
        newGame.setName("testAdd");
        newGame.setImg("testImage");
        newGame.setUserID("1");
        newGame.setHoursPlayed(20);

        try {
            toTest.addGame(newGame);
        } catch (NullGameException | InvalidGameIDException | NoChangesMadeException ex) {
            fail();
        }

        try {
            toTest.addGameToUser(newGame);
        } catch (NullGameException | NoChangesMadeException | InvalidUserIDException ex) {
            fail();
        }
        assertThrows(NoChangesMadeException.class, () -> toTest.addGameToUser(newGame));
    }

    @Test
    public void testGetGamesByUserIDGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getGamesByUserID("1");
        } catch (InvalidUserIDException ex) {
            fail();
        }
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(true, game.isCompleted());
    }

    @Test
    public void testGetGamesByUserIDNullID() {
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

    /*@Test
    public void testGetUserGamesOfGenreGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getUserGamesInGenre("1","testgenre");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(2, games.size());
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(true, game.isCompleted());
    }

    @Test
    public void testGetUserGamesOfGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesInGenre(null, "testGenre"));
    }

    @Test
    public void testGetUserGamesOfGenreNullGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesInGenre("1", null));
    }

    @Test
    public void testGetUserGamesOfGenreNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesInGenre("1", "no"));
    }*/

    @Test
    public void testGetUserGamesUnderHoursPlayedGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getUserGamesUnderHoursPlayed("1",15.0);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(true, game.isCompleted());
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesUnderHoursPlayed(null, 15.0));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNullHoursPlayed() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed("1", null));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed("1", 1.0));
    }

/*    @Test
    public void testGetLeastPlayedGameInGenreGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getLeastPlayedGameInGenre("1", "testGenre");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals("3", game.getGameID());
        assertEquals("testGame3", game.getName());
        assertEquals(15, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(false, game.isCompleted());
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getLeastPlayedGameInGenre(null, "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("2", "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("1", null));
    }

    @Test
    public void testGetLeastPlayedGameInGenreBadGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre("1", "no"));
    }*/

    @Test
    public void testChangeCompletedStatusGoldenPath() {
        Game game = null;
        try {
            game = toTest.changeCompletedStatus("1","1");
        } catch (NoGamesFoundException ex) {
            fail();
        }
        assertEquals("1", game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("1", game.getUserID());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(false, game.isCompleted());
    }

    @Test
    public void testChangeCompletedStatusNullUserID() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus(null, "1"));
    }

    @Test
    public void testChangeCompletedStatusNullGameID() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus("1", null));
    }

    @Test
    public void testChangeCompletedStatusNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus("-1", "1"));
    }

    @Test
    public void testChangeCompletedStatusNoGameFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus("1", "-1"));
    }

    @Test
    public void getUserAvgPlayTimeGoldenPath() {
        double avg = 0;
        try {
            avg = toTest.getUserAveragePlayTime("1");
        } catch (InvalidUserIDException ex) {
            fail();
        }
        assertEquals(15, avg);

        try {
            assertEquals(0, toTest.getUserAveragePlayTime("2"));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void getUserAvgPlayTimeNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserAveragePlayTime(null));
    }

    @Test
    public void getUserAvgPlayTimeNoUserFound() {
        try {
            assertEquals(0, toTest.getUserAveragePlayTime("-1"));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesGoldenPath() {
        try {
            assertEquals(2, toTest.getNumOfUncompletedGames("1"));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesNoGamesFound() {
        try {
            assertEquals(0, toTest.getNumOfUncompletedGames("2"));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getNumOfUncompletedGames(null));
    }
}