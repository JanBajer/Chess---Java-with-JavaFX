package sample;

/**
 * class Move
 */
public class Move {
    private Player player;
    private Square startSquare;
    private Square endSquare;
    private Piece startPiece;
    private Piece endPiece;
    private Board board;
    private MoveType type;
    private Piece promotionPiece;

    /**
     * FOR WHEN THE COMPUTER CREATES THE LIST OF ITS LEGAL MOVES
     * THIS HAS FULL INFORMATION WHICH ALLOW YOU THE MOVE TO BE MADE
     * @param player = current player
     * @param startSquare = start square of piece
     * @param endSquare = end square of piece
     * @param board = whole board, all squares
     * @param type = move type
     */
    Move(Player player, Square startSquare, Square endSquare, Board board, MoveType type) {
        this.player = player;
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.startPiece = startSquare.getPiece();
        this.endPiece = endSquare.getPiece();
        this.board = board;
        this.type = type;
    }


    /**
     * This is used when there is move by pawn and is promotion
     * @param player = current player
     * @param startSquare = start square of piece
     * @param endSquare = end square of piece
     * @param board = whole board, all squares
     * @param type = move type
     * @param promotionPiece = promotion Piece, can be Queen,Bishop,Knight or Rook
     */
    Move(Player player, Square startSquare, Square endSquare, Board board, MoveType type, Piece promotionPiece) {
        this.player = player;
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.startPiece = startSquare.getPiece();
        this.endPiece = endSquare.getPiece();
        this.board = board;
        this.type = type;
        this.promotionPiece = promotionPiece;
    }


    /**
     *  FOR WHEN THE USER IS INPUTTING THE MOVE
     *  DONT NEED TO INPUT FULL INFORMATION BECAUSE THAT CAN BE FOUND OUT
     *  THROUGH THE START AND END SQUARES
     * @param player = current player
     * @param startSquare = start square of piece
     * @param endSquare = end square of piece
     */
    Move(Player player, Square startSquare, Square endSquare) {
        this.player = player;
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.startPiece = startSquare.getPiece();
        this.endPiece = endSquare.getPiece();
        this.type = null;
    }

    /**
     * movePiece, there are 4 types - Normal, Castle, Pawn, En Passant
     */
    public void movePiece() {
        switch (type) {
            case NORMAL:
                moveNormally();
                break;
            case CASTLE:
                castle();
                break;
            case PAWN_PROMOTION:
                promotePawn();
                break;
            case EN_PASSANT:
                enPassant();
                break;
        }
    }

    //normal move
    private void moveNormally() {
        startPiece.setMoveCount(startPiece.getMoveCount() + 1);
        endSquare.setPiece(startPiece);
        startSquare.setPiece(null);
    }

    //castle
    private void castle() {
        Square destSquare = getEndSquare();
        Board currBoard = getBoard();
        Move moveRook = null;

        if (destSquare.equalsSquare(currBoard.getSquare(0, 6))) {
            moveRook = new Move(getPlayer(), board.getSquare(0, 7), board.getSquare(0, 5), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(0, 2))) {
            moveRook = new Move(getPlayer(), board.getSquare(0, 0), board.getSquare(0, 3), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 6))) {
            moveRook = new Move(getPlayer(), board.getSquare(7, 7), board.getSquare(7, 5), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 2))) {
            moveRook = new Move(getPlayer(), board.getSquare(7, 0), board.getSquare(7, 3), getBoard(), MoveType.NORMAL);
        }

        // will never be null because all castline moves follow these rules.

        moveNormally();
        moveRook.moveNormally();
    }

    //promotePawn move
    private void promotePawn() {
        moveNormally();
        endSquare.setPiece(promotionPiece);
    }

    //en passant move
    private void enPassant() {
        moveNormally();

        int destRow = getEndSquare().getROW();
        int destCol = getEndSquare().getCOL();
        int behind = (getPlayer().getColor() == ChessColor.WHITE) ? -1 : 1;
        Square oppPawnSquare = board.getSquare(destRow + behind, destCol);

        oppPawnSquare.setPiece(null);
    }

    //method to undo move piece
    private void undoMovePiece() {
        switch (type) {
            case NORMAL:
                undoMoveNormally();
                break;
            case CASTLE:
                undoCastle();
                break;
            case PAWN_PROMOTION:
                undoPromotePawn();
                break;
            case EN_PASSANT:
                undoEnPassant();
                break;
        }
    }

    //all down to undo move
    private void undoMoveNormally() {
        startPiece.setMoveCount(startPiece.getMoveCount() - 1);
        endSquare.setPiece(endPiece);
        startSquare.setPiece(startPiece);
    }

    private void undoCastle() {
        Square destSquare = getEndSquare();
        Board currBoard = getBoard();
        Move moveRook = null;

        if (destSquare.equalsSquare(currBoard.getSquare(0, 6))) {
            moveRook = new Move(getPlayer(), board.getSquare(0, 7), board.getSquare(0, 5), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(0, 2))) {
            moveRook = new Move(getPlayer(), board.getSquare(0, 0), board.getSquare(0, 3), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 6))) {
            moveRook = new Move(getPlayer(), board.getSquare(7, 7), board.getSquare(7, 5), getBoard(), MoveType.NORMAL);
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 2))) {
            moveRook = new Move(getPlayer(), board.getSquare(7, 0), board.getSquare(7, 3), getBoard(), MoveType.NORMAL);
        }

        // will never be null because all castline moves follow these rules.
        undoMoveNormally();
        moveRook.undoMoveNormally();
    }

    private void undoPromotePawn() {
        undoMoveNormally();
        endSquare.setPiece(getEndPiece());
    }

    private void undoEnPassant() {
        undoMoveNormally();

        int destRow = getEndSquare().getROW();
        int destCol = getEndSquare().getCOL();

        ChessColor currColor = getPlayer().getColor();
        ChessColor oppColor = (currColor == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;

        int behind = (currColor == ChessColor.WHITE) ? -1 : 1;
        Square oppPawnSquare = board.getSquare(destRow + behind, destCol);

        oppPawnSquare.setPiece(new Pawn(oppColor));
        oppPawnSquare.getPiece().setMoveCount(1);
    }

    /**
     * method to get player
     * @return current player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * method to get start square
     * @return current square
     */

    public Square getStartSquare() {
        return startSquare;
    }

    /**
     * method to get end square
     * @return end square
     */
    public Square getEndSquare() {
        return endSquare;
    }

    /**
     * method to get start piece
     * @return piece which was on startsquare
     */
    public Piece getStartPiece() {
        return startPiece;
    }

    /**
     * method to get end piece
     * @return piece which was on endsquare
     */
    public Piece getEndPiece() {
        return endPiece;
    }

    /**
     * method to get board
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * method to get move type
     * @return move type
     */
    public MoveType getType() {
        return type;
    }


    /**
     * method to set promotion piece
     * @param promotionPiece = promotion piece
     */
    public void setPromotionPiece(Piece promotionPiece) {
        this.promotionPiece = promotionPiece;
    }


    /**
     * method which I use when saving file
     */
   public String toString() {
        return (getType() == MoveType.PAWN_PROMOTION) ? promoToString() : elseToString();
    }

    /**
     * method which I use when saving file
     */
    private String elseToString() {
        return endSquare.toString().toLowerCase();
    }

    /**
     * method which I use when saving file
     */
    private String promoToString() {
        return elseToString() + "=" + String.valueOf(promotionPiece.getIcon());
    }


    /**
     * Check if move is legal
     * @return True/False
     */
    public boolean isLegal() {
        switch (getType()) {
            case CASTLE:
                return castleIsLegal();
            default:
                return normalIsLegal();
        }
    }

    private boolean normalIsLegal() {
        return !putsKingIntoCheck();
    }


    private boolean castleIsLegal() {
        return !getBoard().squareCanBeAttacked(getEndSquare(), getStartPiece().getColor()) && !castlesThroughCheck();
    }

    private boolean castlesThroughCheck() {
        Square destSquare = getEndSquare();
        Board currBoard = getBoard();

        if (destSquare.equalsSquare(currBoard.getSquare(0, 6))) {
            return currBoard.squareCanBeAttacked(currBoard.getSquare(0, 5), getStartPiece().getColor());
        } else if (destSquare.equalsSquare(currBoard.getSquare(0, 2))) {
            return currBoard.squareCanBeAttacked(currBoard.getSquare(0, 3), getStartPiece().getColor());
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 6))) {
            return currBoard.squareCanBeAttacked(currBoard.getSquare(7, 5), getStartPiece().getColor());
        } else if (destSquare.equalsSquare(currBoard.getSquare(7, 2))) {
            return currBoard.squareCanBeAttacked(currBoard.getSquare(7, 3), getStartPiece().getColor());
        }

        // will never run below. all four castle destSquares are accounted for
        return false;
    }

    private boolean putsKingIntoCheck() {
        movePiece();

        // all I do is go from the king square, and then just check all around it for
        // pieces of the opposite color
        boolean kingIsInCheck = getBoard().kingIsInCheck(getPlayer().getColor());

        undoMovePiece();
        return kingIsInCheck;
    }

    /**
     * I compare 2 moves
     * @param otherMove  = unique move
     * @return true or false
     */
    public boolean equalsMove(Move otherMove) {
        // A COMBO OF A START AND END SQUARE ALWAYS PRODUCES A UNIQUE MOVE
        return getStartSquare().equalsSquare(otherMove.getStartSquare())
                && getEndSquare().equalsSquare(otherMove.getEndSquare());
    }
}