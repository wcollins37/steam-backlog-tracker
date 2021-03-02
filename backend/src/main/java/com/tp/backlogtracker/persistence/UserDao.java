package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.InvalidUserNameException;
import com.tp.backlogtracker.exceptions.NoChangesMadeException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.User;

import java.util.List;

public interface UserDao {
    String addUser(String userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException;
    User getUserByID(String userID) throws InvalidUserIDException;
    String addFriend(String userID, String friendID) throws InvalidUserIDException, NoChangesMadeException;
    List<User> getUserFriends(String userID) throws InvalidUserIDException;
}
