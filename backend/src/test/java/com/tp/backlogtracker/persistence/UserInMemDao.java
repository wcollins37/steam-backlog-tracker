package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.InvalidUserNameException;
import com.tp.backlogtracker.exceptions.NoChangesMadeException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("serviceTesting")
public class UserInMemDao implements UserDao {

    @Autowired
    JdbcTemplate template;

    List<User> allUsers = new ArrayList<>();
    Map<Integer, List<Integer>> allFriends = new HashMap<>();

    public UserInMemDao() {
        allUsers.clear();
    }

    @Override
    public int addUser(Integer userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }
        if (name == null || name == "") {
            throw new InvalidUserNameException("Username cannot be null or empty");
        }
        for (User user : allUsers) {
            if (user.getUserID() == userID) {
                throw new NoChangesMadeException("No changes made");
            }
        }
        User newUser = new User();
        newUser.setUserID(userID);
        newUser.setName(name);
        allUsers.add(newUser);
        return userID;
    }

    @Override
    public User getUserByID(Integer userID) throws InvalidUserIDException {
        if (userID == null) {
            throw new InvalidUserIDException("User ID cannot be null");
        }

        User partialUser = null;

        for (User toCheck : allUsers) {
            if (toCheck.getUserID() == userID) {
                partialUser = toCheck;
                break;
            }
        }

        if (partialUser == null) {
            throw new InvalidUserIDException("No user found with ID " + userID);
        }

        return partialUser;
    }

    @Override
    public int addFriend(Integer userID, Integer friendID) throws InvalidUserIDException, NoChangesMadeException {
        if (userID == null || friendID == null) {
            throw new InvalidUserIDException("User IDs cannot be null");
        }
        if (userID.equals(friendID)) {
            throw new InvalidUserIDException("Users cannot be friends with themselves");
        }
        List<Integer> friends = allFriends.getOrDefault(userID, new ArrayList<>());
        if (friends.contains(friendID)) {
            throw new NoChangesMadeException("No changes made");
        } else {
            friends.add(friendID);
            allFriends.put(userID, friends);
            List<Integer> friends2 = allFriends.getOrDefault(friendID, new ArrayList<>());
            friends2.add(userID);
            allFriends.put(friendID, friends2);
            return friendID;
        }
    }

    @Override
    public List<User> getUserFriends(Integer userID) throws InvalidUserIDException {
        return null;
    }
}
