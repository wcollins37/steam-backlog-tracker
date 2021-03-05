package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.Game;

import java.util.List;

public interface GameDao {
    Game addGame(Game game) throws NullGameException, InvalidGameIDException, NoChangesMadeException;
    void addGameToUser(Game game) throws NullGameException, InvalidUserIDException, NoChangesMadeException;
    List<Game> getGamesByUserID(String userID) throws InvalidUserIDException;
    void assignGameGenres(Game game);
//    List<Game> getUserGamesInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getUserGamesUnderHoursPlayed(String userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getUncompletedGames(String userID) throws InvalidUserIDException, NoGamesFoundException;
//    List<Game> getLeastPlayedGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    Game changeCompletedStatus(String userID, String gameID) throws NoGamesFoundException;
    double getUserAveragePlayTime(String userID) throws InvalidUserIDException;
    int getNumOfUncompletedGames(String userID) throws InvalidUserIDException;
}
