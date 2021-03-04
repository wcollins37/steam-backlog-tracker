import {Genre} from './Genre';

export interface Game {
    gameID : string;
    name : string;
    hoursPlayed : number;
    userID : string;
    genres? : Genre[];
    completed? : boolean;
}