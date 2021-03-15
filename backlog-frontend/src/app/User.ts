import {Game} from './Game';

export interface User {
    userID : string;
    name : string;
    avatarSrc : string;
    library? : Game[];
    avgPlayTime? : number;
    numUncompletedGames? : number;
    percentCompleted? : number;
    friends? : User[];
    profilePic? : string;
}