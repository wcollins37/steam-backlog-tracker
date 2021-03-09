import { ThrowStmt } from '@angular/compiler';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Sort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
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
  @Input()displayedGames : String = "all";
  lastSort : Sort = {active: "name", direction: "asc"};
  @ViewChild(MatTable,{static:true}) table: MatTable<any>;
  totalLibrarySize : number;


  constructor(private libService : LibraryService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    let id = -1;
    this.route.queryParams.subscribe(params => {
      id = params['id'];
    });
    this.libService.getUserByID(id).subscribe(user => {
      console.log(this.user)
      if (user === null || user === undefined) {
        this.errorMessage = "User not found";
      } else {
        this.user = user;
        this.sortData({active: "name", direction: "asc"})
        this.user.avgPlayTime = Math.round(this.user.avgPlayTime * 100) / 100;
        this.user.percentCompleted = Math.round(this.user.percentCompleted * 100) / 100;
        this.totalLibrarySize = this.user.library.length;
      }
    });
  }

  sortData(sort : Sort) {
    const data = this.user.library.slice();
    if (!sort.active || sort.direction === '') {
      this.user.library = data;
      return;
    }

    this.user.library = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      this.lastSort = sort;
      switch (sort.active) {
        case "completed": 
          if (a.completed === b.completed) {
            return this.compare(a.name.toLowerCase(), b.name.toLowerCase(), true)
          } else {
            return this.compare(a.completed, b.completed, isAsc);
          }
        case "name": return this.compare(a.name.toLowerCase(), b.name.toLowerCase(), isAsc);
        case "hoursPlayed":
          if (a.hoursPlayed === b.hoursPlayed) {
            return this.compare(a.name.toLowerCase(), b.name.toLowerCase(), true);
          } else {
            return this.compare(a.hoursPlayed, b.hoursPlayed, isAsc);
          }
        default: return 0;
      }
    })
  }

  compare(a: number | string | boolean, b: number | string | boolean, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  getRandomGame() : void {
    let randomGame : Game = this.user.library[Math.floor(Math.random() * this.user.library.length)];
    console.log(randomGame);
    this.navigateToStore(randomGame);
  }

  changeDisplayedGames(e) : void {
    console.log(e);
    this.displayedGames = e;
    switch(e) {
      case "all":
        this.libService.getFullUserLibrary(this.user.userID).subscribe(x => {
          this.user.library = x;
          this.sortData(this.lastSort);
        });
        break;
      case "uncompleted":
        this.libService.getUncompletedGames(this.user.userID).subscribe(x => {
          this.user.library = x;
          this.sortData(this.lastSort);
        })
        break;
    }
  }

  swapCompleted(game) {
    this.libService.swapCompletedStatus(game).subscribe(x => {
      if (x.completed) {
        this.user.numUncompletedGames--;
      } else {
        this.user.numUncompletedGames++;
      }
      this.user.percentCompleted = Math.round((this.totalLibrarySize - this.user.numUncompletedGames) / this.totalLibrarySize * 100) / 100;
      this.changeDisplayedGames(this.displayedGames);
    })
  }

  navigateToStore(game : Game) {
    let formattedTitle : string = game.name.split(' ').join('_');
    window.location.href = "https://store.steampowered.com/app/" + game.gameID + "/" + formattedTitle;
  }

  updateUser() {
    this.libService.retrieveSteamUserInfo(this.user.userID).subscribe(steamUser => {
      let updatedUser : User = {userID: this.user.userID, name: steamUser.response.players[0].personaname};
      this.libService.retrieveSteamUserLibrary(this.user.userID).subscribe(steamGames => {
        updatedUser.library = steamGames;
        this.libService.updateUser(updatedUser).subscribe(x => {
          this.user = x;
          this.sortData(this.lastSort);
          this.totalLibrarySize = x.library.length;
        })
      })
    })
  }

  pickLeastPlayedUncompletedGame() {
    this.libService.pickLeastPlayedUncompletedGame(this.user.userID).subscribe(x => {
      this.navigateToStore(x);
    })
  }

}
