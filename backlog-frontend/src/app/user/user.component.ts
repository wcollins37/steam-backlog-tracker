import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Game } from '../Game';
import { Genre } from '../Genre';
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
      this.user = user;
      console.log(this.user.library);
    });
  }

  // showAllGames() : void {
  //   this.libService.retrieveSteamUserLibrary(this.user.userID).subscribe(x => {
  //     this.user.library = [];
  //     for (let game of x.response.games) {
  //       let newGame : Game = {gameID: game.appid.toString(),
  //                             name: game.name, 
  //                             hoursPlayed: Math.round((game.playtime_forever / 60.0) * 100) / 100,
  //                             userID: this.user.userID,
  //                             genres: []};
  //       this.user.library.push(newGame);
  //     }
  //   });
  // }

}
