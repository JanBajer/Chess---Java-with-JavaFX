package sample;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.*;

/**
 * @author Jan Bajer
 * This is main class which u can use to start application.
 */
public class Main extends Application {

    public static final Image icon = new Image("file:chess/src/main/resources/black_Bishop.png",100,100,true,false);
    public static Scene mainScene = null;
    public static BorderPane primaryStagee = new BorderPane();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
    private static final Logger LOG = Logger.getLogger(Board.class.getName());


    public static void main(String[] args) throws IOException {
        launch(args);
    }

    /**
     * @param primaryStage - it is stage I use for this application. On primaryStage I only change scenes.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(icon);
        primaryStagee.setCenter(createContent());
        mainScene = new Scene(primaryStagee,1000,640);
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    /**
     * this method I use as scene for Menu. It has title chess, 4 buttons
     * @return - It returns root. It is Pane(). On this root is Title and 4 Labels. Each Label has unique event.
     */
    static Parent createContent(){
        Pane root = new Pane();
        root.setPrefSize(1000, 640);

        //SET MENU BACKGROUND IMAGE
        try(InputStream is = Files.newInputStream(Paths.get("chess/src/main/java/sample/ch3.jpg"))){
            ImageView img = new ImageView(new Image(is));
            img.setFitWidth(1000);
            img.setFitHeight(640);
            root.getChildren().add(img);
        }
        catch(IOException e) {
            System.out.println("Couldn't load image");
        }

        //TITLE
        Title title = new Title ("C H E S S");
        title.setTranslateX(518);
        title.setTranslateY(100);

        //ON CLICK START NEW GAME VS PC
        MenuItem itemPcPc = new MenuItem("NEW GAME VS PC");
        itemPcPc.setOnMouseClicked((event) -> {
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": Game against computer chosen");
            Player whitePlayer = new HumanPlayer(ChessColor.WHITE);
            Player blackPlayerRobot = new CPUPlayer(ChessColor.BLACK);
            Game game2 = new Game(whitePlayer, blackPlayerRobot);
            HBox hbox = new HBox();
            hbox.getChildren().add(game2.getBoard().getGraphicBoard());
            primaryStagee.setCenter(hbox);
            Thread gameThread = new Thread(game2);
            gameThread.start();


        });

        //ON CLICK START NEW GAME VS PLAYER
        MenuItem itemPlayerPlayer = new MenuItem("NEW GAME VS PLAYER");
        itemPlayerPlayer.setOnMouseClicked((event) -> {
            LOG.log(Level.INFO,dtf.format(LocalDateTime.now())+": Game against player chosen");
            Player whitePlayer = new HumanPlayer(ChessColor.WHITE);
            Player blackPlayer = new HumanPlayer(ChessColor.BLACK);
            Game game = new Game(whitePlayer, blackPlayer);
            HBox hbox = new HBox();
            hbox.getChildren().add(game.getBoard().getGraphicBoard());
            primaryStagee.setCenter(hbox);
            Thread gameThread = new Thread(game);
            gameThread.start();

            /*Thread timer = new Thread(() -> {
                game.setTimers();
            });*/
            //timer.start();
        });

        //ON CLICK LOAD GAME
        MenuItem LoadGame = new MenuItem("LOAD GAME");
        LoadGame.setOnMouseClicked((event) -> {
            //new GameLoad();
            GameLoad.choseGameToContinue();
        });

        //On CLICK CLOSE THE GAME
        MenuItem itemExit = new MenuItem("EXIT GAME");
        itemExit.setOnMouseClicked(event -> System.exit(0));

        MenuBox vbox = new MenuBox(
                itemPcPc,
                itemPlayerPlayer,
                LoadGame,
                itemExit);
        vbox.setTranslateX(520);
        vbox.setTranslateY(200);


        root.getChildren().addAll(title,vbox);

        return root;
    }

    private static class Title extends StackPane{
        public Title(String name) {
            Rectangle bg = new Rectangle(255, 60);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(2);
            bg.setFill(null);

            Text text = new Text(name);
            text.setFill(Color.WHITE);
            text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 50));

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg,text);
        }
    }

    private static class MenuBox extends VBox{
        public MenuBox(MenuItem...items) {
            getChildren().add(createSeperator());

            for(MenuItem item : items) {
                getChildren().addAll(item, createSeperator());
            }
        }

        private Line createSeperator() {
            Line sep = new Line();
            sep.setEndX(255);
            sep.setStroke(Color.RED);
            return sep;
        }

    }

    private static class MenuItem extends StackPane{
        public MenuItem(String name) {
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[] {
                    new Stop(0, Color.RED),
                    new Stop(0.1, Color.DARKRED),
                    new Stop(0.9, Color.DARKRED),
                    new Stop(1, Color.RED)

            });

            Rectangle bg = new Rectangle(260,40);
            bg.setOpacity(0.4);

            Text text = new Text(name);
            text.setFill(Color.DARKGREY);
            text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD,20));

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
            setOnMouseEntered(event -> {
                bg.setFill(gradient);
                text.setFill(Color.WHITE);

            });

            setOnMouseExited(event -> {
                bg.setFill(Color.BLACK);
                text.setFill(Color.DARKGREY);
            });
            setOnMousePressed(event -> {
                bg.setFill(Color.DARKVIOLET);

            });

            setOnMouseReleased(event -> {
                bg.setFill(gradient);
            });

        }
    }
}

