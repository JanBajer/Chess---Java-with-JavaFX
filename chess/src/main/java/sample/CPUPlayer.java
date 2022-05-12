package sample;

import java.util.*;

/**
 * class for computer player
 */
public class CPUPlayer extends Player {
    CPUPlayer(ChessColor color) {
        super(color);
    }

    /**
     * method to get move from computer
     * @param board = actual board(contains all squares)
     * @return move of computer
     */
    @Override
    public Move getMove(Board board) {
        Random rand = new Random();
        int max = getAllLegalMoves().size();
        return getAllLegalMoves().get(rand.nextInt(max));
    }

    /**
     * get random promo piece
     * @param board = actual board(contains all squares)
     * @return piece promoted
     */
    @Override
    public Piece getPromotionPiece(Board board) {
        Random rand = new Random();
        switch (rand.nextInt(4)) {
            case 0:
                return new Queen(getColor());
            case 1:
                return new Rook(getColor());
            case 2:
                return new Bishop(getColor());
            default:
                return new Knight(getColor());
        }
    }
}