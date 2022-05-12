package sample;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.Main.*;


/**
 * class to Load game
 */
public class GameLoad {
    private Player whitePlayer = new HumanPlayer(ChessColor.WHITE);
    private Player blackPlayer = new HumanPlayer(ChessColor.BLACK);
    private Game game = new Game(whitePlayer,blackPlayer);
    private Board boardLoad;
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy");
    static LocalDateTime now = LocalDateTime.now();
    static String[] loadedGame = null;
    private static final Logger LOG = Logger.getLogger(Board.class.getName());
    private Label spectateButton = new Label();
    private static String loadedGameName = "";

    /**
     * method to LOAD GAME
     */
    public GameLoad(String gameName) {
        HBox hbox = new HBox();
        game.setTurn(1);
        this.boardLoad = new Board();
        loadAllMoves(gameName);
        getAllMoves();
        boardLoad.updatePieceSprites();
        boardLoad.getGraphicBoard();
        boardLoad.setEarlyGameMessage();
        setSpectateLabel();
        hbox.getChildren().add(boardLoad.getGraphicBoard());
        primaryStagee.setCenter(hbox);
        //choseGameToContinue();
    }

    public static String[] getAllGames(){
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": searching for game saves");
        File dir = new File("chess/src/main/java/saves");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.length()==23) {
                    return name.contains(".pgn");
                }
                return false;
            }
        };
        return dir.list(filter);
    }

    public static String[] getAllSaves(){
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": getting all saves");
        ArrayList<String> allSaves = new ArrayList<>();
        for (String name : getAllGames()){
            allSaves.add(name);
        }
        return allSaves.toArray(String[]::new);
    }

    public static void choseGameToContinue(){
        String[] games = getAllSaves();
        ListView listView = new ListView();
        for (String game : games){
            listView.getItems().add(game);
        }

        Button button = new Button("Continue this game");
        Button returning = new Button("BACK TO MENU");
        returning.setOnAction(e->{
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": BACK TO MENU");
            primaryStagee.setCenter(createContent());
        });
        button.setOnAction(event -> {
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": GAME SELECTED TO LOAD");
            ObservableList selectedIndices = listView.getSelectionModel().getSelectedIndices();
            String gameName ="";
            for(Object o : selectedIndices){
                gameName = listView.getItems().get(o.hashCode()).toString();
            }
            new GameLoad(gameName);
            loadedGameName=(gameName);
        });
        Label chess = new Label();
        chess.setText("Chess");
        chess.setFont(new Font("Arial",60));
        VBox vBox = new VBox();
        listView.setMaxWidth(500);
        listView.setMaxHeight(500);
        vBox.setMinWidth(200);
        vBox.getChildren().add(chess);
        vBox.getChildren().add(listView);
        vBox.getChildren().add(button);
        vBox.getChildren().add(returning);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        primaryStagee.setCenter(vBox);
    }

    /**
     * set label with text: ONLY SPECTATE
     */
    public void setSpectateLabel() {
        Platform.runLater(() -> {

            spectateButton.setText("ONLY SPECTATE");
            spectateButton.setTranslateX(500);
            spectateButton.setTranslateY(-220);
            spectateButton.setPrefWidth(300);
            spectateButton.setPrefHeight(100);
            spectateButton.setStyle(
                    "-fx-font-family: \"Helvetica\";\n" +
                    "-fx-font-size: 18px;\n" +
                    "-fx-font-weight: bold;\n" +
                    "-fx-text-fill: #990000;\n" +
                    "-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);");
            spectateButton.setAlignment(Pos.BASELINE_CENTER);

            boardLoad.getGraphicBoard().getChildren().add(spectateButton);
        });
    }

    /**
     * it load all moves from file AllMovesInGame3. We have to change file manually ...
     */
    public static void loadAllMoves(String gameName){
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": loading moves!");
        Path path = Path.of("chess/src/main/java/saves/"+gameName);
        ArrayList<String> moveData = new ArrayList<>();
        boolean eof=false;
        String data ="";
        //gets data from file end turns it to string
        try {
            data = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data= data.replace("\n"," ");
        int i=0;


        String originalString = data;
        if(data.contains("# ")){
            int index = data.indexOf("# ");
            String stringToBeInserted =" ";
            data = insertString(originalString,
                    stringToBeInserted,
                    index);
        }
        // Insert the String
        System.out.println(data);
        while (true) {
            i++;
            //turns data to round segments
            int firstIndex = data.indexOf(i + ". ");
            int secondIndex = data.indexOf((i + 1) + ". ");
            if (i>9){
                firstIndex+=1;
            }
            if (secondIndex==-1){
                if (data.contains("0-1")||data.contains("1-0")) {
                    secondIndex = data.length() - 6;
                } else if (data.contains("1/2-1/2")){
                    secondIndex = data.indexOf(" 1/2-1/2");
                } else if (data.contains("*")){
                    secondIndex = data.length() - 2;
                }
                eof=true;
            } else {
                secondIndex-=1;
            }
            String roundSegment = data.substring(firstIndex+3, secondIndex);
            //turns every round segment to game turns white and black
            int whiteIndex = roundSegment.indexOf(" ");
            if (whiteIndex==-1){
                whiteIndex=roundSegment.length();
            }
            String whiteMove = roundSegment.substring(0, whiteIndex);
            String blackMove="";
            if ((roundSegment.length()-whiteIndex)>2) {
                blackMove = roundSegment.substring(whiteIndex + 1, roundSegment.length());
            }
            moveData.add(whiteMove);
            moveData.add(blackMove);
            if (eof){
                break;
            }
        }
        //saves data to class variable loadedGame
        loadedGame= moveData.toArray(new String[0]);
        System.out.println(moveData);
    }

    public static String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        return newString;
    }

    /**
     * we dont get All moves, we make all moves
     */
    public void getAllMoves(){
        ArrayList<Move> list= new ArrayList<>();
        LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": moving piece");
        String[] allMoves = loadedGame;

        System.out.println("---");
        for (int j = 0; j < loadedGame.length-1; j++) {
            int k = 0;
            ArrayList<Move> listSameMove = new ArrayList<>();
            game.setCurrPlayer((j % 2 == 0) ? game.getWhitePlayer() : game.getBlackPlayer());
            game.getCurrPlayer().setAllLegalMoves(boardLoad);

            String thisMove  = allMoves[j];
            String piece = "";
            char xSource = 9;
            char xDest = 9;
            char yDest = 9;
            char promoPiece = 'O';

            //we get piece information with first char
            if(thisMove.startsWith("B")){
                piece ="B";
            }
            else if(thisMove.startsWith("N")){
                piece ="N";
            }
            else if(thisMove.startsWith("Q")){
                piece ="Q";
            }
            else if(thisMove.startsWith("K")){
                piece ="K";
            }
            else if(thisMove.startsWith("R")){
                piece ="R";
            }
            else if(thisMove.startsWith("O")){
                piece ="O";
            }
            else{
                piece ="P";
            }

            //Here in these "if" we get square information
            if(piece=="Q" || piece=="N"|| piece=="K"|| piece=="R"|| piece=="B"){
                if(thisMove.contains("x")){
                    if(thisMove.contains("+") || thisMove.contains("#")){
                        if(thisMove.length()==5){
                            xDest=thisMove.charAt(2);
                            yDest=thisMove.charAt(3);
                        }
                        else{
                            xSource=thisMove.charAt(1);
                            xDest=thisMove.charAt(3);
                            yDest=thisMove.charAt(4);
                        }
                    }
                    else if(thisMove.length()==4){
                        xDest=thisMove.charAt(2);
                        yDest=thisMove.charAt(3);
                    }
                    else{
                        xSource=thisMove.charAt(1);
                        xDest=thisMove.charAt(3);
                        yDest=thisMove.charAt(4);
                    }
                }
                else if(thisMove.contains("+") || thisMove.contains("#")){
                    if(thisMove.length()==4){
                        xDest=thisMove.charAt(1);
                        yDest=thisMove.charAt(2);
                    }
                    else{
                        xSource=thisMove.charAt(1);
                        xDest=thisMove.charAt(2);
                        yDest=thisMove.charAt(3);
                    }
                }
                else if(thisMove.length()==3){
                    xDest=thisMove.charAt(1);
                    yDest=thisMove.charAt(2);
                }
                else{
                    xSource=thisMove.charAt(1);
                    xDest=thisMove.charAt(2);
                    yDest=thisMove.charAt(3);
                }
            }
            else if(piece=="P"){
                if(thisMove.contains("x")){
                    if(thisMove.contains("+") || thisMove.contains("#")){
                        if(thisMove.contains("=")){
                            if(thisMove.length()==7){
                                xSource=thisMove.charAt(0);
                                xDest=thisMove.charAt(2);
                                yDest=thisMove.charAt(3);
                                promoPiece=thisMove.charAt(5);
                            }
                            else{
                                xDest=thisMove.charAt(1);
                                yDest=thisMove.charAt(2);
                                promoPiece=thisMove.charAt(4);
                            }
                        }
                        else if(thisMove.length()==5){
                            xSource=thisMove.charAt(0);
                            xDest=thisMove.charAt(2);
                            yDest=thisMove.charAt(3);
                        }
                        else{
                            xDest=thisMove.charAt(1);
                            yDest=thisMove.charAt(2);
                        }
                    }
                    else if(thisMove.contains("=")){
                        if(thisMove.length()==6){
                            xSource=thisMove.charAt(0);
                            xDest=thisMove.charAt(2);
                            yDest=thisMove.charAt(3);
                            promoPiece=thisMove.charAt(5);
                        }
                        else{
                            xDest=thisMove.charAt(1);
                            yDest=thisMove.charAt(2);
                            promoPiece=thisMove.charAt(4);
                        }
                    }
                    else{
                        if(thisMove.length()==4){
                            xSource=thisMove.charAt(0);
                            xDest=thisMove.charAt(2);
                            yDest=thisMove.charAt(3);
                        }
                        else{
                            xDest=thisMove.charAt(1);
                            yDest=thisMove.charAt(2);
                        }
                    }
                }
                else if(thisMove.contains("+") || thisMove.contains("#")){
                    if(thisMove.contains("=")){
                        if(thisMove.length()==6){
                            xSource=thisMove.charAt(0);
                            xDest=thisMove.charAt(1);
                            yDest=thisMove.charAt(2);
                            promoPiece=thisMove.charAt(4);
                        }
                        else{
                            xDest=thisMove.charAt(0);
                            yDest=thisMove.charAt(1);
                            promoPiece=thisMove.charAt(3);
                        }
                    }
                }
                else if(thisMove.contains("=")){
                    if(thisMove.length()==5){
                        xSource=thisMove.charAt(0);
                        xDest=thisMove.charAt(1);
                        yDest=thisMove.charAt(2);
                        promoPiece=thisMove.charAt(4);
                    }
                    else{
                        xDest=thisMove.charAt(0);
                        yDest=thisMove.charAt(1);
                        promoPiece=thisMove.charAt(3);
                    }
                }
                else{
                    xDest=thisMove.charAt(0);
                    yDest=thisMove.charAt(1);
                }
            }

            int xCoord = -1;
            int yCoord = -1;
            //Here we change squares so they work in this application
            if(xDest == 'a'){
                xCoord = 0;
            }
            else if(xDest == 'b'){
                xCoord = 1;
            }
            else if(xDest == 'c'){
                xCoord = 2;
            }
            else if(xDest == 'd'){
                xCoord = 3;
            }
            else if(xDest == 'e'){
                xCoord = 4;
            }
            else if(xDest == 'f'){
                xCoord = 5;
            }
            else if(xDest == 'g'){
                xCoord = 6;
            }
            else if(xDest == 'h'){
                xCoord = 7;
            }

            if(yDest == '1'){
                yCoord = 0;
            }
            else if(yDest == '2'){
                yCoord = 1;
            }
            else if(yDest == '3'){
                yCoord = 2;
            }
            else if(yDest == '4'){
                yCoord = 3;
            }
            else if(yDest == '5'){
                yCoord = 4;
            }
            else if(yDest == '6'){
                yCoord = 5;
            }
            else if(yDest == '7'){
                yCoord = 6;
            }
            else if(yDest == '8'){
                yCoord = 7;
            }
            if(piece!="O"){
                if(xCoord==-1 || yCoord==-1){
                    break;
                }
            }
            if(xSource == 'a'){
                xSource = 0;
            }
            else if(xSource == 'b'){
                xSource = 1;
            }
            else if(xSource == 'c'){
                xSource = 2;
            }
            else if(xSource == 'd'){
                xSource = 3;
            }
            else if(xSource == 'e'){
                xSource = 4;
            }
            else if(xSource == 'f'){
                xSource = 5;
            }
            else if(xSource == 'g'){
                xSource = 6;
            }
            else if(xSource == 'h'){
                xSource = 7;
            }

            //Here we make move, (j%2 == 0) means it is white turn, else it is black turn
            if(j%2 == 0) {
                if(thisMove.contains("O-O-O")) {
                    boardLoad.getSquare(0, 2).setPiece(new King(ChessColor.WHITE));
                    boardLoad.getSquare(0, 4).setPiece(null);
                    boardLoad.getSquare(0, 3).setPiece(new Rook(ChessColor.WHITE));
                    boardLoad.getSquare(0, 0).setPiece(null);
                } else if(thisMove.contains("O-O")){
                        boardLoad.getSquare(0,6).setPiece(new King(ChessColor.WHITE));
                        boardLoad.getSquare(0,4).setPiece(null);
                        boardLoad.getSquare(0,5).setPiece(new Rook(ChessColor.WHITE));
                        boardLoad.getSquare(0,7).setPiece(null);
                    }
                else {
                    for (Piece whitePiece : boardLoad.whitePieces()) {
                        if (whitePiece.getName().equals(piece)) {
                            if (whitePiece.getLegalMoves() != null) {
                                for (Move move : whitePiece.getLegalMoves()) {
                                    if ((move.getEndSquare() == boardLoad.getSquare(yCoord, xCoord))) {
                                        if(xSource==9){
                                            if((whitePiece.getName()=="P") && (promoPiece =='Q')){
                                                list.add(move);
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else{
                                                list.add(move);
                                                if(piece=="P"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Pawn(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="Q"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="B"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Bishop(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="R"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Rook(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="N"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Knight(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="K"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new King(ChessColor.WHITE));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                            }
                                        }
                                        else if(move.getStartSquare().getCOL()==xSource){
                                            listSameMove.add(move);
                                            if(piece=="P"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Pawn(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else if(piece=="Q"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else if(piece=="B"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Bishop(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else if(piece=="R"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Rook(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else if(piece=="N"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Knight(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else if(piece=="K"){
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new King(ChessColor.WHITE));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
                if(thisMove.contains("O-O-O")) {
                    boardLoad.getSquare(7, 2).setPiece(new King(ChessColor.BLACK));
                    boardLoad.getSquare(7, 4).setPiece(null);
                    boardLoad.getSquare(7, 3).setPiece(new Rook(ChessColor.BLACK));
                    boardLoad.getSquare(7, 0).setPiece(null);
                } else if(thisMove.contains("O-O")){
                    boardLoad.getSquare(7,6).setPiece(new King(ChessColor.BLACK));
                    boardLoad.getSquare(7,4).setPiece(null);
                    boardLoad.getSquare(7,5).setPiece(new Rook(ChessColor.BLACK));
                    boardLoad.getSquare(7,7).setPiece(null);
                }
                else {
                    for (Piece blackPiece : boardLoad.blackPieces()) {
                        if (blackPiece.getName().equals(piece)) {
                            if (blackPiece.getLegalMoves() != null) {
                                for (Move move : blackPiece.getLegalMoves()) {
                                    if ((move.getEndSquare() == boardLoad.getSquare(yCoord, xCoord))) {
                                        if(xSource==9){
                                            if((blackPiece.getName()=="P") && (promoPiece =='Q')){
                                                list.add(move);
                                                boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.BLACK));
                                                boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                move.getStartSquare().setPiece(null);
                                            }
                                            else{
                                                list.add(move);
                                                if(piece=="P"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Pawn(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="Q"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="B"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Bishop(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="R"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Rook(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="N"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Knight(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="K"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new King(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                            }
                                        }
                                        else{
                                            if(move.getStartSquare().getCOL()==xSource){
                                                if(piece=="P"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Pawn(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="Q"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Queen(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="B"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Bishop(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="R"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Rook(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="N"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new Knight(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                                else if(piece=="K"){
                                                    boardLoad.getSquare(yCoord,xCoord).setPiece(new King(ChessColor.BLACK));
                                                    boardLoad.getSquare(yCoord,xCoord).getPiece().setMoveCount(2);
                                                    move.getStartSquare().setPiece(null);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            boardLoad.printBoard();
        }
        System.out.println(list);
        boardLoad.printBoard();
    }
}