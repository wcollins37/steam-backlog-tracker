import {Game} from './Game';

export interface User {
    userID : number;
    name : string;
    library : Game[];
    avgPlayTime : number;
    numUncompletedGames : number;
    percentCompleted : number;
    friends : User[];
}