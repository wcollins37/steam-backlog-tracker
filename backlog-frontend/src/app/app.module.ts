import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GameComponent } from './game/game.component';
import { LibraryComponent } from './library/library.component';
import { UserComponent } from './user/user.component';
import { GetUserComponent } from './get-user/get-user.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AddUserComponent } from './add-user/add-user.component';
import { DetailedGameComponent } from './detailed-game/detailed-game.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {MatTableModule} from '@angular/material/table';
import { GameTableComponent } from './game-table/game-table.component';
import {MatSortModule} from '@angular/material/sort';
import {MatRadioModule} from '@angular/material/radio';

@NgModule({
  declarations: [
    AppComponent,
    GameComponent,
    LibraryComponent,
    UserComponent,
    GetUserComponent,
    AddUserComponent,
    DetailedGameComponent,
    GameTableComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NoopAnimationsModule,
    MatTableModule,
    MatSortModule,
    MatRadioModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
