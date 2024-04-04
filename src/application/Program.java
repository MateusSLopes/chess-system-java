package application;

import boardgame.BoardException;
import chess.ChessMatch;

public class Program {
    public static void main(String[] args) {
        try{
            ChessMatch chessmatch = new ChessMatch();
            UI.printBoard(chessmatch.getPieces());
        } catch (BoardException e) {
            System.out.println(e.getMessage());
        }
    }
}