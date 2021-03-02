import { SteamPlayer } from "./SteamPlayer";

export interface SteamUserInfo {
    response: {
        players: SteamPlayer[]
    }
}