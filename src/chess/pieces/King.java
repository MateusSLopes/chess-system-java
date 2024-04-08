package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class King extends ChessPiece {
    private ChessMatch chessMatch;
    public King(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece)getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0,0);

        // Above
        p.setValues(position.getRow()-1,position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Below
        p.setValues(position.getRow()+1,position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Left
        p.setValues(position.getRow(),position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Right
        p.setValues(position.getRow(),position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // NE
        p.setValues(position.getRow()-1,position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // NO
        p.setValues(position.getRow()-1,position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // SO
        p.setValues(position.getRow()+1,position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // SE
        p.setValues(position.getRow()+1,position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Special move castling
        if (getMoveCount()==0 && !chessMatch.getCheck()) {
            // Castling King side Rook
            Position posT1 = new Position(position.getRow(),position.getColumn()+3);
            if (testRookCastling(posT1)) {
                Position p1 = new Position(position.getRow(),position.getColumn()+1);
                Position p2 = new Position(position.getRow(),position.getColumn()+2);
                if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2)) {
                    mat[position.getRow()][position.getColumn()+2] = true;
                }
            }

            // Castling Queen side Rook
            Position posT2 = new Position(position.getRow(),position.getColumn()-4);
            if (testRookCastling(posT2)) {
                Position p1 = new Position(position.getRow(),position.getColumn()-1);
                Position p2 = new Position(position.getRow(),position.getColumn()-2);
                Position p3 = new Position(position.getRow(),position.getColumn()-3);
                if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p3)) {
                    mat[position.getRow()][position.getColumn()-2] = true;
                }
            }
        }

        return mat;
    }

    public boolean testRookCastling(Position position) {
        ChessPiece p = (ChessPiece)getBoard().piece(position);
        return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
    }

    @Override
    public String toString() {
        return "K";
    }

}
