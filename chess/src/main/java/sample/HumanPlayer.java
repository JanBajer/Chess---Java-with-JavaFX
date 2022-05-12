package sample;

import java.util.Random;

public class HumanPlayer extends Player {
    HumanPlayer(ChessColor color) {
        super(color);
    }


    /**
     * method to get move from human
     * @param board = actual board(contains all squares)
     * @return move of human
     */
    @Override
    public Move getMove(Board board) {
        boolean oneTime = true;
        while (board.getClickQueue().size() < 2) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
            if (oneTime && board.getClickQueue().size() == 1) {
                board.refreshSquares();
                oneTime = false;
            }
        }
        return new Move(this, board.getClickQueue().get(0), board.getClickQueue().get(1));
    }


    /**
     * get promo piece
     * @param board = actual board(contains all squares)
     * @return promoted piece
     */
    @Override
    public Piece getPromotionPiece(Board board) {
        while (board.getInputtedPromoPiece() == null) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return board.getInputtedPromoPiece();
    }
}