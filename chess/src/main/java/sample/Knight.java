package sample;

import java.util.*;

/**
 * class for piece Knight. I use Name for loading.
 */
public class Knight extends Piece {
    public Knight(ChessColor color) {
        super(color);
        setIcon("N");
        setName("N");
    }

    /**
     * it set possible moves for Knight
     * @param currSquare = current Square = Knight current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     */
    @Override
    public void setPossibleMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();
        // not the basketball player
        int[] dRows = new int[] { 1, -1, 1, -1, 2, -2, 2, -2 };
        int[] dCols = new int[] { 2, 2, -2, -2, 1, 1, -1, -1 };

        for (int i = 0; i < 8; i++) {
            int dRow = row + dRows[i];
            int dCol = col + dCols[i];

            if (dRow < 8 && dRow >= 0 && dCol < 8 && dCol >= 0) {
                Square destSquare = board.getSquare(dRow, dCol);
                Piece destPiece = destSquare.getPiece();

                if (destPiece == null || destPiece.getColor() != getColor()) {
                    possibleMoves.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                }
            }
        }
        setPossibleMoves(possibleMoves);
    }
}