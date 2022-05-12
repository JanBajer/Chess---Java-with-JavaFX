package sample;

import java.util.ArrayList;

/**
 * abstract class Piece, it is extended by classes: Bishop, King, Knight, Pawn, Queen, Rook
 */
public abstract class Piece {
    private ChessColor color;
    private String icon;
    private String name;
    private int moveCount;
    private int alive;
    int pieceCount;
    private ArrayList<Move> possibleMoves;
    private ArrayList<Move> legalMoves;

    /**
     * each piece has information of color and movecount(default 0).
     * @param color = White or Black color
     */
    public Piece(ChessColor color) {
        this.color = color;
        this.moveCount = 0;
    }

    /**
     * get color of piece
     * @return color
     */
    public ChessColor getColor() {
        return color;
    }

    /**
     * @return get icon of piece
     */
    public String getIcon() {
        return icon;
    }

    /**
     * set icon
     * @param icon = each piece has own icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * each piece has name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name = name of piece
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return get move Count of each piece
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * @param moveCount = how many times piece moved
     */
    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    /**
     * @return get possible moves of piece
     */
    public ArrayList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    /**
     * set all possible moves
     * @param curr = current square where is now piece
     * @param board = board with all squares
     * @param currPlayer = current player
     */
    public abstract void setPossibleMoves(Square curr, Board board, Player currPlayer);

    //I created a general method to check all of the linear moves by having one method take an x and y coordinates and
    //then just go along that line. This method is used for Bishop, Queen and Rook.
    protected ArrayList<Move> oneDirection(int rowDir, int colDir, Square currSquare, Board board, Player currPlayer) {
        ArrayList<Move> oneDirection = new ArrayList<Move>();
        int row = currSquare.getROW();
        int col = currSquare.getCOL();

        int i = 1;
        while (i < 8) {
            int dRow = row + (i * rowDir);
            int dCol = col + (i * colDir);

            if (dRow >= 0 && dRow < 8 && dCol < 8 && dCol >= 0) {
                Square destSquare = board.getSquare(row + (i * rowDir), col + (i * colDir));
                Piece destPiece = destSquare.getPiece();

                if (destPiece == null) {
                    oneDirection.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                } else if (destPiece.getColor() != getColor()) {
                    oneDirection.add(new Move(currPlayer, currSquare, destSquare, board, MoveType.NORMAL));
                    break;
                    // break because you cannot go through a piece of different colour but you can still land on it
                } else {
                    break;
                    // cannot go to a move with a piece the same colour
                }
            } else {
                break;
            }
            i++;
        }

        return oneDirection;
    }

    /**
     * @param possibleMoves = set all possible moves of piece
     */
    public void setPossibleMoves(ArrayList<Move> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    /**
     * method to print Possible Moves of piece
     */
    public void printPossibleMoves() {
        for (Move move : getLegalMoves()) {
            System.out.println(move.toString());
        }
    }

    /**
     * It return all legal moves of piece
     * @return legal moves
     */
    public ArrayList<Move> getLegalMoves() {
        return legalMoves;
    }

    /**
     * It set all legal moves of player/piece
     * @return legal moves
     */
    public void setLegalMoves() {
        ArrayList<Move> legalMoves = new ArrayList<Move>();

        for (Move possMove : getPossibleMoves()) {
            if (possMove.isLegal()) {
                legalMoves.add(possMove);
            }
        }

        this.legalMoves = legalMoves;
    }
}