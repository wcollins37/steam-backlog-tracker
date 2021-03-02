import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LibraryService } from '../library.service';
import { User } from '../User'

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

  @Input()userID : string;
  @Input()name : string;

  constructor(private libService : LibraryService, private router : Router) { }

  ngOnInit(): void {
  }

  addUserByID() {
    this.libService.retrieveSteamUserInfo(this.userID).subscribe(x => {
      if (x.response.players.length === 0) {
        this.router.navigate(["user"], {queryParams: {id: "-1"}});
      }
      this.name = x.response.players[0].personaname;
      let toAdd : User = {userID: this.userID, name: this.name};
      console.log(toAdd);
      this.libService.addUser(toAdd).subscribe((_) => {
        this.router.navigate(["user"], {queryParams: {id: toAdd.userID}});
      });
    });
  }

}
