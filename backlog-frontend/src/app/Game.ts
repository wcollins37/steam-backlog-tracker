import {Genre} from './Genre';

export interface Game {
    gameID : string;
    name : string;
    hoursPlayed : number;
    userName : string;
    genres? : Genre[];
    completed? : boolean;
}