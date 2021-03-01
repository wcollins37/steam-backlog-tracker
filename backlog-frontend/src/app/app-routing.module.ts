import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GetUserComponent } from './get-user/get-user.component';
import { UserComponent } from './user/user.component';

const routes: Routes = [{path: "", component: GetUserComponent},
                        {path: "user", component: UserComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
