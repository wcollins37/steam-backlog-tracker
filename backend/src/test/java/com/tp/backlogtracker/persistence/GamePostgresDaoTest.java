package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

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
                "insert into \"UserGames\" (\"userID\",\"gameID\",\"completed\",\"playTime\") values('1','1','true','10 hours'),('1','2','false','20 hours'),('1','3','false','15 hours');");
    }

    @Test
    public void testGetGamesByUserIDGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getGamesByUserID(1);
        } catch (InvalidUserIDException ex) {
            fail();
        }
        Game game = games.get(0);
        assertEquals(1, game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("testUser", game.getUserName());
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
            assertEquals(0, toTest.getGamesByUserID(-1).size());
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetUserGamesOfGenreGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getUserGamesInGenre(1,"testgenre");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(2, games.size());
        Game game = games.get(0);
        assertEquals(1, game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("testUser", game.getUserName());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(true, game.isCompleted());
    }

    @Test
    public void testGetUserGamesOfGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesInGenre(null, "testGenre"));
    }

    @Test
    public void testGetUserGamesOfGenreNullGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesInGenre(1, null));
    }

    @Test
    public void testGetUserGamesOfGenreNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesInGenre(1, "no"));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getUserGamesUnderHoursPlayed(1,15.0);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals(1, game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("testUser", game.getUserName());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(true, game.isCompleted());
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getUserGamesUnderHoursPlayed(null, 15.0));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNullHoursPlayed() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed(1, null));
    }

    @Test
    public void testGetUserGamesUnderHoursPlayedNoGamesFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getUserGamesUnderHoursPlayed(1, 1.0));
    }

    @Test
    public void testGetLeastPlayedGameInGenreGoldenPath() {
        List<Game> games = null;
        try {
            games = toTest.getLeastPlayedGameInGenre(1, "testGenre");
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            fail();
        }
        assertEquals(1, games.size());
        Game game = games.get(0);
        assertEquals(3, game.getGameID());
        assertEquals("testGame3", game.getName());
        assertEquals(15, game.getHoursPlayed());
        assertEquals("testUser", game.getUserName());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(false, game.isCompleted());
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getLeastPlayedGameInGenre(null, "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre(2, "testGenre"));
    }

    @Test
    public void testGetLeastPlayedGameInGenreNullGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre(1, null));
    }

    @Test
    public void testGetLeastPlayedGameInGenreBadGenre() {
        assertThrows(NoGamesFoundException.class, () -> toTest.getLeastPlayedGameInGenre(1, "no"));
    }

    @Test
    public void testChangeCompletedStatusGoldenPath() {
        Game game = null;
        try {
            game = toTest.changeCompletedStatus(1,1);
        } catch (NoGamesFoundException ex) {
            fail();
        }
        assertEquals(1, game.getGameID());
        assertEquals("testGame", game.getName());
        assertEquals(10, game.getHoursPlayed());
        assertEquals("testUser", game.getUserName());
        assertEquals("testGenre", game.getGenres().get(0));
        assertEquals(false, game.isCompleted());
    }

    @Test
    public void testChangeCompletedStatusNullUserID() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus(null, 1));
    }

    @Test
    public void testChangeCompletedStatusNullGameID() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus(1, null));
    }

    @Test
    public void testChangeCompletedStatusNoUserFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus(-1, 1));
    }

    @Test
    public void testChangeCompletedStatusNoGameFound() {
        assertThrows(NoGamesFoundException.class, () -> toTest.changeCompletedStatus(1, -1));
    }

    @Test
    public void getUserAvgPlayTimeGoldenPath() {
        double avg = 0;
        try {
            avg = toTest.getUserAveragePlayTime(1);
        } catch (InvalidUserIDException ex) {
            fail();
        }
        assertEquals(15, avg);

        try {
            assertEquals(0, toTest.getUserAveragePlayTime(2));
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
            assertEquals(0, toTest.getUserAveragePlayTime(-1));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesGoldenPath() {
        try {
            assertEquals(2, toTest.getNumOfUncompletedGames(1));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesNoGamesFound() {
        try {
            assertEquals(0, toTest.getNumOfUncompletedGames(2));
        } catch (InvalidUserIDException ex) {
            fail();
        }
    }

    @Test
    public void testGetNumOfUncompletedGamesNullUserID() {
        assertThrows(InvalidUserIDException.class, () -> toTest.getNumOfUncompletedGames(null));
    }
}