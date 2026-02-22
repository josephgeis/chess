package request;

import chess.ChessGame.TeamColor;

public record JoinGameRequest(TeamColor playerColor, int gameID) { }
