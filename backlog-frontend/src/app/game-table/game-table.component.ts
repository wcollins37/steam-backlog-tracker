import { Component, Input, OnInit } from '@angular/core';
import { Sort } from '@angular/material/sort';
import { Game } from '../Game';

@Component({
  selector: 'app-game-table',
  templateUrl: './game-table.component.html',
  styleUrls: ['./game-table.component.css']
})
export class GameTableComponent implements OnInit {

  @Input()dataSource : Game[];
  sortedData : Game[];
  constructor() {
    this.dataSource = this.dataSource.slice();
  }

  sortData(sort : Sort) {
    const data = this.dataSource.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedData = data;
      return;
    }

    this.sortedData = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case "completed": return this.compare(a.completed, b.completed, isAsc);
        case "name": return this.compare(a.name, b.name, isAsc);
        case "hoursPlayed": return this.compare(a.hoursPlayed, b.hoursPlayed, isAsc);
        default: return 0;
      }
    })
  }

  compare(a: number | string | boolean, b: number | string | boolean, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  ngOnInit(): void {
  }

}
