package sample;

import java.util.*;

/**
 * class for piece King. I use Name for loading.
 */
public class King extends Piece {
    public King(ChessColor color) {
        super(color);
        setIcon("K");
        setName("K");
    }

    /**
     * it set possible moves for King
     * @param currSquare = current Square = King current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     */
    @Override
    public void setPossibleMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        // yet to do castling
        possibleMoves.addAll(regularMoves(currSquare, board, currPlayer));
        possibleMoves.addAll(castling(currSquare, board, currPlayer));

        setPossibleMoves(possibleMoves);
    }

    /**
     * it set possible regular moves for King(not castling)
     * @param currSquare = current Square = King current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     * @return squares where can King move
     */
    public ArrayList<Move> regularMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleEndSquares = new ArrayList<Move>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();

        int[] dRows = new int[] { 1, -1, 0, 0, 1, -1, 1, -1 };
        int[] dCols = new int[] { 0, 0, 1, -1, 1, 1, -1, -1 };

        for (int i = 0; i < 8; i++) {
            int dRow = row + dRows[i];
            int dCol = col + dCols[i];

            if (dRow < 8 && dRow >= 0 && dCol < 8 && dCol >= 0) {
                Square destSquare = board.getSquare(dRow, dCol);
                Piece destPiece = destSquare.getPiece();

                if (destPiece == null || destPiece.getColor() != getColor()) {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                }
            }
        }

        return possibleEndSquares;
    }

    /**
     * it set possible castling moves for King (not regular moves)
     * @param currSquare = current Square = King current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     * @return squares where can King move
     */
    public ArrayList<Move> castling(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleEndSquares = new ArrayList<Move>();

        // for all of them, the move count being 0 necessarily means that it is
        // still on its starting square, and that the piece on that square is
        // necessarily a rook, so i don't have to verify that

        Square s1;
        Square s2;
        Square s3;
        Square s4;

        int row = (getColor() == ChessColor.WHITE) ? 0 : 7;

        //squares where can be piece
        s1 = board.getSquare(row, 5);
        s2 = board.getSquare(row, 6);
        s3 = board.getSquare(row, 7);

        //KingSIDE
        //if there are not pieces on squares s1,s2 and piece 3 moveCount is 0
        if (getMoveCount() == 0 && s1.getPiece() == null && s2.getPiece() == null && s3.getPiece() != null
                && s3.getPiece().getMoveCount() == 0) {
            possibleEndSquares.add(new Move(currPlayer, currSquare, s2, board, MoveType.CASTLE));
        }

        // QUEENSIDE
        s1 = board.getSquare(row, 3);
        s2 = board.getSquare(row, 2);
        s3 = board.getSquare(row, 1);
        s4 = board.getSquare(row, 0);

        //if there are not pieces on squares s1,s2,s3 and piece 4 moveCount is 0
        if (getMoveCount() == 0 && s1.getPiece() == null && s2.getPiece() == null && s3.getPiece() == null
                && s4.getPiece() != null && s4.getPiece().getMoveCount() == 0) {
            possibleEndSquares.add(new Move(currPlayer, currSquare, s2, board, MoveType.CASTLE));
        }

        return possibleEndSquares;
    }
}