package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.Game;

import java.util.List;

public interface GameDao {
    String addGame(String userID, Game game);
    List<Game> getGamesByUserID(String userID) throws InvalidUserIDException;
    void assignGameGenres(Game game);
    List<Game> getUserGamesInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getUserGamesUnderHoursPlayed(String userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getLeastPlayedGameInGenre(String userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    Game changeCompletedStatus(String userID, String gameID) throws NoGamesFoundException;
    double getUserAveragePlayTime(String userID) throws InvalidUserIDException;
    int getNumOfUncompletedGames(String userID) throws InvalidUserIDException;
}
