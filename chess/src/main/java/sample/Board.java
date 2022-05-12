package sample;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import static sample.Main.*;

/**
 * in Board class I create whole board and there are also moves to check if king is in check.
 */
public class Board {
    private Square[][] board;
    private Piece lastPieceToMove;
    private final double squareLen = 80.0;
    private final double pictureSize = 80.0;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
    private static final Logger LOG = Logger.getLogger(Board.class.getName());
    Image blackBishop = new Image("file:chess/src/main/resources/black_Bishop.png", pictureSize, pictureSize, false, false);
    Image blackKing = new Image("file:chess/src/main/resources/black_King.png", pictureSize, pictureSize, false, false);
    Image blackKnight = new Image("file:chess/src/main/resources/black_Knight.png", pictureSize, pictureSize, false, false);
    Image blackPawn = new Image("file:chess/src/main/resources/black_Pawn.png", pictureSize, pictureSize, false, false);
    Image blackQueen = new Image("file:chess/src/main/resources/black_Queen.png", pictureSize, pictureSize, false, false);
    Image blackRook = new Image("file:chess/src/main/resources/black_Rook.png", pictureSize, pictureSize, false, false);

    Image whiteBishop = new Image("file:chess/src/main/resources/white_Bishop.png", pictureSize, pictureSize, false, false);
    Image whiteKing = new Image("file:chess/src/main/resources/white_King.png", pictureSize, pictureSize, false, false);
    Image whiteKnight = new Image("file:chess/src/main/resources/white_Knight.png", pictureSize, pictureSize, false, false);
    Image whitePawn = new Image("file:chess/src/main/resources/white_Pawn.png", pictureSize, pictureSize, false, false);
    Image whiteQueen = new Image("file:chess/src/main/resources/white_Queen.png", pictureSize, pictureSize, false, false);
    Image whiteRook = new Image("file:chess/src/main/resources/white_Rook.png", pictureSize, pictureSize, false, false);

    Image blank = new Image("file:chess/src/main/resources/blank.png", pictureSize, pictureSize, false, false);


    private GridPane squares;
    private GridPane pieceSprites;
    private GridPane buttons;
    private StackPane graphicBoard;

    private ArrayList<Square> clickQueue;
    private Piece inputtedPromoPiece;

    private Label endGameMessage;

    private HBox promoButtons;
    private Button ResetGameButton;
    private Button BackToMenuButton;
    private Button QuitButton;
    private Collection<Piece> whitePieces;
    private Collection<Piece> blackPieces;
    private ArrayList<Square> allPieces = new ArrayList<>();

    /**
     * this method I use when I want to create new Board and buttons on right side.
     */
    public Board() {
        setNewBoard();
        clickQueue = new ArrayList<Square>();

        buttons = new GridPane();
        setButtons();

        squares = new GridPane();
        setSquares();
        BackToMenuButton = new Button();
        QuitButton = new Button();
        pieceSprites = new GridPane();
        ResetGameButton = new Button();
        endGameMessage = new Label();
        setPieceSprites();

        promoButtons = new HBox();
        setRoot();
    }

    /**
     * This method I use to return clickQueue. Click Queue is list of max 2 squares. First is start square, 2nd is end square.
     * @return clickQueue
     */
    public ArrayList<Square> getClickQueue() {
        return clickQueue;
    }


    /**
     * this method creates new clickQueue (reset)
     */
    public void resetClickQueue() {
        clickQueue = new ArrayList<Square>();
    }


    /**
     * it updates images on squares on board
     */
    public void updatePieceSprites() {
        Platform.runLater(() -> setPieceSprites());
    }

    /**
     * I use it to refresh squares.
     */
    public void refreshSquares() {
        Platform.runLater(() -> {
                setSquares();
        });
    }

    /**
     * This method return graphic Board. Graphic board is StackPane() which contains squares, pieceSprites and buttons
     * @return graphicBoard
     */
    public StackPane getGraphicBoard() {
        return graphicBoard;
    }

    private void setRoot() {
        graphicBoard = new StackPane();
        graphicBoard.getChildren().add(squares);
        graphicBoard.getChildren().add(pieceSprites);
        graphicBoard.getChildren().add(buttons);
    }


    /**
     * This method creates list of all active pieces on actual Board.
     * @return allPieces
     */
    public ArrayList<Square> printPiece(){
        allPieces = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j].getPiece()!=null){
                    allPieces.add(board[i][j]);
                }
            }
        }
        return allPieces;
    }

    /**
     * this method creates list of all active black pieces on board
     * @return blackPieces
     */
    public List<Piece> blackPieces(){
        printPiece();
        blackPieces = new ArrayList<Piece>();
        for(Square square:allPieces){
            if(square.getPiece().getColor() == (ChessColor.BLACK)){
                blackPieces.add(square.getPiece());
            }
        }
        return (List<Piece>) blackPieces;
    }

    /**
     * this method creates list of all active white pieces on board
     * @return  whitePieces
     */
    public List<Piece> whitePieces(){
        printPiece();
        whitePieces = new ArrayList<Piece>();
        for(Square square:allPieces){
            if(square.getPiece().getColor() == (ChessColor.WHITE)){
                whitePieces.add(square.getPiece());
            }
        }
        return (List<Piece>) whitePieces;
    }

    //setSquares, only visual, here I define colors and also hilight selected square
    private void setSquares() {
        squares.getChildren().clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle square = new Rectangle();

                square.setHeight(squareLen);
                square.setWidth(squareLen);

                square.setStyle((i % 2 != j % 2) ? "-fx-fill: grey;" : "-fx-fill: white;");
                hilightSelectedSquare(getSquare(7-i,j), square);
                squares.add(square, j, i);
            }
        }
    }


    /**
     * This method hilight selected square (orange color).
     * @param boardSquare = single square of board
     * @param square = rectangle, only visual usage
     */
    public void hilightSelectedSquare(Square boardSquare, Rectangle square) {
        if (getClickQueue().contains(boardSquare)) {
            square.setStyle("-fx-fill: orange;");
        }
    }


    //set to every piece image
    private void setPieceSprites() {
        pieceSprites.getChildren().clear();

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                ImageView v = new ImageView(rightImageForPiece(getSquare(i, j).getPiece()));
                pieceSprites.add(v, j, 7 - i);
            }
        }
    }

    //define images of piece instances
    private Image rightImageForPiece(Piece piece) {
        if (piece == null) {
            return blank;
        } else if (piece instanceof Bishop) {
            return (piece.getColor() == ChessColor.WHITE) ? whiteBishop : blackBishop;
        } else if (piece instanceof King) {
            return (piece.getColor() == ChessColor.WHITE) ? whiteKing : blackKing;
        } else if (piece instanceof Knight) {
            return (piece.getColor() == ChessColor.WHITE) ? whiteKnight : blackKnight;
        } else if (piece instanceof Pawn) {
            return (piece.getColor() == ChessColor.WHITE) ? whitePawn : blackPawn;
        } else if (piece instanceof Queen) {
            return (piece.getColor() == ChessColor.WHITE) ? whiteQueen : blackQueen;
        } else {
            // ONLY OTHER PIECE TYPE IS ROOK
            return (piece.getColor() == ChessColor.WHITE) ? whiteRook : blackRook;
        }
    }

    //set buttons, on click it adds square to click Queue
    private void setButtons() {
        buttons.getChildren().clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Button button = new Button();
                button.setMaxSize(squareLen, squareLen);
                button.setMinSize(squareLen, squareLen);

                final int FINAL_ROW = 7 - i;
                final int FINAL_COL = j;

                button.setOnAction(e -> {
                    clickQueue.add(getSquare(FINAL_ROW, FINAL_COL));
                });

                button.setStyle("-fx-background-radius: 0; -fx-background-color: transparent;");
                buttons.add(button, j, i);
            }
        }
    }

    /**
     * It is final message when game is over.
     * @param s - I define this message in class Game. It can be message 1) Black win, 2) White win, 3) DRAW
     */
    public void setEndGameMessage(String s) {
        Platform.runLater(() -> {

            endGameMessage.setText(s);
            endGameMessage.setPadding(new Insets(15.0, 15.0, 15.0, 15.0));
            endGameMessage.setStyle(
                    "-fx-background-color: #dedede; -fx-background-radius: 2; -fx-border-width: 1; -fx-border-color: #bfbfbf; -fx-border-radius: 3;");
            endGameMessage.setFont(Font.font("Verdana", 16));

            graphicBoard.getChildren().add(endGameMessage);
            StackPane.setAlignment(endGameMessage, Pos.CENTER);
        });
    }


    /**
     * this method is to show on right side 3 buttons: RESET GAME, BACK TO MENU, QUIT GAME
     */
    public void setEarlyGameMessage() {
        Platform.runLater(() -> {

            ResetGameButton.setText("RESET GAME");
            ResetGameButton.setTranslateX(500);
            ResetGameButton.setTranslateY(-75);
            ResetGameButton.setPrefWidth(300);
            ResetGameButton.setPrefHeight(100);
            ResetGameButton.setStyle("-fx-background-color: \n" +
                    "linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                    "linear-gradient(#020b02, #3a3a3a),\n" +
                    "linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),\n" +
                    "linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);\n" +
                    "-fx-background-insets: 0,1,4,5;\n" +
                    "-fx-background-radius: 9,8,5,4;\n" +
                    "-fx-padding: 15 30 15 30;\n" +
                    "-fx-font-family: \"Helvetica\";\n" +
                    "-fx-font-size: 18px;\n" +
                    "-fx-font-weight: bold;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
            ResetGameButton.setAlignment(Pos.BASELINE_CENTER);
            ResetGameButton.setOnAction(e ->{
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": RESETING GAME!");
                Player whitePlayer = new HumanPlayer(ChessColor.WHITE);
                Player blackPlayer = new HumanPlayer(ChessColor.BLACK);
                Game game = new Game(whitePlayer, blackPlayer);
                HBox hbox = new HBox();
                hbox.getChildren().add(game.getBoard().getGraphicBoard());
                primaryStagee.setCenter(hbox);
                Thread gameThread = new Thread(game);
                gameThread.start();
            } );

            BackToMenuButton.setText("BACK TO MENU");
            BackToMenuButton.setPrefWidth(300);
            BackToMenuButton.setPrefHeight(100);
            BackToMenuButton.setTranslateX(500);
            BackToMenuButton.setTranslateY(50);
            BackToMenuButton.setStyle("-fx-background-color: \n" +
                    "linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                    "linear-gradient(#020b02, #3a3a3a),\n" +
                    "linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),\n" +
                    "linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);\n" +
                    "-fx-background-insets: 0,1,4,5;\n" +
                    "-fx-background-radius: 9,8,5,4;\n" +
                    "-fx-padding: 15 30 15 30;\n" +
                    "-fx-font-family: \"Helvetica\";\n" +
                    "-fx-font-size: 18px;\n" +
                    "-fx-font-weight: bold;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
            BackToMenuButton.setAlignment(Pos.BASELINE_CENTER);
            BackToMenuButton.setOnAction(e -> {
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": BACK TO MENU!");
                primaryStagee.setCenter(createContent());
            });


            QuitButton.setText("QUIT GAME");
            QuitButton.setPrefWidth(300);
            QuitButton.setPrefHeight(100);
            QuitButton.setTranslateX(500);
            QuitButton.setTranslateY(175);
            QuitButton.setStyle("-fx-background-color: \n" +
                    "linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                    "linear-gradient(#020b02, #3a3a3a),\n" +
                    "linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),\n" +
                    "linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);\n" +
                    "-fx-background-insets: 0,1,4,5;\n" +
                    "-fx-background-radius: 9,8,5,4;\n" +
                    "-fx-padding: 15 30 15 30;\n" +
                    "-fx-font-family: \"Helvetica\";\n" +
                    "-fx-font-size: 18px;\n" +
                    "-fx-font-weight: bold;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");

            QuitButton.setAlignment(Pos.BASELINE_CENTER);
            QuitButton.setOnAction(e -> System.exit(0));


            graphicBoard.getChildren().add(ResetGameButton);
            graphicBoard.getChildren().add(BackToMenuButton);
            graphicBoard.getChildren().add(QuitButton);
        });
    }


    /**
     * If pawn can promote, it shows buttons on board.
     * @param color - Black or White player
     */
    public void setPromoButtons(ChessColor color) {
        Platform.runLater(() -> {
            graphicBoard.getChildren().add(promoButtons);
            Button queen = new Button();
            Button rook = new Button();
            Button knight = new Button();
            Button bishop = new Button();

            if (color == ChessColor.WHITE) {
                queen.setGraphic(new ImageView(whiteQueen));
                queen.setOnAction(e -> inputtedPromoPiece = new Queen(color));
                rook.setGraphic(new ImageView(whiteRook));
                rook.setOnAction(e -> inputtedPromoPiece = new Rook(color));
                knight.setGraphic(new ImageView(whiteKnight));
                knight.setOnAction(e -> inputtedPromoPiece = new Knight(color));
                bishop.setGraphic(new ImageView(whiteBishop));
                bishop.setOnAction(e -> inputtedPromoPiece = new Bishop(color));

            } else {
                queen.setGraphic(new ImageView(blackQueen));
                queen.setOnAction(e -> inputtedPromoPiece = new Queen(color));
                rook.setGraphic(new ImageView(blackRook));
                rook.setOnAction(e -> inputtedPromoPiece = new Rook(color));
                knight.setGraphic(new ImageView(blackKnight));
                knight.setOnAction(e -> inputtedPromoPiece = new Knight(color));
                bishop.setGraphic(new ImageView(blackBishop));
                bishop.setOnAction(e -> inputtedPromoPiece = new Bishop(color));
            }

            promoButtons.getChildren().add(queen);
            promoButtons.getChildren().add(rook);
            promoButtons.getChildren().add(knight);
            promoButtons.getChildren().add(bishop);

            promoButtons.setAlignment(Pos.CENTER);
        });
    }

    /**
     * After pawn promo is selected, then promo buttons dissappear
     */
    public void clearPromoButtons() {
        setInputtedPromoPiece(null);;
        Platform.runLater(() -> {
            promoButtons.getChildren().clear();
            graphicBoard.getChildren().remove(promoButtons);
        });
    }

    /**
     * method to get piece we want to promo
     * @return inputtedPromoPece
     */
    public Piece getInputtedPromoPiece() {
        return inputtedPromoPiece;
    }

    /**
     * method to set piece which we want to promo
     * @param inputtedPromoPiece
     */
    public void setInputtedPromoPiece(Piece inputtedPromoPiece) {
        this.inputtedPromoPiece = inputtedPromoPiece;
    }

    //all backend from here down

    /**
     * get last piece that moved
     * @return lastPieceToMove
     */
    public Piece getLastPieceToMove() {
        return lastPieceToMove;
    }

    /**
     * set last piece to move
     * @param lastPieceToMove
     */
    public void setLastPieceToMove(Piece lastPieceToMove) {
        this.lastPieceToMove = lastPieceToMove;
    }

    /**
     * create new Board
     */
    public void setNewBoard() {
        LOG.log(Level.INFO, dtf.format(LocalDateTime.now()) + ": initializing board...");
        board = new Square[8][8];

        board[0][0] = new Square(0, 0, new Rook(ChessColor.WHITE));
        //piece.addPieceCountList(Rook(chessColor.WHITE));
        board[0][1] = new Square(0, 1, new Knight(ChessColor.WHITE));
        board[0][2] = new Square(0, 2, new Bishop(ChessColor.WHITE));
        board[0][3] = new Square(0, 3, new Queen(ChessColor.WHITE));
        board[0][4] = new Square(0, 4, new King(ChessColor.WHITE));
        board[0][5] = new Square(0, 5, new Bishop(ChessColor.WHITE));
        board[0][6] = new Square(0, 6, new Knight(ChessColor.WHITE));
        board[0][7] = new Square(0, 7, new Rook(ChessColor.WHITE));
        board[1][0] = new Square(1, 0, new Pawn(ChessColor.WHITE));
        board[1][1] = new Square(1, 1, new Pawn(ChessColor.WHITE));
        board[1][2] = new Square(1, 2, new Pawn(ChessColor.WHITE));
        board[1][3] = new Square(1, 3, new Pawn(ChessColor.WHITE));
        board[1][4] = new Square(1, 4, new Pawn(ChessColor.WHITE));
        board[1][5] = new Square(1, 5, new Pawn(ChessColor.WHITE));
        board[1][6] = new Square(1, 6, new Pawn(ChessColor.WHITE));
        board[1][7] = new Square(1, 7, new Pawn(ChessColor.WHITE));

        board[7][0] = new Square(7, 0, new Rook(ChessColor.BLACK));
        board[7][1] = new Square(7, 1, new Knight(ChessColor.BLACK));
        board[7][2] = new Square(7, 2, new Bishop(ChessColor.BLACK));
        board[7][3] = new Square(7, 3, new Queen(ChessColor.BLACK));
        board[7][4] = new Square(7, 4, new King(ChessColor.BLACK));
        board[7][5] = new Square(7, 5, new Bishop(ChessColor.BLACK));
        board[7][6] = new Square(7, 6, new Knight(ChessColor.BLACK));
        board[7][7] = new Square(7, 7, new Rook(ChessColor.BLACK));


        board[6][0] = new Square(6, 0, new Pawn(ChessColor.BLACK));
        board[6][1] = new Square(6, 1, new Pawn(ChessColor.BLACK));
        board[6][2] = new Square(6, 2, new Pawn(ChessColor.BLACK));
        board[6][3] = new Square(6, 3, new Pawn(ChessColor.BLACK));
        board[6][4] = new Square(6, 4, new Pawn(ChessColor.BLACK));
        board[6][5] = new Square(6, 5, new Pawn(ChessColor.BLACK));
        board[6][6] = new Square(6, 6, new Pawn(ChessColor.BLACK));
        board[6][7] = new Square(6, 7, new Pawn(ChessColor.BLACK));

        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(i, j, null);
            }
        }
    }

    /**
     * method to print board in terminal
     */
    public void printBoard() {
        String out = "";
        for (int i = 7; i >= 0; i--) {
            String line = "";
            for (int j = 0; j < 8; j++) {
                if (j == 0) {
                    line += "" + (i + 1) + " ";
                }
                if (board[i][j].getPiece() == null) {
                    line += "- ";
                } else {
                    String s = String.valueOf(board[i][j].getPiece().getIcon());
                    if(s==""){
                        s="P";
                    }
                    if (board[i][j].getPiece().getColor() == ChessColor.BLACK) {
                        s = s.toLowerCase();
                    }
                    line += s + " ";
                }
            }
            out += line + "\n";
        }
        out += "  a b c d e f g h\n";
        System.out.println(out);
    }


    /**
     * method to getSquare on board
     * @param row -A/B/C/D/E/F/G/H
     * @param col -1/2/3/4/5/6/7/8
     * @return board[row][col]
     */
    public Square getSquare(int row, int col) {
        return board[row][col];
    }

    /**
     * This method find square where King is.
     * @param color - BLACK/WHITE
     * @return square of king
     */
    public Square findKingSquare(ChessColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square currSquare = getSquare(i, j);
                Piece currPiece = currSquare.getPiece();
                //currSquare.
                if (currPiece != null && currPiece instanceof King && currPiece.getColor() == color) {
                    return currSquare;
                }
            }
        }

        return null;
    }


    /**
     * function to check if king can be attacked when it leaves this square and to see on which squares can king go
     * @param square
     * @param friendlyColor
     * @return True/False
     */
    public boolean squareCanBeAttacked(Square square, ChessColor friendlyColor) {
        // DONT DO PRE PROCESSING BECAUSE SHORT CIRCUIT OR IS MORE EFFICIENT

        return canBeAttackedLinear(1, 0, square, friendlyColor) || canBeAttackedLinear(-1, 0, square, friendlyColor)
                || canBeAttackedLinear(0, 1, square, friendlyColor) || canBeAttackedLinear(0, -1, square, friendlyColor)
                || canBeAttackedLinear(1, 1, square, friendlyColor) || canBeAttackedLinear(-1, 1, square, friendlyColor)
                || canBeAttackedLinear(1, -1, square, friendlyColor)
                || canBeAttackedLinear(-1, -1, square, friendlyColor) || canBeAttackedKnight(square, friendlyColor);
    }


    /**
     * check if king is in check, it can be true or false
     * @param color = color of current Player
     * @return True/False
     */
    public boolean kingIsInCheck(ChessColor color) {
        return squareCanBeAttacked(findKingSquare(color), color);

    }

    //check if king can be attacked linear
    private boolean canBeAttackedLinear(int rowDir, int colDir, Square square, ChessColor friendlyColor) {
        int row = square.getROW();
        int col = square.getCOL();

        int i = 1;
        while (i < 8) {
            int dRow = row + (i * rowDir);
            int dCol = col + (i * colDir);

            if (dRow >= 0 && dRow < 8 && dCol < 8 && dCol >= 0) {
                Square destSquare = getSquare(dRow, dCol);
                Piece destPiece = destSquare.getPiece();

                if (destPiece != null) {
                    if (destPiece.getColor() != friendlyColor) {
                        if (rowDir == 0 || colDir == 0) {
                            return straightLineThreats(destPiece, i);
                        } else {
                            return diagonalThreats(destPiece, i, rowDir, friendlyColor);
                        }
                    } else {
                        break;
                    }
                }

                // dont do anything if the piece is null
                // just keep checking along that line
            } else {
                break;
            }
            i++;
        }
        return false;
    }

    // find if there are straight line threats
    private boolean straightLineThreats(Piece destPiece, int i) {
        if (i == 1) {
            return destPiece instanceof Rook || destPiece instanceof Queen || destPiece instanceof King;
        }

        return destPiece instanceof Rook || destPiece instanceof Queen;
    }

    // find if there are diagonal threats
    private boolean diagonalThreats(Piece destPiece, int i, int rowDir, ChessColor friendlyColor) {
        // WE ALREADY KNOW THAT THE DEST PIECE IS OF THE OPPOSITE COLOR
        if (i == 1) {
            if (friendlyColor == ChessColor.WHITE && rowDir == 1 || friendlyColor == ChessColor.BLACK && rowDir == -1) {
                return destPiece instanceof Bishop || destPiece instanceof Queen || destPiece instanceof King
                        || destPiece instanceof Pawn;
            } else {
                return destPiece instanceof Bishop || destPiece instanceof Queen || destPiece instanceof King;
            }
        }

        return destPiece instanceof Bishop || destPiece instanceof Queen;
    }

    //To see if can be attacked by knight
    private boolean canBeAttackedKnight(Square square, ChessColor color) {
        int row = square.getROW();
        int col = square.getCOL();
        // not the basketball player
        int[] dRows = new int[] { 1, -1, 1, -1, 2, -2, 2, -2 };
        int[] dCols = new int[] { 2, 2, -2, -2, 1, 1, -1, -1 };

        for (int i = 0; i < 8; i++) {
            int dRow = row + dRows[i];
            int dCol = col + dCols[i];

            if (dRow < 8 && dRow >= 0 && dCol < 8 && dCol >= 0) {
                Square destSquare = getSquare(dRow, dCol);
                Piece destPiece = destSquare.getPiece();

                if (destPiece != null && destPiece.getColor() != color && destPiece instanceof Knight) {
                    return true;
                }
            }
        }

        return false;
    }
}