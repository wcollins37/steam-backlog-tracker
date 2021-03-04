import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LibraryService } from '../library.service';
import { User } from '../User'
import {Game} from '../Game'

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

  @Input()userID : string;
  @Input()name : string;
  steamLib : Game[];
  gamesNotInDBLib : Game[];

  constructor(private libService : LibraryService, private router : Router) { }

  ngOnInit(): void {
  }

  addUserByID() {
    console.log("CLICKED");
    this.libService.retrieveSteamUserInfo(this.userID).subscribe(x => {
      if (x.response.players.length === 0) {
        this.router.navigate(["user"], {queryParams: {id: this.userID}});
      }
      else {
        this.name = x.response.players[0].personaname;
        let toAdd : User = {userID: this.userID, name: this.name};
        console.log(toAdd);
        this.libService.addUser(toAdd).subscribe(added => {
          this.libService.retrieveSteamUserLibrary(added.userID).subscribe(addedLib => {
            this.steamLib = addedLib;
            this.libService.addLibraryToDatabase(addedLib).subscribe((_) => {
              this.gamesNotInDBLib = this.steamLib.filter(game => !addedLib.includes(game));
              console.log("Games left out: " + this.gamesNotInDBLib);
              this.router.navigate(["user"], {queryParams: {id: this.userID}});
            })
          })
        });
      }
    });
  }

}
