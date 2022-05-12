package sample;

import java.util.*;

/**
 * Piece Bishop, it has Icon B. I also use Name for loading.
 */
public class Bishop extends Piece {
    public Bishop(ChessColor color) {
        super(color);
        setIcon("B");
        setName("B");
    }

    /**
     * it set possible moves for Bishop
     * @param currSquare = current Square = Bishops current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     */
    @Override
    public void setPossibleMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        possibleMoves.addAll(oneDirection(1, 1, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(-1, 1, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(1, -1, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(-1, -1, currSquare, board, currPlayer));

        setPossibleMoves(possibleMoves);
    }
}