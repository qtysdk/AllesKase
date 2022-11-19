import {CreateGameResponse, GameView} from "../Types";

interface GetGameViewResponse {
    onGameView(view: GameView)

    onError(reason: string)
}

export function GetGameView(gameId: string, callback: GetGameViewResponse) {
    return fetch(`http://localhost:8080/games/${gameId}/status`, {
        method: "GET",
        headers: {
            "content-type": "application/json"
        },
    }).then(data => data.json()).then((view: GameView) => {
        callback.onGameView(view);
    }).catch(reason => callback.onError(reason));
}

interface CreateGameCallback {
    onGameCreated(response: CreateGameResponse)

    onError(reason: string)
}


export function CreateGameApi(playerId: string, callback: CreateGameCallback) {
    fetch("http://localhost:8080/games", {
        method: "POST",
        headers: {
            "content-type": "application/json"
        },
        body: JSON.stringify({"playerId": playerId}),
    }).then(data => data.json()).then((response: CreateGameResponse) => {
        callback.onGameCreated({gameId: response.gameId, playerId: playerId});
    }).catch(reason => callback.onError(reason));
}

interface JoinGameResponse {
    isSuccess: boolean
}

export function JoinGameApi(gameId: string, playerId: string, callback: CreateGameCallback) {
    fetch(`http://localhost:8080/games/${gameId}/player/${playerId}/join`, {
        method: "POST",
        headers: {
            "content-type": "application/json"
        },
        body: JSON.stringify({"playerId": playerId}),
    }).then(data => data.json()).then((response: JoinGameResponse) => {
        if (response.isSuccess) {
            callback.onGameCreated({gameId: gameId, playerId: playerId});
        } else {
            callback.onError("Cannot join the game")
        }
    }).catch(reason => callback.onError(reason));
}

// /games/{gameId}/player/{playerId}/act
export function PlayActionApi(gameId: string, playerId: string, action: string, index: number) {
    fetch(`http://localhost:8080/games/${gameId}/player/${playerId}/act`, {
        method: "POST",
        headers: {
            "content-type": "application/json"
        },
        body: JSON.stringify({"action": action, "index": index}),
    }).then(data => data.json()).then((response) => {
        console.log(response);
    }).catch(reason => console.log(reason));
}
