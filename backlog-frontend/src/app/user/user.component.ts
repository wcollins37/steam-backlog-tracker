import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Game } from '../Game';
import { LibraryService } from '../library.service';
import { User } from '../User';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user : User;
  errorMessage : String = "";

  constructor(private libService : LibraryService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    let id = -1;
    this.route.queryParams.subscribe(params => {
      id = params['id'];
    });
    this.libService.getUserByID(id).subscribe(user => {
      if (user === null) {
        this.errorMessage = "User not found";
      }
      console.log(this.errorMessage);
      this.user = user;
      if (user != null && user != undefined) {
        this.showAllGames();
      }
    });
  }

  showAllGames() : void {
    this.libService.retrieveSteamUserLibrary(this.user.userID).subscribe(x => {
      console.log(x);
      let gameLibrary : Game[] = [];
      for (let game of x.response.games) {
        let newGame : Game = {gameID: game.appid, name: game.name, hoursPlayed: game.playtime_forever / 60.0, userName: this.user.name};
        gameLibrary.push(newGame);
      }
      console.log(gameLibrary);
    });
  }

}
