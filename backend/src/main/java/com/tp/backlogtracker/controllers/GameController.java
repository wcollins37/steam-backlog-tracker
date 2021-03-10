package com.tp.backlogtracker.controllers;

import com.tp.backlogtracker.exceptions.*;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin()
public class GameController {

    @Autowired
    BacklogService service;

    @GetMapping("/steam/library/{userID}")
    public ResponseEntity retrieveSteamUserLibrary(@PathVariable String userID) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=F777B10EBCB73303DD6B5FC5FD76F321&steamid="+userID+"&include_appinfo=true&format=json")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(response.body());
    }

    @GetMapping("/steam/image/{gameID}")
    public ResponseEntity retrieveSteamGameImage(@PathVariable String gameID, @RequestBody User user) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=F777B10EBCB73303DD6B5FC5FD76F321&steamid="+user.getUserID()+"&include_appinfo=true&format=json&input_json={appids: ["+gameID+"]}")).build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(response.body());
    }

    @PostMapping("/game/addmany")
    public ResponseEntity addManyGamesFromSteam(@RequestBody Game[] games) {
        try {
            service.addManyGamesFromSteam(games);
        } catch (NullGameException | InvalidGameIDException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(games);
    }

    @GetMapping("/random/{userID}")
    public ResponseEntity getRandomGameInLibrary(@PathVariable String userID) {
        Game toReturn = null;
        try {
            toReturn = service.pickRandomGame(userID);
        } catch (NoGamesFoundException | InvalidUserIDException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/random/uncompleted/{userID}")
    public ResponseEntity getRandomLeastPlayedUncompletedGame(@PathVariable String userID) {
        Game toReturn = null;
        try {
            toReturn = service.getRandomLeastPlayedUncompletedGame(userID);
        } catch (InvalidUserIDException | NoGamesFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @PutMapping("/swapcompleted")
    public ResponseEntity changeCompletedStatus(@RequestBody Game game) {
        Game toReturn = null;
        try {
            toReturn = service.changeCompletedStatus(game);
        } catch (NoGamesFoundException | NullGameException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/uncompleted/{userID}")
    public ResponseEntity getUncompletedGames(@PathVariable String userID) {
        List<Game> toReturn = new ArrayList<>();
        try {
            toReturn = service.getUncompletedGames(userID);
        } catch (InvalidUserIDException | NoGamesFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok(toReturn);
    }
}
