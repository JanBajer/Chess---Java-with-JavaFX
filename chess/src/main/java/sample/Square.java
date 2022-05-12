package sample;

/**
 * in Square class I define squares that are used in Board
 */
public class Square {
    private Piece piece;
    private final int ROW;
    private final int COL;

    /**
     * @param ROW = row - 1/2/3/4/5/6/7/8
     * @param COL = col - A/B/C/D/E/F/G/H
     * @param piece = piece(Bishop,Queen,King,Queen,Rook,Pawn)
     */
    Square(int ROW, int COL, Piece piece) {
        this.ROW = ROW;
        this.COL = COL;
        setPiece(piece);

    }

    /**
     * GET
     * @return get piece(Bishop,Queen,King,Queen,Rook,Pawn)
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * SET
     * @param piece = piece(Bishop,Queen,King,Queen,Rook,Pawn)
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * @return get ROW
     */
    public int getROW() {
        return ROW;
    }

    /**
     * @return GET COL
     */
    public int getCOL() {
        return COL;
    }

    /**
     * @return changes Numbers TO STRING
     */
    public String toString() {
        String alph = "ABCDEFGH";

        String startCol = String.valueOf(alph.charAt(getCOL()));
        int startRow = getROW() + 1;

        return startCol + startRow;
    }

    /**
     * I compare 2 squares
     * @param otherSquare = different square and start square
     * @return true/false
     */
    public boolean equalsSquare(Square otherSquare) {
        int r1 = getROW();
        int c1 = getCOL();

        int r2 = otherSquare.getROW();
        int c2 = otherSquare.getCOL();

        return r1 == r2 && c1 == c2;
    }
}