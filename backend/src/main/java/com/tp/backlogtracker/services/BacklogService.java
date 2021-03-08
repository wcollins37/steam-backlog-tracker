package com.tp.backlogtracker.services;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;
import com.tp.backlogtracker.models.User;
import com.tp.backlogtracker.persistence.GameDao;
import com.tp.backlogtracker.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BacklogService {

    @Autowired
    GameDao gameDao;

    @Autowired
    UserDao userDao;

    Random rand = new Random();

    public void addGameFromSteam(Game game) throws NullGameException, InvalidGameIDException, NoChangesMadeException, InvalidUserIDException {
        try {
            gameDao.addGame(game);
        } catch (NullGameException | InvalidGameIDException ex) {
            throw ex;
        } catch (NoChangesMadeException ex) {

        }
        gameDao.addGameToUser(game);
    }

    public void addManyGamesFromSteam(Game[] games) throws NullGameException, InvalidGameIDException, InvalidUserIDException {
        for(Game game : games) {
            try {
                addGameFromSteam(game);
            } catch (NoChangesMadeException ex) {
            } catch (NullGameException | InvalidGameIDException | InvalidUserIDException ex) {
                throw ex;
            }
        }
    }

    public User addUser(String userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException {
        User newUser = new User();
        newUser.setUserID(userDao.addUser(userID, name));
        newUser.setName(name);
        return newUser;
    }

    public User addFriend(String userID, String friendID) throws InvalidUserIDException, NoChangesMadeException{
        return getUserByID(userDao.addFriend(userID, friendID));
    }

    public boolean checkIfUserOwnsGame(String userID, String gameID) throws InvalidUserIDException, InvalidGameIDException {
        return userDao.checkIfUserOwnsGame(userID, gameID);
    }

    public List<Game> getGamesByUserID(String userID) throws InvalidUserIDException {
        return gameDao.getGamesByUserID(userID);
    }

    public User getUserByID(String userID) throws InvalidUserIDException {
        User partialUser = userDao.getUserByID(userID);
        List<Game> userGames = gameDao.getGamesByUserID(userID);
        partialUser.setLibrary(userGames);
        partialUser.setAvgPlayTime((double) Math.round(gameDao.getUserAveragePlayTime(userID) * 100) / 100);
        partialUser.setNumUncompletedGames(gameDao.getNumOfUncompletedGames(userID));
        if (partialUser.getLibrary().size() != 0) {
            double percent = (partialUser.getLibrary().size() - partialUser.getNumUncompletedGames()) / (double) partialUser.getLibrary().size();
            partialUser.setPercentCompleted((double) Math.round(percent * 100) / 100);
        } else {
            partialUser.setPercentCompleted(0.0);
        }
        partialUser.setFriends(userDao.getUserFriends(userID));
        return partialUser;
    }

    // sort methods: return entire library sorted
    // get methods: return only games that meet criteria

    public User sortUserGamesByGenre(Integer userID) throws NoGamesFoundException, InvalidUserIDException {
        /*User user = getUserByID(userID);
        List<Game> games = user.getLibrary();
        Comparator<Game> gameComparator = Comparator.comparing(Game::getGenre);
        Collections.sort(games, gameComparator);
        user.setLibrary(games);
        return user;*/
        // Will need to revisit how to implement since games
        // can have multiple genres
        throw new UnsupportedOperationException();
    }

/*    public User getUserGamesByGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        User user = getUserByID(userID);
        List<Game> genreGames = gameDao.getUserGamesInGenre(userID, genre);

        if (genreGames.size() == 0) {
            throw new NoGamesFoundException("No " + genre.substring(0,1).toUpperCase()+genre.substring(1).toLowerCase()
                + " games found in user's library");
        }

        user.setLibrary(genreGames);
        return user;
    }*/

    public User sortUserGamesByHoursPlayed(String userID) throws NoGamesFoundException, InvalidUserIDException {
        User user = getUserByID(userID);
        List<Game> games = user.getLibrary();
        Comparator<Game> gameComparator = Comparator.comparing(Game::getHoursPlayed);
        Collections.sort(games, gameComparator);
        user.setLibrary(games);
        return user;
    }

    public User getUserGamesUnderHoursPlayed(String userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException {
        User user = getUserByID(userID);
        List<Game> playTimeGames = gameDao.getUserGamesUnderHoursPlayed(userID, hoursPlayed);

        if (playTimeGames.size() == 0) {
            throw new NoGamesFoundException("No games found under " + hoursPlayed + " hours played in user's library");
        }
        user.setLibrary(playTimeGames);
        return user;
    }

/*    public Game getLeastPlayedGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        List<Game> leastPlayedGenreGames = gameDao.getLeastPlayedGameInGenre(userID, genre);
        if (leastPlayedGenreGames.size() == 0) {
            throw new NoGamesFoundException("No eligible uncompleted games found owned by user " + userID);
        } else {
            return leastPlayedGenreGames.get(rand.nextInt(leastPlayedGenreGames.size()));
        }
    }*/

    public Game changeCompletedStatus(Game game) throws NoGamesFoundException {
        Game changedGame = gameDao.changeCompletedStatus(game.getUserID(), game.getGameID());
        String gameStatus = "";
        if (changedGame.isCompleted()) {
            gameStatus = "completed";
        } else {
            gameStatus = "uncompleted";
        }
        return changedGame;
    }

    public Game pickRandomGame(String userID) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        List<Game> library = getGamesByUserID(userID);
        if (library == null || library.size() == 0) {
            throw new NoGamesFoundException("Library cannot be null or empty");
        }
        return library.get(rand.nextInt(library.size()));
    }

/*    public Game pickRandomGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        List<Game> library = gameDao.getUserGamesInGenre(userID, genre);
        if (library == null || library.size() == 0) {
            throw new NoGamesFoundException("No " + genre.substring(0,1).toUpperCase()+genre.substring(1).toLowerCase()
                    + " games found in user's library");
        }
        return library.get(rand.nextInt(library.size()));
    }*/

    public List<Game> getUncompletedGames(String userID) throws InvalidUserIDException, NoGamesFoundException {
        return gameDao.getUncompletedGames(userID);
    }

    public Game getRandomLeastPlayedUncompletedGame(String userID) throws InvalidUserIDException, NoGamesFoundException {
        List<Game> games = this.gameDao.getLeastPlayedUncompletedGames(userID);
        if (games.size() == 0) {
            throw new NoGamesFoundException("No games found");
        } else {
            return games.get(rand.nextInt(games.size()));
        }
    }

    public User updateUser(User user) throws InvalidUserIDException, InvalidUserNameException, InvalidGameIDException {
        int updateStatus = this.userDao.updateUserInfo(user.getUserID(), user.getName());
        if (updateStatus == 0) {
            try {
                this.userDao.addUser(user.getUserID(), user.getName());
            } catch (NoChangesMadeException ex) {
                System.out.println("User already in database");
            }
        }
        for (Game game : user.getLibrary()) {
            int gameStatus = this.gameDao.updateHoursPlayed(user.getUserID(), game.getGameID(), game.getHoursPlayed());
            if (gameStatus == 0) {
                try {
                    addGameFromSteam(game);
                } catch (NullGameException | NoChangesMadeException ex) {
                    System.out.println("Game not added");
                }
            }
        }
        return getUserByID(user.getUserID());
    }
}
