package com.tp.backlogtracker.models;

import java.util.List;

public class User {
    Integer userID;
    String name;
    List<Game> library;
    Double avgPlayTime;
    Integer numUncompletedGames;
    Double percentCompleted;
    List<User> friends;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Game> getLibrary() {
        return library;
    }

    public void setLibrary(List<Game> library) {
        this.library = library;
    }

    public Double getAvgPlayTime() {
        return avgPlayTime;
    }

    public void setAvgPlayTime(Double avgPlayTime) {
        this.avgPlayTime = avgPlayTime;
    }

    public Integer getNumUncompletedGames() {
        return numUncompletedGames;
    }

    public void setNumUncompletedGames(Integer numUncompletedGames) {
        this.numUncompletedGames = numUncompletedGames;
    }

    public Double getPercentCompleted() {
        return percentCompleted;
    }

    public void setPercentCompleted(Double percentCompleted) {
        this.percentCompleted = percentCompleted;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}
