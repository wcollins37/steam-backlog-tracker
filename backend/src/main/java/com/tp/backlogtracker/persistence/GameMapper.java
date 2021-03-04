package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.models.Game;
import com.tp.backlogtracker.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class GameMapper implements RowMapper<Game> {

    @Override
    public Game mapRow(ResultSet resultSet, int i) throws SQLException {
        Game mappedGame = new Game();

        mappedGame.setGameID(resultSet.getString("gameID"));
        mappedGame.setName(resultSet.getString("gameName"));
        mappedGame.setUserID(resultSet.getString("userID"));
        mappedGame.setHoursPlayed(resultSet.getDouble("hoursPlayed"));
        mappedGame.setCompleted(resultSet.getBoolean("completed"));

        return mappedGame;
    }
}
