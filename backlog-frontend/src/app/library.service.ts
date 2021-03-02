import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from './Game';
import {User} from './User';
import {tap, catchError} from 'rxjs/operators';
import {of} from 'rxjs';
import { UserComponent } from './user/user.component';
import { SteamUserInfo } from './SteamUserInfo';

@Injectable({
  providedIn: 'root'
})
export class LibraryService {

  baseURL : string = "http://localhost:8080/api";
  steamURL : string = "http://api.steampowered.com";
  apiKey : string = "F777B10EBCB73303DD6B5FC5FD76F321";
  httpOptions = {headers: new HttpHeaders({"Content-Type": "application/json"})};

  constructor(private http : HttpClient) { }

  getUserByID(userID : number) : Observable<User>{
    return this.http.get<User>(this.baseURL + "/user/" + userID)
    .pipe(
      tap(x => console.log(x)),
      catchError(err => {
        console.log(err);
        let empty = null;
        return of(empty);
      })
    );
  }

  retrieveSteamUserInfo(userID : string) : Observable<SteamUserInfo> {
    return this.http.get<SteamUserInfo>(this.baseURL + "/test")
    .pipe(
      tap(x => console.log(x)),
      catchError(err => {
        console.log(err);
        return of(null);
      })
    );
  }

  addUser(toAdd : User) : Observable<User> {
    console.log("User = " + toAdd.name);
    return this.http.post<User>(this.baseURL + "/user/add", toAdd, this.httpOptions);
  }
}
