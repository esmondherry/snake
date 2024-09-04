import java.util.ArrayList;
import java.util.LinkedList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class App extends Application {
    private final int unitSize = 16;
    private final int fieldSize = unitSize * 10;
    private int lastPressed = 3;
    private SnakePiece last;
    private Rectangle foodBox;
    private SnakePiece player;
    private Text score;
    private Pane pane;
    private Circle snakeSprite;
    private ImageView foodSprite;
    private ArrayList<SnakePiece> body;
    private Text scoreHigh;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane borderpane = new BorderPane();
        player = new SnakePiece(unitSize * 2, unitSize * 5, unitSize, unitSize);
        snakeSprite = new Circle(8, Color.RED);
        snakeSprite.centerXProperty().bind(player.xProperty().add(unitSize / 2));
        snakeSprite.centerYProperty().bind(player.yProperty().add(unitSize / 2));

        foodBox = new Rectangle(unitSize * 7, unitSize * 5, unitSize, unitSize);
        foodBox.setFill(Color.BLUE);

        foodSprite = new ImageView("file:images/food.png");
        foodSprite.xProperty().bind(foodBox.xProperty().add(1));
        foodSprite.yProperty().bind(foodBox.yProperty());

        pane = new Pane(snakeSprite, foodSprite);
        pane.setStyle("-fx-border-color:black");
        borderpane.setCenter(pane);

        VBox vBox = new VBox();
        score = new Text("0");
        scoreHigh = new Text("0");
        vBox.setPrefWidth(15);
        vBox.setStyle("-fx-background-color:grey;");
        vBox.getChildren().addAll(score, scoreHigh);
        borderpane.setRight(vBox);

        pane.setMinSize(fieldSize, fieldSize);
        pane.setMaxSize(fieldSize, fieldSize);
        pane.setStyle("-fx-background-color:rgb(70,70,70);");

        Scene scene = new Scene(borderpane);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:images/snake_icon.png"));
        primaryStage.setTitle("Snake");
        primaryStage.show();

        last = player;

        body = new ArrayList<>();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), e -> {

            if (lastPressed == 0) {// up
                player.lastLocation = new int[] { (int) player.getX(), (int) player.getY() };
                player.setY(player.getY() - unitSize);
                if (player.getY() < 0)// wall
                    restart();
            } else if (lastPressed == 1) {// down
                player.lastLocation = new int[] { (int) player.getX(), (int) player.getY() };
                player.setY(player.getY() + unitSize);
                if (player.getY() >= fieldSize) // wall
                    restart();
            } else if (lastPressed == 2) {// left
                player.lastLocation = new int[] { (int) player.getX(), (int) player.getY() };
                player.setX(player.getX() - unitSize);
                if (player.getX() < 0)// wall
                    restart();
            } else if (lastPressed == 3) {// right
                player.lastLocation = new int[] { (int) player.getX(), (int) player.getY() };
                player.setX(player.getX() + unitSize);
                if (player.getX() >= fieldSize)// wall
                    restart();
            }

            for (int i = 0; i < body.size(); i++) {
                if (player.getX() == body.get(i).getX() && player.getY() == body.get(i).getY()) {
                    restart();
                    break;
                }
            }

            SnakePiece next = player.nextSegment;
            SnakePiece curr = player;
            while (next != null && !curr.equals(last)) {

                next.lastLocation = new int[] { (int) next.getX(), (int) next.getY() };
                next.setX(curr.lastLocation[0]);
                next.setY(curr.lastLocation[1]);

                curr = curr.nextSegment;
                next = next.nextSegment;

            }

            foodGet();

        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    lastPressed = 0;
                    break;
                case DOWN:
                    lastPressed = 1;
                    break;
                case LEFT:
                    lastPressed = 2;
                    break;
                case RIGHT:
                    lastPressed = 3;
                    break;
                case W:
                    lastPressed = 0;
                    break;
                case S:
                    lastPressed = 1;
                    break;
                case A:
                    lastPressed = 2;
                    break;
                case D:
                    lastPressed = 3;
                    break;
                case R:
                    restart();
                    break;
            }
        });

    }

    private void foodGet() {
        if (player.getX() == (foodBox.getX()) && player.getY() == (foodBox.getY())) { // food get
            boolean test;
            do {
                test = false;
                foodBox.setX((int) (Math.random() * (fieldSize / unitSize)) * unitSize);
                foodBox.setY((int) (Math.random() * (fieldSize / unitSize)) * unitSize);
                for (int i = 0; i < body.size(); i++) {
                    if (foodBox.getX() == body.get(i).getX() && foodBox.getY() == body.get(i).getY()) {
                        test = true;
                        break;
                    }
                }
                if (foodBox.getX() == player.getX() && foodBox.getY() == player.getY()) {
                    test = true;
                }

            } while (test);
            body.add(new SnakePiece(last.getX(), last.getY(), unitSize, unitSize));
            last.nextSegment = body.get(body.size() - 1);
            last = last.nextSegment;
            pane.getChildren().add(1, last);

            score.setText((Integer.parseInt(score.getText()) + 1) + "");

        }
    }

    private void restart() {
        player.nextSegment = null;
        last = player;
        player.setX(unitSize * 2);
        player.setY(unitSize * 5);

        foodBox.setX(unitSize * 7);
        foodBox.setY(unitSize * 5);

        if (Integer.parseInt(score.getText()) > Integer.parseInt(scoreHigh.getText())) {
            scoreHigh.setText(score.getText());
        }

        score.setText(0 + "");

        body.clear();

        pane.getChildren().clear();
        pane.getChildren().addAll(snakeSprite, foodSprite);

        lastPressed = 3;
    }
}

class SnakePiece extends Rectangle {
    SnakePiece nextSegment;
    int[] lastLocation;

    SnakePiece(double x, double y, double width, double height) {
        super(x, y, width, height);
        super.setFill(Color.LIGHTGREEN);
        lastLocation = new int[] { (int) x, (int) y };
    }
}