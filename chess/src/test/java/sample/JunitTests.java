package sample;


import javafx.embed.swing.JFXPanel;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import sample.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class JunitTests {
    JFXPanel panel = new JFXPanel();
    Board board = new Board();
    Player whitePlayer = new HumanPlayer(ChessColor.WHITE);
    Player blackPlayer = new HumanPlayer(ChessColor.BLACK);
    Game game = new Game(whitePlayer, blackPlayer);
    Thread gameThread = new Thread(game);

    @Test
    public void TEST1_AllPiecesOnBoard() {
        assertEquals(32,board.printPiece().size(),"Number of all pieces on new board");
    }

    @Test
    public void TEST2_AllBlackPiecesOnBoard() {
        assertEquals(16,board.blackPieces().size(),"Number of all black pieces on new board");
    }

    @Test
    public void TEST3_AllWhitePiecesOnBoard() {
        assertEquals(16,board.whitePieces().size(),"Number of all white pieces on new board");
    }

    @Test
    @DisplayName("Testing all possible moves first round for pieces on D2,B2,A1,A7,G8")
    public void TEST4_FirstRoundPiecePossibleMoves() {
        gameThread.start();
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);
        assertEquals("[d3, d4]",board.getSquare(1,3).getPiece().getPossibleMoves().toString(),"Get squares where can white Pawn first round on D2 move");
        assertEquals("[c3, a3]",board.getSquare(0,1).getPiece().getPossibleMoves().toString(),"Get squares where can white Knight first round on B2 move");
        assertEquals("[]", board.getSquare(0,0).getPiece().getPossibleMoves().toString(),"Get squares where can white Rook first round on A1 move");
        assertEquals(null, board.getSquare(6,0).getPiece().getPossibleMoves(),"Get squares where can black Pawn first round on A7 move");
        assertEquals(null, board.getSquare(7,6).getPiece().getPossibleMoves(),"Get squares where can black Knight first round on G8 move");
    }
    @Test
    @DisplayName("Try to find square of white and black King. Testing method FindKingSquare() ")
    public void TEST5_FindKingSquare() {
        gameThread.start();
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        assertEquals("E1", board.findKingSquare(ChessColor.WHITE).toString(),"Find white King Square(E1)");
        assertEquals("E8", board.findKingSquare(ChessColor.BLACK).toString(),"Find white King Square(E8)");

        //Here I add new white King on E5 and remove king on E1.
        board.getSquare(4,4).setPiece(new King(ChessColor.WHITE));
        board.getSquare(0,4).setPiece(null);
        assertEquals("E5", board.findKingSquare(ChessColor.WHITE).toString(),"Find white new King Square(E5)");

        //Here I add new black King on F6 and remove king on E1.
        board.getSquare(5,5).setPiece(new King(ChessColor.BLACK));
        board.getSquare(7,4).setPiece(null);
        assertEquals("F6", board.findKingSquare(ChessColor.BLACK).toString(),"Find white new King Square(F6)");
    }

    @Test
    @DisplayName("Testing method kingIsInCheck ")
    public void TEST6_isKingInCheck() {
        gameThread.start();
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //Check if first round is white or black king in check.
        assertEquals(false, board.kingIsInCheck(ChessColor.WHITE),"Test if is white king first round in Check");
        assertEquals(false, board.kingIsInCheck(ChessColor.BLACK),"Test if is white king first round in Check");

        //Here I put a white King on position E5.
        board.getSquare(4,4).setPiece(new King(ChessColor.WHITE));
        board.getSquare(0,4).setPiece(null);

        //Here I put a black Queen on position A5.
        board.getSquare(4,0).setPiece(new Queen(ChessColor.BLACK));
        board.getSquare(7,3).setPiece(null);

        //I changed only start positions of white King(E5) and Black Queen(A5).
        //Other pieces are on place where they are normally first round.
        assertEquals(true, board.kingIsInCheck(ChessColor.WHITE),"Test if King is in check");

        board.setNewBoard();
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);
    }

    @Test
    @DisplayName("Testing move enPassant. We have white pawn on B5 and last piece that moved(move count of this piece=1) is black pawn on C5")
    public void TEST7_enPassant() {
        gameThread.start();

        //PREPARE, I put white pawn on B5 and black pawn on C5. Last piece that moved is black pawn on C5 and his move count = 1.
        board.getSquare(4,1).setPiece(new Pawn(ChessColor.WHITE));
        board.getSquare(4,1).getPiece().setMoveCount(2);
        board.getSquare(1,1).setPiece(null);
        board.getSquare(4,2).setPiece(new Pawn(ChessColor.BLACK));
        board.getSquare(4,2).getPiece().setMoveCount(1);
        board.setLastPieceToMove(board.getSquare(4,2).getPiece());
        board.getSquare(6,2).setPiece(null);

        // This print board so we can see actual positions.
        board.printBoard();

        //Set curr player and all legal moves
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //TEST to see squares where can white pawn move
        assertEquals("[b6, c6]", board.getSquare(4,1).getPiece().getLegalMoves().toString(),"Check if pawn can go forward and do enPassant");
        //counter of white moves
        assertEquals(2, board.getSquare(4,1).getPiece().getLegalMoves().size(),"Check if pawn has 2 possible moves(go forward and enPassant)");
    }

    @Test
    @DisplayName("Testing move enPassant. We have white pawn on B5 and there is black pawn(move count=1) on C5 but it is not last piece that moved.")
    public void TEST8_enPassant2() {
        gameThread.start();

        //PREPARE, I put white pawn on B5 and black pawn on C5 but BLACK PAWN is not last piece that moved!
        board.getSquare(4,1).setPiece(new Pawn(ChessColor.WHITE));
        board.getSquare(4,1).getPiece().setMoveCount(2);
        board.getSquare(1,1).setPiece(null);
        board.getSquare(4,2).setPiece(new Pawn(ChessColor.BLACK));
        board.getSquare(6,2).setPiece(null);

        // This print board so we can see actual positions.
        board.printBoard();

        //Set curr player and all legal moves
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //TEST to see squares where can white pawn move
        assertEquals("[b6]", board.getSquare(4,1).getPiece().getLegalMoves().toString(),"Check if pawn can go forward and do enPassant");
        //counter of white moves
        assertEquals(1, board.getSquare(4,1).getPiece().getLegalMoves().size(),"Check if pawn has 2 possible moves(go forward and enPassant)");
    }

    @Test
    @DisplayName("I put white pawn on B5 and black pawn on C5, BLACK PAWN is last piece that moved but it was his 2nd move this game")
    public void TEST9_enPassant3() {
        gameThread.start();

        //PREPARE, I put white pawn on B5 and black pawn on C5, BLACK PAWN is last piece that moved but it was his 2nd move this game!
        board.getSquare(4,1).setPiece(new Pawn(ChessColor.WHITE));
        board.getSquare(4,1).getPiece().setMoveCount(2);
        board.getSquare(1,1).setPiece(null);
        board.getSquare(4,2).setPiece(new Pawn(ChessColor.BLACK));
        board.getSquare(4,2).getPiece().setMoveCount(2);
        board.setLastPieceToMove(board.getSquare(4,2).getPiece());
        board.getSquare(6,2).setPiece(null);

        // This print board so we can see actual positions.
        board.printBoard();

        //Set curr player and all legal moves
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //TEST to see squares where can white pawn move
        assertEquals("[b6]", board.getSquare(4,1).getPiece().getLegalMoves().toString(),"Check if pawn can go forward and do enPassant");
        //counter of white moves
        assertEquals(1, board.getSquare(4,1).getPiece().getLegalMoves().size(),"Check if pawn has 2 possible moves(go forward and enPassant)");
    }

    @Test
    @DisplayName("Test how many moves can do player under check. ")
    public void TEST10_PossibleMovesOfPlayerUnderCheck() {
        gameThread.start();

        //PREPARE, we remove E2 pawn and set Queen on E6, so there should be CHECK
        board.getSquare(1,4).setPiece(null);
        board.getSquare(5,4).setPiece(new Queen(ChessColor.BLACK));

        //Set curr player and all legal moves
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //TEST if KING is in check
        assertEquals(true, board.kingIsInCheck(ChessColor.WHITE),"Find white new King Square(E5)");
        // This print board so we can see actual positions.
        board.printBoard();

        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);
        //It should be 3, because it can be only blocked by Queen(D1->E2), Bishop(F1->E2) and Knight(G1->E2)
        assertEquals(3, game.getCurrPlayer().getAllLegalMoves().size(),"How many moves can do current player under check");
    }

    @Test
    @DisplayName("Test possible moves of ROOK when there is new board but the rook spawn in middle E5")
    public void TEST11_TestPossibleMovesOfRook() {
        gameThread.start();

        //PREPARE, we set ROOK on E5
        board.getSquare(4,4).setPiece(new Rook(ChessColor.WHITE));
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //Counter of moves the rook can do
        assertEquals(11,board.getSquare(4,4).getPiece().getLegalMoves().size(),"Find how many possible moves can the rook do");

        //First it checks possible moves to top, then to bot, then right side and then left side
        assertEquals("[e6, e7, e4, e3, f5, g5, h5, d5, c5, b5, a5]",
                board.getSquare(4,4).getPiece().getLegalMoves().toString(),"Find all possible moves of the rook");
    }
    @Test
    @DisplayName("Test possible moves of Queen when there is new board but the Queen spawn in middle E5")
    public void TEST12_TestPossibleMovesOfQueen() {
        gameThread.start();

        //PREPARE, we set ROOK on E5
        board.getSquare(4,4).setPiece(new Queen(ChessColor.WHITE));
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //Counter of moves the Queen can do
        assertEquals(19,board.getSquare(4,4).getPiece().getLegalMoves().size(),"Find how many possible moves can the Queen do");

        //First it checks possible moves top right, then to bot right, then top left, bot left, top, bot, right, left.
        assertEquals("[f6, g7, f4, g3, d6, c7, d4, c3, e6, e7, e4, e3, f5, g5, h5, d5, c5, b5, a5]",
                board.getSquare(4,4).getPiece().getLegalMoves().toString(),"Find all possible moves of the Queen");
    }
    @Test
    @DisplayName("Test possible moves of King when there is new board but the King spawn in middle E5")
    public void TEST13_TestPossibleMovesOfKing() {
        gameThread.start();

        //PREPARE, we set ROOK on E5
        board.getSquare(4,4).setPiece(new King(ChessColor.WHITE));
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //Counter of moves the King can do
        assertEquals(8,
                board.getSquare(4,4).getPiece().getLegalMoves().size(),"Find how many possible moves can the King do");

        //Possible moves of King
        assertEquals("[e6, e4, f5, d5, f6, f4, d6, d4]",
                board.getSquare(4,4).getPiece().getLegalMoves().toString(),"Find all possible moves of the King");
    }

    @Test
    @DisplayName("Test possible moves of Bishop when there is new board but the Bishop spawn in middle E5")
    public void TEST14_TestPossibleMovesOfBishop() {
        gameThread.start();

        //PREPARE, we set ROOK on E5
        board.getSquare(4,4).setPiece(new Bishop(ChessColor.WHITE));
        game.setCurrPlayer(whitePlayer);
        game.getCurrPlayer().setAllLegalMoves(board);

        //Counter of moves the Bishop can do
        assertEquals(8,
                board.getSquare(4,4).getPiece().getLegalMoves().size(),"Find how many possible moves can the King do");

        //Possible moves of Bishop
        assertEquals("[f6, g7, f4, g3, d6, c7, d4, c3]",
                board.getSquare(4,4).getPiece().getLegalMoves().toString(),"Find all possible moves of the King");
    }

}