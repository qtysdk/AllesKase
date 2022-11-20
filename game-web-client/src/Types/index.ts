export interface CreateGameResponse {
    gameId: string
    playerId: string
}

export interface Player {
    playerId: string;
    keptCards: string;
    score: number;
    alive: boolean;
}

export interface DemoZone {
    cards: string;
}

export interface Turn {
    actionList: Array<string>;
    actionIndex: Array<number>;
    player: Player;
    diceValue: number;
}

export interface DeckInfo {
    value: number;
    numberOfCards: number;
}

export interface GameView {
    gameId: string;
    players: Array<Player>;
    demoZone: Array<number>;
    turn: Turn;
    providingDeck: DeckInfo;
    droppedDeck: DeckInfo;
}
