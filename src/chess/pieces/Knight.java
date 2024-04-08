package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.Color;
import chess.ChessPiece;

public class Knight extends ChessPiece {
    public Knight(Board board, Color color) {
        super(board,color);
    }
    private boolean canMove(Position p) {
        if (!getBoard().thereIsAPiece(p))
            return true;
        return isThereOpponentPiece(p);
    }

    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position p = new Position(0,0);

        // 1
        p.setValues(position.getRow()-1, position.getColumn()-2);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 2
        p.setValues(position.getRow()-2, position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 3
        p.setValues(position.getRow()-1, position.getColumn()+2);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 4
        p.setValues(position.getRow()+1, position.getColumn()-2);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 5
        p.setValues(position.getRow()+1, position.getColumn()+2);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 6
        p.setValues(position.getRow()-2, position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 7
        p.setValues(position.getRow()+2, position.getColumn()-1);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        // 8
        p.setValues(position.getRow()+2, position.getColumn()+1);
        if (getBoard().positionExists(p) && canMove(p))
            mat[p.getRow()][p.getColumn()] = true;
        return mat;
    }

    @Override
    public String toString() {
        return "N";
    }
}
