package agentgame.window;

import java.io.File;
import java.util.List;

import agentgame.grid.Grid;
import agentgame.parser.FileParser;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AgentGame extends Application {

	private Scene scene;

	public static final double SIZE = 700;

	private Grid grid;

	private BorderPane root;

	private Pane gamePane;

	private GridPane buttonPane;

	private Text pausedText;

	private Text canceledText;

	private File mapFile;

	private File agentFile;

	@Override
	public void start(final Stage primaryStage) {

		try {

			createFirstMenu(primaryStage);

			scene = new Scene(buttonPane, SIZE, SIZE);
			scene.setFill(Color.CHOCOLATE);
			primaryStage.setTitle("AgentGame");
			primaryStage.setScene(scene);

			primaryStage.show();
			final Camera camera = new PerspectiveCamera(false);
			primaryStage.getScene().setCamera(camera);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void createGame(final Stage primaryStage) {
		try {
			final FileParser mapFileParser = new FileParser(mapFile);
			final List<String> mapLines = mapFileParser.parse();

			final FileParser agentFileParser = new FileParser(agentFile);
			final List<String> agentLines = agentFileParser.parse();

			root = new BorderPane();

			gamePane = new Pane();
			root.setCenter(gamePane);

			scene.setRoot(root);
			scene.setFill(Color.CHOCOLATE);
			gamePane.getStyleClass().add("pane");

			grid = new Grid(this, mapLines, agentLines);
			grid.init(scene);

			createPausedMessage();
			createCanceledMessage();

			setScrollZoom(scene);
			final Camera camera = new PerspectiveCamera(false);
			primaryStage.getScene().setCamera(camera);
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private void createFirstMenu(final Stage primaryStage) {
		buttonPane = new GridPane();
		final Button mapFileButton = new Button("Select map file");
		mapFileButton.setPrefSize(SIZE / 3, SIZE / 20);
		mapFileButton.setTranslateX(SIZE / 3);
		mapFileButton.setTranslateY(SIZE * 1.5 / 10);
		final FileChooser mapFileChooser = new FileChooser();
		mapFileChooser.setTitle("Select map file");
		mapFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		mapFileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				mapFile = mapFileChooser.showOpenDialog(primaryStage);
			}
		});
		final Button agentFileButton = new Button("Select agent file");
		agentFileButton.setPrefSize(SIZE / 3, SIZE / 20);
		agentFileButton.setTranslateX(SIZE / 3);
		agentFileButton.setTranslateY(SIZE * 4 / 10);
		final FileChooser agentFileChooser = new FileChooser();
		agentFileChooser.setTitle("Select agent file");
		agentFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		agentFileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				agentFile = agentFileChooser.showOpenDialog(primaryStage);
			}
		});
		final Button startButton = new Button("Start");
		startButton.setPrefSize(SIZE / 3, SIZE / 20);
		startButton.setTranslateX(SIZE / 3);
		startButton.setTranslateY(SIZE * 6.5 / 10);
		startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				createGame(primaryStage);
			}
		});
		buttonPane.add(mapFileButton, 0, 0);
		buttonPane.add(agentFileButton, 0, 1);
		buttonPane.add(startButton, 0, 2);
	}

	private void createPausedMessage() {
		pausedText = new Text(SIZE / 3, SIZE / 2, "Game Paused");
		pausedText.setFont(new Font(40));
		gamePane.getChildren().add(pausedText);
		pausedText.setVisible(false);
	}

	public boolean isPaused() {
		return pausedText.isVisible();
	}

	private void createCanceledMessage() {
		canceledText = new Text(SIZE / 3, SIZE / 2, "Game Canceled");
		canceledText.setFont(new Font(40));
		gamePane.getChildren().add(canceledText);
		canceledText.setVisible(false);
	}

	public void toggleCanceled() {
		canceledText.setVisible(!canceledText.isVisible());
	}

	public boolean isCanceled() {
		return canceledText.isVisible();
	}

	public void togglePaused() {
		try {
			Thread.sleep(50);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		pausedText.setVisible(!pausedText.isVisible());
	}

	private void setScrollZoom(final Scene scene) {
		// handles mouse scrolling
		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(final ScrollEvent event) {
				double zoomFactor = 10;
				final double deltaY = event.getDeltaY();
				if (deltaY < 0) {
					zoomFactor = -10;
				}
				scene.getCamera().setTranslateZ(scene.getCamera().getTranslateZ() + zoomFactor);
				event.consume();
			}
		});

	}

	public static void main(final String[] args) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		launch(args);
	}

	public Pane getPane() {
		return gamePane;
	}
}
