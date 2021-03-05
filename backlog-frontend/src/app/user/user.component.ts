import { Component, Input, OnInit } from '@angular/core';
import { Sort } from '@angular/material/sort';
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
  sortedLibrary : Game[];


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
      this.user.avgPlayTime = Math.round(this.user.avgPlayTime * 100) / 100;
      this.user.percentCompleted = Math.round(this.user.percentCompleted * 100) / 100;
      this.sortedLibrary = this.user.library;
    });
  }

  sortData(sort : Sort) {
    const data = this.user.library.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedLibrary = data;
      return;
    }

    this.sortedLibrary = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case "completed": 
          return this.compare(a.completed, b.completed, isAsc) | this.compare(a.name.toLowerCase(), b.name.toLowerCase(), true);
        case "name": return this.compare(a.name.toLowerCase(), b.name.toLowerCase(), isAsc);
        case "hoursPlayed": return this.compare(a.hoursPlayed, b.hoursPlayed, isAsc);
        default: return 0;
      }
    })
  }

  compare(a: number | string | boolean, b: number | string | boolean, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  getRandomGame() : void {
    this.libService.getRandomGame(this.user.userID).subscribe(x => {
      let formattedTitle : string = x.name.split(' ').join('_');
      window.location.href = "https://store.steampowered.com/app/" + x.gameID + "/" + formattedTitle;
    });
    
  }

  changeDisplayedGames(e) : void {
    console.log(e.target.value);
    switch(e.target.value) {
      case "all":
        this.libService.getFullUserLibrary(this.user.userID).subscribe(x => {
          this.user.library = x;
        });
        break;
      case "uncompleted":
        this.libService.getUncompletedGames(this.user.userID).subscribe(x => {
          this.user.library = x;
        })
        break;
    }
  }

  changeCompleted(game : Game) : void {
    this.libService.swapCompletedStatus(game).subscribe(x => {
      game = x;
    })
  }

  swapCompleted(game) {
    this.libService.swapCompletedStatus(game).subscribe(x => {
      game = x;
    })
  }

}
