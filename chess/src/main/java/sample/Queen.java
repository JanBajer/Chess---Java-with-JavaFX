package sample;

import java.util.*;

/**
 * class for piece Queen. I use Name for loading.
 */
public class Queen extends Piece {
    public Queen(ChessColor color) {
        super(color);
        setIcon("Q");
        setName("Q");
    }

    /**
     * set possible moves for queen
     * @param currSquare = current Square = queen current position
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
        possibleMoves.addAll(oneDirection(1, 0, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(-1, 0, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(0, 1, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(0, -1, currSquare, board, currPlayer));

        setPossibleMoves(possibleMoves);
    }
}