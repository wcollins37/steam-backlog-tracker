package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
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
    Map<String, List<String>> allFriends = new HashMap<>();

    public UserInMemDao() {
        allUsers.clear();
    }

    @Override
    public String addUser(String userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException {
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
    public User getUserByID(String userID) throws InvalidUserIDException {
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
    public String addFriend(String userID, String friendID) throws InvalidUserIDException, NoChangesMadeException {
        if (userID == null || friendID == null) {
            throw new InvalidUserIDException("User IDs cannot be null");
        }
        if (userID.equals(friendID)) {
            throw new InvalidUserIDException("Users cannot be friends with themselves");
        }
        List<String> friends = allFriends.getOrDefault(userID, new ArrayList<>());
        if (friends.contains(friendID)) {
            throw new NoChangesMadeException("No changes made");
        } else {
            friends.add(friendID);
            allFriends.put(userID, friends);
            List<String> friends2 = allFriends.getOrDefault(friendID, new ArrayList<>());
            friends2.add(userID);
            allFriends.put(friendID, friends2);
            return friendID;
        }
    }

    @Override
    public List<User> getUserFriends(String userID) throws InvalidUserIDException {
        return null;
    }

    @Override
    public boolean checkIfUserOwnsGame(String userID, String gameID) throws InvalidUserIDException, InvalidGameIDException {
        return false;
    }
}
