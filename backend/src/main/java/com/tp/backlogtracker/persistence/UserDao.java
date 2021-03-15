package com.tp.backlogtracker.persistence;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.User;

import java.util.List;

public interface UserDao {
    String addUser(String userID, String name, String avatarSrc) throws InvalidUserIDException, InvalidUserNameException, NoChangesMadeException;
    User getUserByID(String userID) throws InvalidUserIDException;
    String addFriend(String userID, String friendID) throws InvalidUserIDException, NoChangesMadeException;
    List<User> getUserFriends(String userID) throws InvalidUserIDException;
    boolean checkIfUserOwnsGame(String userID, String gameID) throws InvalidUserIDException, InvalidGameIDException;
    int updateUserInfo(String userID, String name, String avatarSrc) throws InvalidUserIDException, InvalidUserNameException;
}
