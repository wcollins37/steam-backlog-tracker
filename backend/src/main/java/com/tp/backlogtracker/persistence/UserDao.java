package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.InvalidUserNameException;
import com.tp.backlogtracker.exceptions.NoChangesMadeException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.User;

import java.util.List;

public interface UserDao {
    int addUser(Integer userID, String name) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException;
    User getUserByID(Integer userID) throws InvalidUserIDException;
    int addFriend(Integer userID, Integer friendID) throws InvalidUserIDException, NoChangesMadeException;
    List<User> getUserFriends(Integer userID) throws InvalidUserIDException;
}
