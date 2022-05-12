package sample;

import java.util.*;

/**
 * class for piece Rook. I use Name for loading.
 */
public class Rook extends Piece {
    public Rook(ChessColor color) {
        super(color);
        setIcon("R");
        setName("R");
    }
    /**
     * set possible moves for rook
     * @param currSquare = current Square = rook current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     */
    @Override
    public void setPossibleMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        possibleMoves.addAll(oneDirection(1, 0, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(-1, 0, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(0, 1, currSquare, board, currPlayer));
        possibleMoves.addAll(oneDirection(0, -1, currSquare, board, currPlayer));

        setPossibleMoves(possibleMoves);
    }
}