import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from './Game';
import {User} from './User';
import {tap, catchError} from 'rxjs/operators';
import {of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LibraryService {

  baseURL : string = "http://localhost:8080/api";
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
}
