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
  @Input()displayedGames : String = "all";

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
      this.sortByName();
    });
  }

  sortByName() : void {
    this.user.library.sort((a, b) => (a.name > b.name ? 1 : -1));
  }

  sortByPlayTime() : void {
    this.user.library.sort((a, b) => {
      return a.hoursPlayed - b.hoursPlayed || (a.name > b.name ? 1 : -1);
    });
  }

  changeGamesDisplayed(val : string) {
    this.displayedGames = val;
    switch(this.displayedGames) {
      case "all":
        this.libService.getFullUserLibrary(this.user.userID).subscribe(x => {this.user.library = x; this.sortByName();});
        break;
      case "uncompleted":
        // testing
        this.user.library = [];
        break;
    }
  }

}
