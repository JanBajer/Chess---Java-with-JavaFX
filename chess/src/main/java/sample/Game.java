package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * class Game
 */
public class Game implements Runnable {
    private int turn;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currPlayer;
    private Player lastPlayer;
    private ArrayList<String> allMovesInGame;
    private Board board;
    private GameState gameState;
    private int turnsWithoutCapture;
    private int turnsWithoutPawnMove;
    private Label timeWhite = new Label();
    private Label timeBlack = new Label();
    private Label timeWhite2 = new Label();
    private Label timeBlack2 = new Label();
    private static int counter = 0;
    public LocalTime timerWhite = LocalTime.of(0,10,0,0);
    public LocalTime timerBlack = LocalTime.of(0,10,0,0);
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_YYYY_HH_mm_ss");
    static LocalDateTime now = LocalDateTime.now();
    private Move realMove;
    private boolean IsGameOver = false;
    private static final Logger LOG = Logger.getLogger(Board.class.getName());

    /**
     * I use it when I want to create new game.
     * @param whitePlayer = it can be human or computer player
     * @param blackPlayer = it can be human or computer player
     */
    Game(Player whitePlayer, Player blackPlayer) {
        setTurn(1);
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        allMovesInGame = new ArrayList<String>();
        this.board = new Board();
    }


    /**
     * method to find which turn it is
     * @return turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * method to set turn
     * @param turn
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * method to get whitePlayer
     * @return whitePLayer
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * @return method to get Black Player
     */
    public Player getBlackPlayer() {
        return blackPlayer;
    }

    /**
     * method do get all moves made in this game
     * @return all moves in game
     */
    public ArrayList<String> getAllMovesInGame() {
        return allMovesInGame;
    }

    /**
     * it adds a move to list allMovesInGame
     * @param move = single move
     */
    public void addMoveToAllMovesInGame(String move) {
        allMovesInGame.add(move);
    }

    public void setAllMovesInGame(ArrayList<String> allMovesInGame) {
        this.allMovesInGame = allMovesInGame;
    }

    /**
     * method to get current player
     * @return current Player
     */
    public Player getCurrPlayer() {
        return currPlayer;
    }

    /**
     * method to set current player
     * @param currPlayer = current player
     */
    public void setCurrPlayer(Player currPlayer) {
        this.currPlayer = currPlayer;
    }

    /**
     * method to get last player(not current player)
     * @return last player
     */
    public Player getLastPlayer() {
        return lastPlayer;
    }

    /**
     * method to set last player
     * @param lastPlayer = last player (not current player)
     */
    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    /**
     * method to get board
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * method to get Game state
     * @return gameState
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * method to set game state
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * method to get turns without capture
     * @return turnsWithoutCapture
     */
    public int getTurnsWithoutCapture() {
        return turnsWithoutCapture;
    }

    /**
     * method to set turns without capture
     * @param turnsWithoutCapture = how many rounds was piece not captured
     */
    public void setTurnsWithoutCapture(int turnsWithoutCapture) {
        this.turnsWithoutCapture = turnsWithoutCapture;
    }

    /**
     * method to set turns without capture
     * @param realMove = move that is made each round
     */
    public void setTurnsWithoutCapture(Move realMove) {
        // it includes null or moveType EN Passant, because even endSquare is null, it is still capture
        setTurnsWithoutCapture((realMove.getEndPiece() == null || realMove.getType() == MoveType.EN_PASSANT)
                ? getTurnsWithoutCapture() + 1
                : 0);
    }

    /**
     * method to get how many turns pawn didnt move
     * @return turnsWithoutPawnMove
     */
    public int getTurnsWithoutPawnMove() {
        return turnsWithoutPawnMove;
    }

    /**
     * method to set how many turns pawn didnt move
     * @param turnsWithoutPawnMove  = pawn didnt move
     */
    public void setTurnsWithoutPawnMove(int turnsWithoutPawnMove) {
        this.turnsWithoutPawnMove = turnsWithoutPawnMove;
    }

    /**
     * method to set how many turns pawn didnt move
     * @param realMove = move that is made each round
     */
    public void setTurnsWithoutPawnMove(Move realMove) {
        setTurnsWithoutPawnMove((realMove.getStartPiece() instanceof Pawn) ? 0 : getTurnsWithoutPawnMove() + 1);
    }

    /**
     * method to run application
     */
    @Override
    public void run() {
        try {
            play();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * It first call buttons and game runs when GameState==ONGOING, if GameState != ONGOING it breaks.
     * When it ends it autoSave the game.
     * @throws InterruptedException
     * @throws IOException
     */
    public void play() throws InterruptedException, IOException {
        //show buttons on right side
        getBoard().setEarlyGameMessage();
        //giving inside class setTimers() to show chess clock
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                setTimers();
                LocalTime timer1 = LocalTime.of(0,0,0,0);
                LocalTime timer2 = LocalTime.of(0,0,1,0);
                Duration timer = Duration.between(timer1,timer2);
                setCurrPlayer((getTurn() % 2 == 1) ? getWhitePlayer() : getBlackPlayer());
                if(getGameState()==GameState.ONGOING){
                    if(currPlayer==getWhitePlayer()){
                        timerWhite = timerWhite.minus(timer);
                    }
                    else{
                        timerBlack = timerBlack.minus(timer);
                    }
                }
            }
        };
        Timer timer = new Timer();//create a new Timer
        timer.scheduleAtFixedRate(timerTask, 10, 1000);//this line starts the timer at the same time its executed

        //WHILE GAME IS ACTIVE
        while (true) {
            setCurrPlayer((getTurn() % 2 == 1) ? getWhitePlayer() : getBlackPlayer());
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": "+getCurrPlayer().getColor()+" player's turn");
            if(currPlayer==getWhitePlayer()){
                lastPlayer=getBlackPlayer();
            }
            else{
                lastPlayer=getWhitePlayer();
            }
            //LocalTime startTime = LocalTime.now();

            getCurrPlayer().setAllLegalMoves(getBoard());
            getBoard().updatePieceSprites();
            setGameState();

            if (getGameState() != GameState.ONGOING) {
                IsGameOver=true;
                addToList();
                break;
            }
            else{
                addToList();
            }

            // Parsing move with full information from user input because the method
            // "valid from user" just returns the start and end squares
            realMove = currPlayer.getEquivalentLegalMove(getValidMoveFromUser());
            if (realMove.getType() == MoveType.PAWN_PROMOTION) {
                if (currPlayer instanceof HumanPlayer) {
                    getBoard().setPromoButtons(currPlayer.getColor());
                }
                realMove.setPromotionPiece(currPlayer.getPromotionPiece(getBoard()));
            }
            getBoard().clearPromoButtons();

            // Waiting so computer doenst play too fast.
            if (currPlayer instanceof CPUPlayer) {
                Thread.sleep(1000);
            }

            // Making the move and set needed values for next turn
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": moving "+realMove.getStartPiece()+" on position "+ realMove.getEndSquare());
            realMove.movePiece();
            getBoard().setLastPieceToMove(realMove.getStartPiece());
            setTurn(getTurn() + 1);
            setTurnsWithoutCapture(realMove);
            setTurnsWithoutPawnMove(realMove);
        }
        //it shows endgame message
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": show end game message!");
        getBoard().setEndGameMessage(endGameMessage());
        //Write all moves in game to file;
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": save all moves to a file!");
        writeAllMovesInGameToFile();
    }

    /**
     * method to set timers of every player, each player start with 10 minutes
     */
    public void setTimers() {
        Platform.runLater(() -> {
            Calendar cal = new GregorianCalendar();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss");

            timeWhite.setText(dtf.format(timerWhite));
            timeBlack.setText(dtf.format(timerBlack));

            timeWhite2.setText("White time");
            timeBlack2.setText("Black time");

            timeWhite.setFont(new Font("Calibri",40));
            timeWhite.setStyle("-fx-background-color: \n" +
                    "linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                    "linear-gradient(#020b02, #3a3a3a),\n" +
                    "linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),\n" +
                    "linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);\n" +
                    "-fx-background-insets: 0,1,4,5;\n" +
                    "-fx-background-radius: 9,8,5,4;\n" +
                    "-fx-padding: 5 10 5 10;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
            timeBlack.setFont(new Font("Calibri",40));
            timeBlack.setStyle("-fx-background-color: \n" +
                    "linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                    "linear-gradient(#020b02, #3a3a3a),\n" +
                    "linear-gradient(#b9b9b9 0%, #c2c2c2 20%, #afafaf 80%, #c8c8c8 100%),\n" +
                    "linear-gradient(#f5f5f5 0%, #dbdbdb 50%, #cacaca 51%, #d7d7d7 100%);\n" +
                    "-fx-background-insets: 0,1,4,5;\n" +
                    "-fx-background-radius: 9,8,5,4;\n" +
                    "-fx-padding: 5 10 5 10;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");


            timeWhite.setTranslateX(430);
            timeWhite.setTranslateY(-250);
            timeBlack.setTranslateX(565);
            timeBlack.setTranslateY(-250);

            timeWhite2.setTranslateX(430);
            timeWhite2.setTranslateY(-290);
            timeBlack2.setTranslateX(565);
            timeBlack2.setTranslateY(-290);
            getBoard().getGraphicBoard().getChildren().remove(timeWhite);
            getBoard().getGraphicBoard().getChildren().add(timeWhite);
            getBoard().getGraphicBoard().getChildren().remove(timeBlack);
            getBoard().getGraphicBoard().getChildren().add(timeBlack);

            getBoard().getGraphicBoard().getChildren().remove(timeWhite2);
            getBoard().getGraphicBoard().getChildren().add(timeWhite2);
            getBoard().getGraphicBoard().getChildren().remove(timeBlack2);
            getBoard().getGraphicBoard().getChildren().add(timeBlack2);
        });

    }

    /**
     * it shows message in different game ends
     * @return end game message
     */
    private String endGameMessage() {
        // it shows message in different cases
        switch (getGameState()) {
            case WHITE_WIN:
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": WHITE WON!");
                return "White has won the game!";
            case BLACK_WIN:
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": BLACK WON!");
                return "Black has won the game!";
            case STALEMATE:
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": DRAW BY STALEMATE!");
                return "The game is a draw by stalemate!";
            case FIFTY_MOVE_RULE:
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": DRAW BY FIFTY MOVE RULE!");
                return "The game is a draw by 50 move rule!";
            default:
                // should never reach this
                LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": Houston, we got problem!");
                return "Houston, we have a problem!";
        }
    }

    private Move getValidMoveFromUser() {
        Move userMove = null;
        do {
            userMove = getCurrPlayer().getMove(getBoard());
            getBoard().resetClickQueue();
            getBoard().refreshSquares();
        } while (!currPlayer.legalMovesContains(userMove));
        return userMove;
    }

    /**
     * add move to allMovesInGame list, this list is later used to save moves to pgn format
     */
    private void addToList(){
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": adding move to list!");
        if(getTurn()!=1) {
            ArrayList<Move> allPieces = new ArrayList<>();
            for (Move move : getCurrPlayer().getAllLegalMoves()) {
                if (realMove.getBoard().getLastPieceToMove().getIcon() == move.getStartPiece().getIcon()){
                    allPieces.add(move);
                }
            }
            int i = 0;
            for (Move piece : allPieces) {
                if(piece.getEndSquare() == realMove.getEndSquare()){
                    i++;
                }
            }
            int zk = realMove.getStartSquare().getCOL();
            String column="";
            if(zk==0){
                column="a";
            }
            else if(zk==1){
                column="b";
            }
            else if(zk==2){
                column="c";
            }
            else if(zk==3){
                column="d";
            }
            else if(zk==4){
                column="e";
            }
            else if(zk==5){
                column="f";
            }
            else if(zk==6){
                column="g";
            }
            else if(zk==7){
                column="h";
            }
            if (realMove.getEndPiece() == null && realMove.getType() == MoveType.CASTLE) {
                if (realMove.getType() == MoveType.CASTLE) {
                    if (realMove.getEndSquare().getCOL() == 6) {
                        addMoveToAllMovesInGame("O-O");
                    } else {
                        addMoveToAllMovesInGame("O-O-O");
                    }
                }
            } else if ((getGameState() != (GameState.ONGOING))) {
                if (realMove.getEndPiece() == null) {
                    addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + realMove.toString() + "#");
                } else if (realMove.getEndPiece() != null) {
                    addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + "x" + realMove.toString() + "#");
                }
            } else if(IsGameOver == true){
                System.out.println("Je to over, dont u get it?");
            }else if (realMove.getEndPiece() == null && board.kingIsInCheck(currPlayer.getColor()) == true && i>1) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + column + realMove.toString() + "+");
            } else if (realMove.getEndPiece() != null && board.kingIsInCheck(currPlayer.getColor()) == true) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + "x" + realMove.toString() + "+");
            }else if (realMove.getEndPiece() == null && board.kingIsInCheck(currPlayer.getColor()) == true && i>1) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + column + realMove.toString() + "+");
            } else if (realMove.getEndPiece() != null && board.kingIsInCheck(currPlayer.getColor()) == true) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + "x" + realMove.toString() + "+");
            } else if (realMove.getEndPiece() != null && i>1) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + column +"x" + realMove.toString());//Down done
            } else if (realMove.getEndPiece() != null) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + "x" + realMove.toString());
            } else if (realMove.getEndPiece() == null && i>1) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + column + realMove.toString());
            } else if (realMove.getEndPiece() == null) {
                addMoveToAllMovesInGame(realMove.getStartPiece().getIcon() + realMove.toString());
            }
        }
    }

    /**
     * It writes all moves made in the game to file
     * @throws IOException
     */
    private void writeAllMovesInGameToFile() throws IOException {
        String name = "";
        name = dtf.format(now);
        FileWriter fileWriter = new FileWriter("chess/src/main/java/saves/"+name+".pgn");
        fileWriter.write("[Event \"JUST_CHESS_GAME\"]\n" +
                "[Site \"JanBajerGame\"]\n" +
                "[Date "+dtf.format(now)+"]\n" +
                "[Round "+getTurn()+"]\n"+
                "[White "+whitePlayer.toString()+"]\n"+
                "[Black "+blackPlayer.toString()+"]\n" +
                "[Result "+gameResultString()+"]\n");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        int i = 1;
        int j = 1;
        for (String move : getAllMovesInGame()) {
            if(i%2 == 1){
                bufferedWriter.write(j+". ");
                bufferedWriter.write(move+" ");
                i++;
            }
            else{
                bufferedWriter.write(move+" ");
                j++;
                i++;
            }
        }
        //Then it writes game result
        bufferedWriter.write(gameResultString());
        bufferedWriter.close();
    }

    //3 different game results
    private String gameResultString() {
        switch (getGameState()) {
            case WHITE_WIN:
                IsGameOver = true;
                return "1-0";
            case BLACK_WIN:
                IsGameOver = true;
                return "0-1";
            default:
                return "1/2-1/2";
        }
    }


    /**
     * method to set game state by rules
     */
    private void setGameState() {
        if (inCheckMate()) {
            setGameState((getCurrPlayer().getColor() == ChessColor.WHITE) ? GameState.BLACK_WIN : GameState.WHITE_WIN);
        } else if (inStalemate()) {
            setGameState(GameState.STALEMATE);
        }
        // IS 100 BECAUSE BOTH PLAYERS HAVE TO MOVE TO COUNT FOR 1
        else if (getTurnsWithoutCapture() >= 100 && getTurnsWithoutPawnMove() >= 100) {
            setGameState(GameState.FIFTY_MOVE_RULE);
        } else {
            setGameState(GameState.ONGOING);
        }
    }

    //Check if in checkmate
    private boolean inCheckMate() {
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": checking if in CheckMate");
        return getBoard().kingIsInCheck(getCurrPlayer().getColor()) && getCurrPlayer().getAllLegalMoves().isEmpty();
    }

    //Check if in stalemate
    private boolean inStalemate() {
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": checking if in Stalemate");
        return !getBoard().kingIsInCheck(getCurrPlayer().getColor()) && getCurrPlayer().getAllLegalMoves().isEmpty();
    }
}