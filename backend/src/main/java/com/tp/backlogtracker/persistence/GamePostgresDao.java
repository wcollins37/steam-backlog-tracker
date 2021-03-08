package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile({"production","daoTesting"})
public class GamePostgresDao implements GameDao {
    @Autowired
    JdbcTemplate template;
    Random rand = new Random();

    @Override
    public Game addGame(Game game) throws NullGameException, InvalidGameIDException, NoChangesMadeException {
        if (game == null) {
            throw new NullGameException("Game cannot be null");
        }
        if (game.getGameID() == null || game.getGameID() == "") {
            throw new InvalidGameIDException("Game ID cannot be empty or null");
        }

        // try adding to Games table
        try {
            template.update("insert into \"Games\"(\"gameID\",\"name\",\"image\") values (?, ?, ?);",
                    game.getGameID(),
                    game.getName(),
                    game.getImg());
        } catch (DataAccessException ex) {
            throw new NoChangesMadeException("No changes made");
        }
        return game;
    }

    @Override
    public void addGameToUser(Game game) throws NullGameException, InvalidUserIDException, NoChangesMadeException {
        if (game == null) {
            throw new NullGameException("Game cannot be null");
        }
        if (game.getUserID() == null || game.getUserID() == "") {
            throw new InvalidUserIDException("User ID cannot be empty or null");
        }

        int status;
        try {
            status = template.update("insert into \"UserGames\"(\"completed\", \"playTime\", \"userID\", \"gameID\") values ('false',?,?,?)",
                    game.getHoursPlayed(),
                    game.getUserID(),
                    game.getGameID());
        } catch (DataAccessException ex) {
            throw new NoChangesMadeException("No changes made");
        }
        if (status < 1) {
            throw new NoChangesMadeException("No changes made");
        }
    }

    @Override
    public List<Game> getGamesByUserID(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }

        List<Game> allUserGames = new ArrayList<>();

        try {
        allUserGames = template.query(
                "select ga.\"gameID\", ga.\"name\" as \"gameName\",ga.\"image\" as \"gameImage\", ug.\"userID\" as \"userID\", ug.\"playTime\" as \"hoursPlayed\", ug.\"completed\"\n" +
                        "from \"Games\" as ga\n" +
                        "inner join \"UserGames\" as ug on ug.\"gameID\" = ga.\"gameID\"\n" +
                        "inner join \"Users\" as u on ug.\"userID\" = u.\"userID\"\n" +
                        "where ug.\"userID\" = ?;",
                new GameMapper(),
                userID);
        } catch (EmptyResultDataAccessException ex) {
            return new ArrayList<>();
        }

        for (Game game : allUserGames) {
            assignGameGenres(game);
        }
        return allUserGames;
    }

    @Override
    public void assignGameGenres(Game game) {
        List<String> genres = new ArrayList<>();
        try {
            genres = template.query(
                    "select ge.\"name\"\n" +
                            "from \"Genres\" as ge\n" +
                            "inner join \"GameGenres\" as gg on gg.\"genreID\" = ge.\"genreID\"\n" +
                            "where gg.\"gameID\" = ?;",
                    new GenreMapper(),
                    game.getGameID());
        } catch (DataAccessException ex) {

        }
        game.setGenres(genres);
    }

/*    @Override
    public List<Game> getUserGamesInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (genre == null) {
            throw new NoGamesFoundException("Genre cannot be null");
        }
        List<Game> userGames = getGamesByUserID(userID);
        if (userGames.size() == 0) {
            throw new NoGamesFoundException("No " + genre + " games found in user's library");
        }
        List<Game> genreGames = new ArrayList<>();
        for (Game toCheck : userGames) {
            for (String genreToCheck : toCheck.getGenres()) {
                if (genreToCheck.equalsIgnoreCase(genre)) {
                    genreGames.add(toCheck);
                    break;
                }
            }
        }
        if (genreGames.size() == 0) {
            throw new NoGamesFoundException("No " + genre + " games found in user's library");
        }
        return genreGames;
    } */

    @Override
    public List<Game> getUserGamesUnderHoursPlayed(String userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (hoursPlayed == null) {
            throw new NoGamesFoundException("Hours played cannot be null");
        }
        List<Game> games = new ArrayList<>();

        try {
            games = template.query(
                    "select ga.\"gameID\", ga.\"name\" as \"gameName\", ga.\"image\" as \"gameImage\", ge.\"name\" as \"genreName\", u.\"userID\" as \"userID\", ug.\"playTime\" as \"hoursPlayed\", ug.\"completed\"\n" +
                            "from \"Games\" as ga\n" +
                            "inner join \"GameGenres\" as gg on ga.\"gameID\" = gg.\"gameID\"\n" +
                            "inner join \"Genres\" as ge on gg.\"genreID\" = ge.\"genreID\"\n" +
                            "inner join \"UserGames\" as ug on ug.\"gameID\" = ga.\"gameID\"\n" +
                            "inner join \"Users\" as u on ug.\"userID\" = u.\"userID\"\n" +
                            "where ug.\"userID\" = ? and ug.\"playTime\" < ?;",
                    new GameMapper(),
                    userID,
                    hoursPlayed);
        } catch (EmptyResultDataAccessException ex) {
            throw new NoGamesFoundException("No games in user's library under " + hoursPlayed + " hours played");
        }
        if (games.size() == 0) {
            throw new NoGamesFoundException("No games in user's library under " + hoursPlayed + " hours played");
        }
        for (Game game : games) {
            assignGameGenres(game);
        }
        return games;
    }

    @Override
    public List<Game> getUncompletedGames(String userID) throws InvalidUserIDException, NoGamesFoundException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        List<Game> games = new ArrayList<>();

        try {
            games = template.query(
                    "select ga.\"gameID\", ga.\"name\" as \"gameName\", ga.\"image\" as \"gameImage\", ug.\"userID\" as \"userID\", ug.\"playTime\" as \"hoursPlayed\", ug.\"completed\"\n" +
                            "from \"Games\" as ga\n" +
                            "inner join \"UserGames\" as ug on ug.\"gameID\" = ga.\"gameID\"\n" +
                            "where ug.\"userID\" = ? and ug.\"completed\" = false;",
                    new GameMapper(),
                    userID);
        } catch (EmptyResultDataAccessException ex) {
            throw new NoGamesFoundException("No games found");
        }

        return games;
    }

    @Override
    public int updateHoursPlayed(String userID, String gameID, double newHours) throws InvalidUserIDException, InvalidGameIDException {
        if (userID == null || userID == "") {
            throw new InvalidUserIDException("User ID cannot be null or empty");
        }
        if (gameID == null || gameID == "") {
            throw new InvalidGameIDException("Game ID cannot be null or empty");
        }
        int status = -1;
        try {
            status = template.update("update \"UserGames\" set \"playTime\" = ?\n" +
                    "where \"userID\" = ? and \"gameID\" = ?;",
                    newHours,
                    userID,
                    gameID);
        } catch (DataAccessException ex) {
            return 0;
        }
        return status;
    }

    /*@Override
    public List<Game> getLeastPlayedGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (genre == null) {
            throw new NoGamesFoundException("Genre cannot be null");
        }

        List<Game> genreGames = getUserGamesInGenre(userID, genre);
        Double fewestHoursPlayed = Double.MAX_VALUE;
        for (Game game : genreGames) {
            if (game.getHoursPlayed() <= fewestHoursPlayed) {
                fewestHoursPlayed = game.getHoursPlayed();
            }
        }

        List<Game> toReturn = new ArrayList<>();
        for (Game toCheck : genreGames) {
            if (toCheck.getHoursPlayed() == fewestHoursPlayed) {
                assignGameGenres(toCheck);
                toReturn.add(toCheck);
            }
        }
        return toReturn;
    }*/

    @Override
    public Game changeCompletedStatus(String userID, String gameID) throws NoGamesFoundException {
        int switchResult = template.update("update \"UserGames\" set \"completed\" = not \"completed\" where \"userID\" = ? and \"gameID\" = ?;",
                userID,
                gameID);

        Game swappedGame = null;
        if (switchResult < 1) {
            throw new NoGamesFoundException("No changes made");
        } else {
            swappedGame = template.queryForObject(
                    "select ga.\"gameID\", ga.\"name\" as \"gameName\", ga.\"image\" as \"gameImage\", u.\"userID\" as \"userID\", ug.\"playTime\" as \"hoursPlayed\", ug.\"completed\"\n" +
                            "from \"Games\" as ga\n" +
                            "inner join \"UserGames\" as ug on ug.\"gameID\" = ga.\"gameID\"\n" +
                            "inner join \"Users\" as u on ug.\"userID\" = u.\"userID\"\n" +
                            "where ug.\"userID\" = ? and ug.\"gameID\" = ?;",
                    new GameMapper(),
                    userID,
                    gameID);
            assignGameGenres(swappedGame);
            return swappedGame;
        }
    }

    @Override
    public double getUserAveragePlayTime(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }

        double avgPlayTime = 0;

        try {
            avgPlayTime = template.queryForObject(
                    "select avg(\"playTime\") as \"avgPlayTime\"\n" +
                            "from \"UserGames\"\n" +
                            "where \"userID\" = ?\n" +
                            "group by \"userID\";",
                    new DoubleMapper("avgPlayTime"),
                    userID);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }

        return avgPlayTime;
    }

    @Override
    public int getNumOfUncompletedGames(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        return template.queryForObject(
                "select count(*) as \"count\"\n" +
                        "from \"UserGames\"\n" +
                        "where \"userID\" = ? and \"completed\" = 'false';",
                new IntMapper("count"),
                userID);
    }
}
