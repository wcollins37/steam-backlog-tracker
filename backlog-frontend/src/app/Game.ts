export interface Game {
    gameID : number;
    name : string;
    hoursPlayed : number;
    userName : string;
    genres? : string[];
    completed? : boolean;
}