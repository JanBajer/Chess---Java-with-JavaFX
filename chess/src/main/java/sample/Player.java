package sample;

import java.util.*;

/**
 * in Player class I define player, it is extended by Human/CPU player
 */
public abstract class Player {
    private ChessColor color;
    private ArrayList<Move> allLegalMoves;

    /**
     * @param color = each player has color(Black/WHITE)
     */
    Player(ChessColor color) {
        this.color = color;
    }

    /**
     * @return get color of player
     */
    public ChessColor getColor() {
        return color;
    }

    /**
     * @param board = board
     * @return move of player
     */
    public abstract Move getMove(Board board);

    /**
     * @param board = board
     * @return get promotion piece
     */
    public abstract Piece getPromotionPiece(Board board);

    /**
     * @return get all legal moves of player
     */
    public ArrayList<Move> getAllLegalMoves() {
        return allLegalMoves;
    }

    /**
     * set all legal moves of player
     * @param board = board
     */
    public void setAllLegalMoves(Board board) {
        ArrayList<Move> allLegalMoves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square currSquare = board.getSquare(i, j);
                Piece piece = currSquare.getPiece();

                if (piece != null && piece.getColor() == getColor()) {
                    piece.setPossibleMoves(currSquare, board, this);
                    piece.setLegalMoves();
                    allLegalMoves.addAll(piece.getLegalMoves());
                }
            }
        }

        setAllLegalMoves(allLegalMoves);
        ;
    }

    /**
     * set all legal moves
     * @param allLegalMoves = all legal moves of player
     */
    public void setAllLegalMoves(ArrayList<Move> allLegalMoves) {
        this.allLegalMoves = allLegalMoves;
    }

    /**
     * @param userMove = move of user
     * @return check if move is legal, if not it returns null
     */
    public Move getEquivalentLegalMove(Move userMove) {
        for (Move legalMove : getAllLegalMoves()) {
            // equals move is overloaded for types move and String
            if (legalMove.equalsMove(userMove)) {
                return legalMove;
            }
        }

        return null;
    }


    /**
     * @param otherMove = different move
     * @return true/false
     */
    public boolean legalMovesContains(Move otherMove) {
        for (Move legalMove : getAllLegalMoves()) {
            if (legalMove.equalsMove(otherMove)) {
                return true;
            }
        }
        return false;
    }
}