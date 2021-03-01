package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.Game;

import java.util.List;

public interface GameDao {
    int addGame(Integer userID, Game game);
    List<Game> getGamesByUserID(Integer userID) throws InvalidUserIDException;
    void assignGameGenres(Game game);
    List<Game> getUserGamesInGenre(Integer userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getUserGamesUnderHoursPlayed(Integer userID, Double hoursPlayed) throws NoGamesFoundException, InvalidUserIDException;
    List<Game> getLeastPlayedGameInGenre(Integer userID, String genre) throws NoGamesFoundException, InvalidUserIDException;
    Game changeCompletedStatus(Integer userID, Integer gameID) throws NoGamesFoundException;
    double getUserAveragePlayTime(Integer userID) throws InvalidUserIDException;
    int getNumOfUncompletedGames(Integer userID) throws InvalidUserIDException;
}
