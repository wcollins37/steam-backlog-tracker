package com.tp.backlogtracker.controllers;

import com.tp.backlogtracker.exceptions.*;
import com.tp.backlogtracker.models.User;
import com.tp.backlogtracker.services.BacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin()
public class UserController {

    @Autowired
    BacklogService service;

    @GetMapping("/steam/userinfo/{userID}")
    public ResponseEntity retrieveSteamUserInfo(@PathVariable String userID) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=F777B10EBCB73303DD6B5FC5FD76F321&steamids="+userID+"&format=json")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(response.body());
    }

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
    public ResponseEntity addFriend(@PathVariable String userID, @RequestBody User friend) {
        User toReturn = null;
        try {
            toReturn = service.addFriend(userID, friend.getUserID());
        } catch (InvalidUserIDException | NoChangesMadeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/user/{userID}/owns/{gameID}")
    public ResponseEntity checkIfUserOwnsGame(@PathVariable String userID, @PathVariable String gameID) {
        boolean toReturn = false;
        try {
            toReturn = service.checkIfUserOwnsGame(userID, gameID);
        } catch (InvalidUserIDException | InvalidGameIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity getUserByID(@PathVariable String userID) {
        User toReturn = null;
        try {
            toReturn = service.getUserByID(userID);
        } catch (InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @PutMapping("user/update")
    public ResponseEntity updateUser(@RequestBody User user) {
        User toReturn = null;
        try {
            toReturn = service.updateUser(user);
        } catch (InvalidUserNameException | InvalidGameIDException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }
}
