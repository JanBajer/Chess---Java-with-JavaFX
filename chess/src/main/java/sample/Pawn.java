package sample;

import java.util.*;

/**
 * class for piece Pawn. I use Name for loading.
 */
public class Pawn extends Piece {
    public Pawn(ChessColor color) {
        super(color);
        setIcon("");
        setName("P");
    }

    /**
     * set possible moves for pawn
     * @param currSquare = current Square = pawn current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     */
    @Override
    public void setPossibleMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        // below includes moves with pawn promotion
        possibleMoves.addAll(straightMoves(currSquare, board, currPlayer));
        possibleMoves.addAll(normalCaptureMoves(currSquare, board, currPlayer));
        possibleMoves.addAll(enPassant(currSquare, board, currPlayer));

        setPossibleMoves(possibleMoves);
    }

    /**
     * get all straight moves(moving forward)
     * @param currSquare = current Square = pawn current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     * @return possible straight moves
     */
    public ArrayList<Move> straightMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleEndSquares = new ArrayList<Move>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();
        int dir = (getColor() == ChessColor.WHITE) ? 1 : -1;
        Square destSquare;
        Piece destPiece;

        // never need to worry about going out of bounds because it can only move forward

        if (row + (dir * 1) >= 0 && row + (dir * 1) < 8) {
            destSquare = board.getSquare(row + (1 * dir), col);
            destPiece = destSquare.getPiece();

            if (destPiece == null) {
                if (row + (1 * dir) == 7 || row + (1 * dir) == 0) {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Queen(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Rook(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Knight(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Bishop(getColor())));
                } else {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                }
            } else {
                return possibleEndSquares;
            }
        }

        //first move
        if (getMoveCount() == 0) {
            destSquare = board.getSquare(row + (2 * dir), col);
            destPiece = destSquare.getPiece();

            //if there is something before pawn
            if (destPiece == null) {
                possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
            }
        }

        return possibleEndSquares;
    }

    /**
     * get all normal capture moves
     * @param currSquare = current Square = pawn current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     * @return normal Capture moves
     */
    public ArrayList<Move> normalCaptureMoves(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleEndSquares = new ArrayList<>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();
        int dir = (getColor() == ChessColor.WHITE) ? 1 : -1;
        boolean inRowBound = row + (dir * 1) >= 0 && row + (dir * 1) < 8;

        // never need to worry about going out of bounds because it can only moveforward
        Square destSquare;
        Piece destPiece;

        // capture towards kingside
        if (inRowBound && col + 1 < 8) {
            destSquare = board.getSquare(row + (1 * dir), col + 1);
            destPiece = destSquare.getPiece();

            if (destPiece != null && getColor() != destPiece.getColor()) {
                if (row + (1 * dir) == 7 || row + (1 * dir) == 0) {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Queen(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Rook(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Knight(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Bishop(getColor())));
                } else {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                }
            }
        }

        // capture towards queenside
        if (inRowBound && col - 1 >= 0) {
            destSquare = board.getSquare(row + (1 * dir), col - 1);
            destPiece = destSquare.getPiece();

            if (destPiece != null && getColor() != destPiece.getColor()) {
                if (row + (1 * dir) == 7 || row + (1 * dir) == 0) {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Queen(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Rook(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Knight(getColor())));
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.PAWN_PROMOTION,
                            new Bishop(getColor())));
                } else {
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                }
            }
        }

        return possibleEndSquares;
    }

    /**
     * get enPassant move
     * @param currSquare = current Square = pawn current position
     * @param board = it is chess board
     * @param currPlayer = it is current player. It can be black or white player.
     * @return enPassant move and add it to list
     */
    public ArrayList<Move> enPassant(Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> possibleEndSquares = new ArrayList<Move>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();

        // all -1 because of 0 index
        int oppPawnRow = (getColor() == ChessColor.WHITE) ? 4 : 3;
        int destRow = (getColor() == ChessColor.WHITE) ? 5 : 2;
        Square oppPawnSquare;
        Piece oppPawn;
        Square destSquare;

        if (row == oppPawnRow) {
            if (col - 1 >= 0) {
                oppPawnSquare = board.getSquare(row, col - 1);
                oppPawn = oppPawnSquare.getPiece();

                if (oppPawn != null && oppPawn instanceof Pawn && oppPawn.getMoveCount() == 1
                        && oppPawn.equals(board.getLastPieceToMove())) {
                    destSquare = board.getSquare(destRow, col - 1);
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.EN_PASSANT));
                }
            }
            if (col + 1 < 8) {
                oppPawnSquare = board.getSquare(row, col + 1);
                oppPawn = oppPawnSquare.getPiece();

                if (oppPawn != null && oppPawn instanceof Pawn && oppPawn.getMoveCount() == 1
                        && oppPawn.equals(board.getLastPieceToMove())) {
                    destSquare = board.getSquare(destRow, col + 1);
                    possibleEndSquares.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.EN_PASSANT));
                }
            }
        }

        return possibleEndSquares;
    }
}