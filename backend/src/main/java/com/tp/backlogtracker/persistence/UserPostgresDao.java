package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidGameIDException;
import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.InvalidUserNameException;
import com.tp.backlogtracker.exceptions.NoChangesMadeException;
import com.tp.backlogtracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"production","daoTesting"})
public class UserPostgresDao implements UserDao {

    @Autowired
    JdbcTemplate template;


    @Override
    public String addUser(String userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (name == null || name == "") {
            throw new InvalidUserNameException("Username cannot be null or empty");
        }
        int status;
        try {
            status = template.update("insert into \"Users\"(\"userID\",\"name\") values(?,?);",
                    userID,
                    name);
        } catch (DataAccessException ex) {
            throw new NoChangesMadeException("No changes made");
        }
        if (status < 1) {
            throw new NoChangesMadeException("No changes made");
        }
        return userID;
    }

    @Override
    public User getUserByID(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }

        User partialUser = null;

        try {
            partialUser = template.queryForObject(
                    "select \"userID\",\"name\"\n" +
                            "from \"Users\"\n" +
                            "where \"Users\".\"userID\" = ?;",
                    new PartialUserMapper("userID"),
                    userID);
        } catch (EmptyResultDataAccessException ex) {
            throw new InvalidUserIDException("No user with ID " + userID + " found");
        }

        return partialUser;
    }

    @Override
    public String addFriend(String userID, String friendID) throws InvalidUserIDException, NoChangesMadeException {
        if (userID == null || friendID == null) {
            throw new InvalidUserIDException("User IDs cannot be null");
        }
        if (userID.equals(friendID)) {
            throw new InvalidUserIDException("User cannot be friends with themselves");
        }
        int status;
        try {
            status = template.update("insert into \"UserFriends\"(\"userID\",\"friendID\") values(?,?),(?,?);",
                    userID,
                    friendID,
                    friendID,
                    userID);
        } catch (DataAccessException ex) {
            throw new NoChangesMadeException("No changes made");
        }
        if (status < 1) {
            throw new NoChangesMadeException("No changes made");
        }
        return friendID;
    }

    @Override
    public List<User> getUserFriends(String userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        List<User> partialFriends = new ArrayList<>();
        try {
            partialFriends = template.query("select uf.\"friendID\",us.\"name\"\n" +
                    "from \"UserFriends\" as uf\n" +
                    "inner join \"Users\" as us on uf.\"friendID\" = us.\"userID\"\n" +
                    "where uf.\"userID\" = ? order by uf.\"friendID\";",
                    new PartialUserMapper("friendID"),
                    userID);
        } catch (DataAccessException ex) {

        }
        return partialFriends;
    }

    @Override
    public boolean checkIfUserOwnsGame(String userID, String gameID) throws InvalidUserIDException, InvalidGameIDException {
        if (userID == null || userID == "") {
            throw new InvalidUserIDException("User ID cannot be null or empty");
        }
        if (gameID == null || gameID == "") {
            throw new InvalidGameIDException("Game ID cannot be null or empty");
        }

        int count = 0;
        try {
            count = template.queryForObject("select count(*) from \"UserGames\" where \"userID\" = ? and \"gameID\" = ?;",
                    new IntMapper("count"),
                    userID,
                    gameID);
        } catch (DataAccessException ex) {
            return false;
        }
        return count > 0;
    }

    @Override
    public int updateUserInfo(String userID, String name) throws InvalidUserIDException, InvalidUserNameException {
        if (userID == null || userID == "") {
            throw new InvalidUserIDException("User ID cannot be null or empty");
        }
        if (name == null || name == "") {
            throw new InvalidUserNameException("Username cannot be null or empty");
        }

        int status = -1;
        try {
            status = template.update("update \"Users\" set \"name\" = ?\n" +
                    "where \"userID\" = ?;",
                    name,
                    userID);
        } catch (DataAccessException ex) {
            return 0;
        }
        return status;
    }
}
