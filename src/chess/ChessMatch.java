package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initalSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessPiece[][] getPieces () {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i = 0; i < board.getRows(); i++) {
            for (int j = 0 ; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i,j);
                // Doing downcasting, because ChessPiece is an intermediate class to protect the Piece class, which cannot be accessed by ChessMatch.
            }
        }
        return mat;
    }
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source,target);
        Piece capturedPiece = makeMove(source,target);

        if (testCheck(currentPlayer)){
            undoMove(source,target,capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        check = testCheck(opponent(currentPlayer));
        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }else {
            nextTurn();
        }

        // Special move en passant
        if (movedPiece instanceof Pawn && target.getRow() == source.getRow()-2 || target.getRow() == source.getRow()+2) {
            enPassantVulnerable = movedPiece;
        }else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    private Piece makeMove(Position source, Position target) {
        Piece p = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p,target);
        ((ChessPiece)p).increaseMoveCount();
        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        // Special move castling King side Rook
        if (p instanceof King && target.getColumn() == source.getColumn()+2) {
            Position sourceT = new Position(source.getRow(), source.getColumn()+3);
            Position targetT = new Position(source.getRow(), source.getColumn()+1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook,targetT);
            rook.increaseMoveCount();
        }

        // Special move castling Queen side Rook
        if (p instanceof King && target.getColumn() == source.getColumn()-2) {
            Position sourceT = new Position(source.getRow(), source.getColumn()-4);
            Position targetT = new Position(source.getRow(), source.getColumn()-1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook,targetT);
            rook.increaseMoveCount();
        }

        // Special move en passant
        if (p instanceof Pawn && target.getColumn() != source.getColumn() && capturedPiece == null) {
            Position pawnPosition;
            if (((ChessPiece)p).getColor() == Color.WHITE) {
                pawnPosition = new Position(target.getRow()+1,target.getColumn());
            }else {
                pawnPosition = new Position(target.getRow()-1,target.getColumn());
            }
            capturedPiece = board.removePiece(pawnPosition);
            capturedPieces.add(capturedPiece);
            piecesOnTheBoard.remove(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        Piece p = board.removePiece(target);
        board.placePiece(p,source);
        ((ChessPiece)p).decreaseMoveCount();
        if (capturedPiece != null) {
            board.placePiece(capturedPiece,target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
        // Special move castling King side Rook
        if (p instanceof King && target.getColumn() == source.getColumn()+2) {
            Position sourceT = new Position(source.getRow(), source.getColumn()+3);
            Position targetT = new Position(source.getRow(), source.getColumn()+1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook,sourceT);
            rook.decreaseMoveCount();
        }
        // Special move castling Queen side Rook
        if (p instanceof King && target.getColumn() == source.getColumn()-2) {
            Position sourceT = new Position(source.getRow(), source.getColumn()-4);
            Position targetT = new Position(source.getRow(), source.getColumn()-1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook,sourceT);
            rook.decreaseMoveCount();
        }
        // Special move en passant
        if (p instanceof Pawn && target.getColumn() != source.getColumn() && capturedPiece == enPassantVulnerable) {
            ChessPiece pawn = (ChessPiece)board.removePiece(target);
            if (((ChessPiece)p).getColor() == Color.WHITE) {
                board.placePiece(pawn,new Position(3,target.getColumn()));
            }else {
                board.placePiece(pawn,new Position(4,target.getColumn()));
            }
        }
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : pieces) {
            if (p instanceof King) {
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color))
            return false;
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i< board.getRows(); i++) {
                for (int j = 0; j< board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i,j);
                        Piece capturedPiece = makeMove(source,target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck)
                            return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponents = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() != color).collect(Collectors.toList());
        for (Piece p : opponents) {
            if (p.possibleMoves()[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position");
        }
        if (currentPlayer != ((ChessPiece)(board.piece(position))).getColor()) {
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece.");
        }
    }
    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void placeNewPiece(int row,char column,ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(row, column).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initalSetup() {
        placeNewPiece(1,'a', new Rook(board, Color.WHITE));
        placeNewPiece(1,'b', new Knight(board, Color.WHITE));
        placeNewPiece(1,'c', new Bishop(board, Color.WHITE));
        placeNewPiece(1,'d', new Queen(board, Color.WHITE));
        placeNewPiece(1,'f', new Bishop(board, Color.WHITE));
        placeNewPiece(1,'e', new King(board, Color.WHITE,this));
        placeNewPiece(1,'g', new Knight(board, Color.WHITE));
        placeNewPiece(1,'h', new Rook(board, Color.WHITE));
        placeNewPiece(2,'a', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'b', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'c', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'d', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'e', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'f', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'g', new Pawn(board, Color.WHITE,this));
        placeNewPiece(2,'h', new Pawn(board, Color.WHITE,this));

        placeNewPiece(8,'a', new Rook(board, Color.BLACK));
        placeNewPiece(8,'b', new Knight(board, Color.BLACK));
        placeNewPiece(8,'c', new Bishop(board, Color.BLACK));
        placeNewPiece(8,'d', new Queen(board, Color.BLACK));
        placeNewPiece(8,'f', new Bishop(board, Color.BLACK));
        placeNewPiece(8,'e', new King(board, Color.BLACK,this));
        placeNewPiece(8,'g', new Knight(board, Color.BLACK));
        placeNewPiece(8,'h', new Rook(board, Color.BLACK));
        placeNewPiece(7,'a', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'b', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'c', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'d', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'e', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'f', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'g', new Pawn(board, Color.BLACK,this));
        placeNewPiece(7,'h', new Pawn(board, Color.BLACK,this));
    }
}
