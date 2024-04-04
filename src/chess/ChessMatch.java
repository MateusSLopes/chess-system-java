package chess;

import boardgame.Board;
import boardgame.BoardException;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    private Board board;

    public ChessMatch() throws BoardException {
        board = new Board(8,8);
        initalSetup();
    }

    public ChessPiece[][] getPieces () throws BoardException {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i = 0; i < board.getRows(); i++) {
            for (int j = 0 ; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i,j);
                // Doing downcasting, because ChessPiece is an intermediate class to protect the Piece class, which cannot be accessed by ChessMatch.
            }
        }
        return mat;
    }

    private void placeNewPiece(int row,char column,ChessPiece piece) throws BoardException {
        board.placePiece(piece, new ChessPosition(row, column).toPosition());
    }

    private void initalSetup() throws BoardException {
        placeNewPiece(6,'a',new Rook(board,Color.WHITE));
        placeNewPiece(8,'d',new King(board,Color.BLACK));
        placeNewPiece(1,'h',new King(board,Color.BLACK));
    }
}
