package gaas.common


class IllegalGameStateException(message: String) : RuntimeException(message) {

}

val CannotStartGameWithTooFewPlayersException = IllegalGameStateException("TOO_FEW_PLAYER")
val CannotStartGameByNonHostPlayerException = IllegalGameStateException("ONLY_HOST_PLAY_CAN_DO_THIS")
val GameDoesNotExistException = IllegalGameStateException("NO_SUCH_GAME_ID")
val GameHasStartedException = IllegalGameStateException("GAME_HAS_ALREADY_STARTED")
val GameHasFinishedException = IllegalGameStateException("GAME_HAS_ALREADY_FINISHED")

