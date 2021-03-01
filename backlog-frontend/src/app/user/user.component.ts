import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { LibraryService } from '../library.service';
import { User } from '../User';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user : User;

  constructor(private libService : LibraryService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    console.log("HERE");
    let id = -1;
    this.route.queryParams.subscribe(params => {
      id = params['id'];
    });
    console.log("ID = " + id);
    this.libService.getUserByID(id).subscribe(user => {this.user = user;});
  }

}
