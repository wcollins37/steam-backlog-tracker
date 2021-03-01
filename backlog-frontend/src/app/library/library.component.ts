import { Component, Input, OnInit } from '@angular/core';
import { Game } from '../Game';

@Component({
  selector: 'app-library',
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.css']
})
export class LibraryComponent implements OnInit {

  @Input()games : Game[];

  constructor() { }

  ngOnInit(): void {
  }

}
