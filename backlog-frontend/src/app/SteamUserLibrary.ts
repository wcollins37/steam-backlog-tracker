import {SteamGame} from './SteamGame';

export interface SteamUserLibrary {
    response : {
        game_count : number;
        games : SteamGame[];
    }
}