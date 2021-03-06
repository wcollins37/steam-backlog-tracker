import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from './Game';
import {User} from './User';
import {tap, catchError, map} from 'rxjs/operators';
import {of} from 'rxjs';
import { UserComponent } from './user/user.component';
import { SteamUserInfo } from './SteamUserInfo';
import { SteamUserLibrary } from './SteamUserLibrary';
import { Genre } from './Genre';
import { ThisReceiver } from '@angular/compiler';

@Injectable({
  providedIn: 'root'
})
export class LibraryService {

  baseURL : string = "http://localhost:8080/api";
  steamURL : string = "http://api.steampowered.com";
  steamStoreURL : string = "http://store.steampowered.com/api";
  apiKey : string = "F777B10EBCB73303DD6B5FC5FD76F321";
  httpOptions = {headers: new HttpHeaders({"Content-Type": "application/json"})};

  constructor(private http : HttpClient) { }

  getUserByID(userID : number) : Observable<User>{
    return this.http.get<User>(this.baseURL + "/user/" + userID)
    .pipe(
      catchError(err => {
        console.log(err);
        let empty = null;
        return of(empty);
      })
    );
  }

  retrieveSteamUserInfo(userID : string) : Observable<SteamUserInfo> {
    return this.http.get<SteamUserInfo>(this.baseURL + "/steam/userinfo/" + userID)
    .pipe(
      catchError(err => {
        console.log(err);
        return of(null);
      })
    );
  }

  addUser(toAdd : User) : Observable<User> {
    return this.http.post<User>(this.baseURL + "/user/add", toAdd, this.httpOptions)
    .pipe(
      catchError(err => {
        console.log(err);
        return of(toAdd);
      })
    );
  }

  retrieveSteamUserLibrary(userID : string) : Observable<Game[]> {
    return this.http.get<SteamUserLibrary>(this.baseURL + "/steam/library/" + userID)
    .pipe(
      map(x => {
        let list : Game[] = [];
        for (let game of x.response.games) {
          list.push({gameID: game.appid.toString(),
                    name: game.name, 
                    hoursPlayed: Math.round(game.playtime_forever / 60.0 * 100) / 100,
                    userID: userID,
                    img: "http://media.steampowered.com/steamcommunity/public/images/apps/" + game.appid + "/" + game.img_logo_url + ".jpg"});
        }
        console.log(list);
        return list;
      })
    );
  }

  checkIfGameOwnedByUserInDatabase(userID : string, gameID : string) : Observable<any> {
    return this.http.get(this.baseURL + "/user/" + userID + "/owns/" + gameID);
  }

  retrieveSteamGameGenres(game : Game) : Observable<any> {
    return this.http.get(this.baseURL + "/steam/genre/" + game.gameID);
  }

  addLibraryToDatabase(games : Game[]) : Observable<any> {
    return this.http.post(this.baseURL + "/game/addmany", games, this.httpOptions)
    .pipe(
      tap(x => console.log(x))
    );
  }

  swapCompletedStatus(game : Game) : Observable<any> {
    return this.http.put(this.baseURL + "/swapcompleted", game, this.httpOptions)
    .pipe(
      tap(x => {console.log(x)})
    );
  }

  getFullUserLibrary(userID : string) : Observable<Game[]> {
    return this.http.get<User>(this.baseURL + "/user/" + userID)
    .pipe(
      map(x => {
        return x.library;
      })
    )
  }

  getRandomGame(userID : string) : Observable<Game> {
    return this.http.get<Game>(this.baseURL + "/random/" + userID);
  }

  getUncompletedGames(userID : string) : Observable<Game[]> {
    return this.http.get<Game[]>(this.baseURL + "/uncompleted/" + userID);
  }

  updateUser(user : User) : Observable<User> {
    return this.http.put<User>(this.baseURL + "/user/update", user, this.httpOptions);
  }

  pickLeastPlayedUncompletedGame(userID : string) : Observable<Game> {
    return this.http.get<Game>(this.baseURL + "/random/uncompleted/" + userID);
  }

  deleteGameFromLibrary(gameID : string, userID : string) : Observable<User> {
    return this.http.delete<User>(this.baseURL + "/delete/" + gameID + "/" + userID)
    .pipe(
      catchError(err => {
        return of(null);
      })
    )
  }
}