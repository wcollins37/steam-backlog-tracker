package com.tp.backlogtracker.controllers;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.InvalidUserNameException;
import com.tp.backlogtracker.exceptions.NoChangesMadeException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.User;
import com.tp.backlogtracker.services.BacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    BacklogService service;

    @PostMapping("/user/add")
    public ResponseEntity addUser(@RequestBody User user) {
        User toReturn = null;
        try {
            toReturn = service.addUser(user.getUserID(), user.getName());
        } catch (InvalidUserIDException | InvalidUserNameException | NoChangesMadeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/user/{userID}/addfriend")
    public ResponseEntity addFriend(@PathVariable Integer userID, @RequestBody User friend) {
        User toReturn = null;
        try {
            toReturn = service.addFriend(userID, friend.getUserID());
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity getUserByID(@PathVariable Integer userID) {
        User toReturn = null;
        try {
            toReturn = service.getUserByID(userID);
        } catch (InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    /*@GetMapping("/user/{userID}/sort/genre")
    public ResponseEntity sortUserGamesByGenre(@PathVariable Integer userID) {
        User toReturn = null;
        try {
            toReturn = service.sortUserGamesByGenre(userID);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }*/

    @GetMapping("/user/{userID}/genre/{genre}")
    public ResponseEntity getUserGamesByGenre(@PathVariable Integer userID, @PathVariable String genre) {
        User toReturn = null;
        try {
            toReturn = service.getUserGamesByGenre(userID, genre);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/user/{userID}/sort/hoursplayed")
    public ResponseEntity sortUserGamesByPlayTime(@PathVariable Integer userID) {
        User toReturn = null;
        try {
            toReturn = service.sortUserGamesByHoursPlayed(userID);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/user/{userID}/hoursplayed/{hoursPlayed}")
    public ResponseEntity getUserGamesUnderPlayTime(@PathVariable Integer userID, @PathVariable Double hoursPlayed) {
        User toReturn = null;
        try {
            toReturn = service.getUserGamesUnderHoursPlayed(userID, hoursPlayed);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }
}
