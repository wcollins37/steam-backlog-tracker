import {Game} from './Game';

export interface User {
    userID : string;
    name : string;
    library? : Game[];
    avgPlayTime? : number;
    numUncompletedGames? : number;
    percentCompleted? : number;
    friends? : User[];
}