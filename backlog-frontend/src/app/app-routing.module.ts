import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GetUserComponent } from './get-user/get-user.component';
import { UserComponent } from './user/user.component';
import {AddUserComponent} from './add-user/add-user.component';
import {DetailedGameComponent} from './detailed-game/detailed-game.component';

const routes: Routes = [{path: "", component: AddUserComponent},
                        {path: "user", component: UserComponent},
                        {path: "game", component: DetailedGameComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
