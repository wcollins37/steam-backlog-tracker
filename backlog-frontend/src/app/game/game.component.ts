import { Component, Input, OnInit } from '@angular/core';
import { Game } from '../Game';
import { LibraryService } from '../library.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {

  @Input()game : Game;

  constructor(private libService : LibraryService) { }

  ngOnInit(): void {
  }

  changeCompleted() : void {
    this.libService.swapCompletedStatus(this.game).subscribe(x => {
      this.game = x;
    })
  }

}
