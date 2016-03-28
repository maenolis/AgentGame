package agentgame.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentgame.entity.Building;
import agentgame.entity.agent.Agent;
import agentgame.grid.cell.Cell;
import agentgame.parser.AgentParser;
import agentgame.parser.MapParser;
import agentgame.path.point.GridPoint;
import agentgame.window.AgentGame;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;

/**
 * Represents all the visible world of the game. Holds the map and all its
 * entities.
 */
public final class Grid {

	private static final float SPEED_FACTOR = 0.1f;

	private static final float MAX_SPEED = 4.0f;

	private static final float MIN_SPEED = 0.2f;

	private final AgentGame game;

	/** The grid rows. */
	private int rows;

	/** The grid columns. */
	private int columns;

	private double speed;

	private double tempSpeed;

	/** The grid's cell dimension. */
	private Dimension2D cellDimension;

	/** The cells. */
	private final List<List<Cell>> cells;

	/** The agents. */
	private final List<Agent> agents;

	/** The map parser. */
	private final MapParser mapParser;

	private final AgentParser agentParser;

	private final Map<Integer, Building> agentHouses;

	private final List<String> mapLines;

	private final List<String> agentLines;

	/**
	 * Instantiates a new grid.
	 *
	 * @param pane
	 *            the pane
	 * @param size
	 *            grid size in pixels
	 * @param lines
	 *            the parsed lines from the given file
	 */
	public Grid(final AgentGame game, final List<String> mapLines, final List<String> agentLines) {

		this.game = game;
		this.mapLines = mapLines;
		this.agentLines = agentLines;
		cells = new ArrayList<List<Cell>>();
		agents = new ArrayList<Agent>();
		agentHouses = new HashMap<Integer, Building>();
		mapParser = new MapParser();
		agentParser = new AgentParser();
		mapParser.parse(this);
		agentParser.parseAgents(this);
		this.speed = MAX_SPEED;
	}

	/**
	 * Inits the rows, columns properties and creates the visual grid.
	 */
	public void init(final Scene scene) {
		this.rows = cells.size();
		this.columns = cells.get(0).size();
		createVisualGrid();
		addGameAnimation();
		addSpeedHandling(scene);
		updateSceneSpeed();
	}

	private void addGameAnimation() {

		new AnimationTimer() {

			@Override
			public void handle(final long now) {
				if (agentsMoving()) {
					return;
				}
				for (final Agent agent : agents) {
					agent.findNextMove();
				}

				startAgentsAgain();
			}

		}.start();
	}

	private boolean agentsMoving() {
		for (final Agent agent : agents) {
			if (agent.isMoving()) {
				return true;
			}
		}
		return false;
	}

	private void startAgentsAgain() {
		for (final Agent agent : agents) {
			agent.setMoving(true);
		}
	}

	/**
	 * Creates the visual grid.
	 */
	private void createVisualGrid() {
		for (int i = 0; i < columns + 1; i++) {
			final Line line = new Line(i * cellDimension.getWidth(), 0, i * cellDimension.getWidth(), AgentGame.SIZE);
			game.getPane().getChildren().add(line);
		}

		for (int i = 0; i < rows + 1; i++) {
			final Line line = new Line(0, i * cellDimension.getHeight(), AgentGame.SIZE, i * cellDimension.getHeight());
			game.getPane().getChildren().add(line);
		}

	}

	/**
	 * Gets all the available neighboors.
	 *
	 * @param currentCell
	 *            the current cell
	 * @return the neighboors
	 */
	public List<GridPoint> getEmptyNeighboors(final Cell currentCell) {
		final List<GridPoint> neighboors = new ArrayList<GridPoint>();
		final int x = currentCell.getX();
		final int y = currentCell.getY();
		if (PositionTools.checkEmptyCell(this, y, x - 1)) {
			neighboors.add(cells.get(y).get(x - 1));
		}
		if (PositionTools.checkEmptyCell(this, y, x + 1)) {
			neighboors.add(cells.get(y).get(x + 1));
		}
		if (PositionTools.checkEmptyCell(this, y - 1, x)) {
			neighboors.add(cells.get(y - 1).get(x));
		}
		if (PositionTools.checkEmptyCell(this, y + 1, x)) {
			neighboors.add(cells.get(y + 1).get(x));
		}

		return neighboors;
	}

	public List<GridPoint> getAllNeighboors(final Cell currentCell) {
		final List<GridPoint> neighboors = new ArrayList<GridPoint>();
		final int x = currentCell.getX();
		final int y = currentCell.getY();
		if (PositionTools.checkCell(this, y, x - 1)) {
			neighboors.add(cells.get(y).get(x - 1));
		}
		if (PositionTools.checkCell(this, y, x + 1)) {
			neighboors.add(cells.get(y).get(x + 1));
		}
		if (PositionTools.checkCell(this, y - 1, x)) {
			neighboors.add(cells.get(y - 1).get(x));
		}
		if (PositionTools.checkCell(this, y + 1, x)) {
			neighboors.add(cells.get(y + 1).get(x));
		}

		return neighboors;
	}

	/**
	 * Adds a child to the pane.
	 *
	 * @param e
	 *            the e
	 */
	public void addChild(final Node e) {
		game.getPane().getChildren().add(e);
	}

	public void updateSceneSpeed() {
		for (final Agent agent : agents) {
			if (agent != null) {
				agent.setSpeed(speed);
			}
		}
	}

	private void resetGame() {
		for (final Agent agent : agents) {
			if (agent != null) {
				agent.reset();
			}
		}
		agentParser.parsePlans(this);
		if (game.isPaused()) {
			game.togglePaused();
		}
		if (game.isCanceled()) {
			game.toggleCanceled();
		}
		this.speed = MAX_SPEED;
		updateSceneSpeed();
	}

	private void cancelGame() {
		for (final Agent agent : agents) {
			if (agent != null) {
				agent.setVisible(false);
			}
		}
		if (game.isPaused()) {
			game.togglePaused();
		}
		if (!game.isCanceled()) {
			game.toggleCanceled();
		}
		this.speed = 0.0;
		updateSceneSpeed();
	}

	private void pauseGame() {
		if (game.isCanceled()) {
			return;
		}
		game.togglePaused();
	}

	private void addSpeedHandling(final Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(final KeyEvent event) {

				switch (event.getText()) {
				case "q":
					if (speed < MAX_SPEED) {
						speed += SPEED_FACTOR;
						updateSceneSpeed();
					}
					break;
				case "w":
					if (speed > MIN_SPEED) {
						speed -= SPEED_FACTOR;
						updateSceneSpeed();
					}
					break;
				case "p":
					if (speed > 0.0f) {
						tempSpeed = speed;
						speed = 0.0f;
						updateSceneSpeed();
					} else {
						speed = tempSpeed;
						updateSceneSpeed();
					}
					pauseGame();
					break;
				case "r":
					resetGame();
					break;
				case "c":
					cancelGame();
					break;
				default:
					break;
				}

				event.consume();
			}
		});

	}

	public List<List<Cell>> getCells() {
		return cells;
	}

	public Dimension2D getCellDimension() {
		return cellDimension;
	}

	/**
	 * Creates the cell dimension.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void createCellDimension(final double width, final double height) {
		this.cellDimension = new Dimension2D(width, height);
	}

	public double getSize() {
		return AgentGame.SIZE;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public Map<Integer, Building> getAgentHouses() {
		return agentHouses;
	}

	public List<String> getMapLines() {
		return mapLines;
	}

	public List<String> getAgentLines() {
		return agentLines;
	}

}
