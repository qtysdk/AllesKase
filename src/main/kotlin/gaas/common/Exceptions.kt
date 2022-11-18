package gaas.common


class IllegalGameStateException(message: String) : RuntimeException(message)

// For StartGameUseCase
val CannotStartGameWithTooFewPlayersException = IllegalGameStateException("TOO_FEW_PLAYER")
val CannotStartGameByNonHostPlayerException = IllegalGameStateException("ONLY_HOST_PLAY_CAN_DO_THIS")
val GameDoesNotExistException = IllegalGameStateException("NO_SUCH_GAME_ID")
val GameHasStartedException = IllegalGameStateException("GAME_HAS_ALREADY_STARTED")
val GameHasFinishedException = IllegalGameStateException("GAME_HAS_ALREADY_FINISHED")

val PlayerHasBeenDead = IllegalGameStateException("PLAYER_HAS_BEEN_DEAD")

// For JoinGameUseCase
val GameRoomHasBeenFull = IllegalGameStateException("GAME_ROOM_HAS_BEEN_FULL")
val GameRoomHasStarted = IllegalGameStateException("GAME_ROOM_HAS_STARED")
val PlayerHasBeenInTheGame = IllegalGameStateException("PLAYER_HAS_BEEN_IN_THE_GAME")


// For PlayerActionUseCase

class IllegalPlayerActionException(message: String) : RuntimeException(message)

val InvalidPlayerActionException = IllegalPlayerActionException("INVALID_PLAYER_ACTION")
val InvalidCardIndexException = IllegalPlayerActionException("INVALID_CARD_INDEX")
val InvalidTurnPlayerException = IllegalPlayerActionException("INVALID_TURN_PLAYER")