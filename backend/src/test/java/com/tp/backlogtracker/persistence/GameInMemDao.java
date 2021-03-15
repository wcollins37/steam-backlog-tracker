package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("serviceTesting")
public class GameInMemDao implements GameDao {

    @Autowired
    JdbcTemplate template;
    Random rand = new Random();

    // game, userID
    Map<Game, String> allGames = new HashMap<>();

    public GameInMemDao() {
        allGames = new HashMap<>();
    }

    @Override
    public Game addGame(Game game) throws NullGameException, InvalidGameIDException, NoChangesMadeException {
        allGames.put(game, game.getUserID());
        return game;
    }

    @Override
    public void addGameToUser(Game game) throws NullGameException, InvalidUserIDException, NoChangesMadeException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Game> getGamesByUserID(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }

        List<Game> games = new ArrayList<>();
        for (Game toCheck : allGames.keySet()) {
            if (allGames.get(toCheck) == userID) {
                games.add(toCheck);
            }
        }

        if (games.size() == 0) {
            return new ArrayList<>();
        }

        Comparator<Game> gameComparator = Comparator.comparing(Game::getGameID);
        Collections.sort(games, gameComparator);

        return games;
    }

    @Override
    public void assignGameGenres(Game game) {

    }

/*    @Override
    public List<Game> getUserGamesInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        List<Game> games = getGamesByUserID(userID);
        List<Game> genreGames = new ArrayList<>();

        for (Game game : games) {
            for (String genreToCheck : game.getGenres()) {
                if (genreToCheck.equalsIgnoreCase(genre.toLowerCase())) {
                    genreGames.add(game);
                }
            }
        }

        if (genreGames.size() == 0) {
            throw new NoGamesFoundException("No " + genre + " games found in user's library");
        }

        return genreGames;
    }*/

    @Override
    public List<Game> getUserGamesUnderHoursPlayed(String userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException {
        List<Game> games = getGamesByUserID(userID);
        List<Game> playTimeGames = new ArrayList<>();

        for (Game game: games) {
            if (game.getHoursPlayed() <= hoursPlayed) {
                playTimeGames.add(game);
            }
        }

        return playTimeGames;
    }

    @Override
    public List<Game> getUncompletedGames(String userID) throws InvalidUserIDException, NoGamesFoundException {
        return null;
    }

    @Override
    public List<Game> getLeastPlayedUncompletedGames(String userID) throws InvalidUserIDException, NoGamesFoundException {
        return null;
    }

    @Override
    public int updateHoursPlayed(String userID, String gameID, double newHours) throws InvalidUserIDException, InvalidGameIDException {
        return 0;
    }

    /*    @Override
    public List<Game> getLeastPlayedGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (genre == null) {
            throw new NoGamesFoundException("Genre cannot be null");
        }

        List<Game> toReturn = new ArrayList<>();
        double genreMin = Double.MAX_VALUE;
        for (Game toCheck : allGames.keySet()) {
            for (String genreToCheck : toCheck.getGenres()) {
                if (allGames.get(toCheck) == userID && genreToCheck.equalsIgnoreCase(genre) && toCheck.getHoursPlayed() < genreMin) {
                    genreMin = toCheck.getHoursPlayed();
                }
            }
        }
        for (Game toCheck: allGames.keySet()) {
            for (String genreToCheck : toCheck.getGenres()) {
                if (genreToCheck.equalsIgnoreCase(genre) && toCheck.getHoursPlayed() == genreMin) {
                    toReturn.add(toCheck);
                }
            }
        }
        if (toReturn.size() == 0) {
            throw new NoGamesFoundException("No eligible uncompleted games found owned by user " + userID);
        } else {
            return toReturn;
        }
    }*/

    @Override
    public Game changeCompletedStatus(String userID, String gameID) throws NoGamesFoundException {
        if (gameID == null) {
            throw new NoGamesFoundException("No changes made");
        }
        Game game = null;
        for (Game toCheck : allGames.keySet()) {
            if (allGames.get(toCheck) == userID && toCheck.getGameID() == gameID) {
                game = toCheck;
                break;
            }
        }
        if (game == null) {
            throw new NoGamesFoundException("No changes made");
        }
        game.setCompleted(!game.isCompleted());
        return game;
    }

    @Override
    public double getUserAveragePlayTime(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        double sum = 0;
        int count = 0;
        for (Game game : allGames.keySet()) {
            if (allGames.get(game) == userID) {
                sum += game.getHoursPlayed();
                count++;
            }
        }
        if (sum == 0) {
            return 0;
        } else {
            return sum / count;
        }
    }

    @Override
    public int getNumOfUncompletedGames(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        int count = 0;
        for (Game game : allGames.keySet()) {
            if (allGames.get(game) == userID && !game.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void deleteGameFromLibrary(String gameID, String userID) throws InvalidGameIDException, InvalidUserIDException, NoChangesMadeException {
        
    }
}
