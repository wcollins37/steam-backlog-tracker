package com.tp.backlogtracker.controllers;

import com.tp.backlogtracker.exceptions.InvalidUserIDException;
import com.tp.backlogtracker.exceptions.NoGamesFoundException;
import com.tp.backlogtracker.models.Game;
import com.tp.backlogtracker.models.User;
import com.tp.backlogtracker.models.UserGameRequest;
import com.tp.backlogtracker.services.BacklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin()
public class GameController {

    @Autowired
    BacklogService service;

    @GetMapping("/steam/library/{userID}")
    public ResponseEntity retrieveSteamUserLibrary(@PathVariable String userID) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=F777B10EBCB73303DD6B5FC5FD76F321&steamid=76561198022304257&include_appinfo=true&format=json")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(response.body());
    }

    @GetMapping("/steam/genre/{gameID}")
    public ResponseEntity retrieveSteamGameGenres(@PathVariable String gameID) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://store.steampowered.com/api/appdetails?appids="+gameID+"&filters=genres")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(response.body());
    }

    @GetMapping("/pick/{genre}")
    public ResponseEntity getBacklogGameInGenre(@RequestBody User user, @PathVariable String genre) {
        Game toReturn = null;
        try {
            toReturn = service.getLeastPlayedGameInGenre(user.getUserID(), genre);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/random")
    public ResponseEntity getRandomGameInLibrary(@RequestBody User user) {
        Game toReturn = null;
        try {
            toReturn = service.pickRandomGame(user.getUserID());
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/random/genre/{genre}")
    public ResponseEntity getRandomGameInGenre(@RequestBody User user, @PathVariable String genre) {
        Game toReturn = null;
        try {
            toReturn = service.pickRandomGameInGenre(user.getUserID(), genre);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @PutMapping("/swapcompleted")
    public ResponseEntity changeCompletedStatus(@RequestBody UserGameRequest request) {
        String toReturn = null;
        try {
            toReturn = service.changeCompletedStatus(request.getUserID(), request.getGameID());
        } catch (NoGamesFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }
}
