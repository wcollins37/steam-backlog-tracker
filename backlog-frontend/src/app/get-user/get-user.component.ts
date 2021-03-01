import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LibraryService } from '../library.service';

@Component({
  selector: 'app-get-user',
  templateUrl: './get-user.component.html',
  styleUrls: ['./get-user.component.css']
})
export class GetUserComponent implements OnInit {

  @Input()userID : number;

  constructor(private libService : LibraryService, private router : Router) { }

  ngOnInit(): void {
    
  }

  getUserByID() {
    this.router.navigate(["user"], {queryParams: {id: this.userID}});
  }

}
