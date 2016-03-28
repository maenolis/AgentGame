package agentgame.entity.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import agentgame.entity.Building;
import agentgame.entity.MapEntity;
import agentgame.grid.MoveEnum;
import agentgame.grid.PositionTools;
import agentgame.grid.cell.Cell;
import agentgame.path.point.GridPoint;
import agentgame.path.search.AstarSearch;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

public class Agent extends MapEntity {

	private static final String AGENT_NAME = "Agent";

	private static final double DEPTH = 15.0f;

	public static final double SLIM_FACTOR = 0.6;

	private final Polyline path;

	private final GridPoint home;

	private GridPoint currentCell;

	private final List<Character> targets;

	private final Map<Character, Set<GridPoint>> knowledge;

	private final Group knowledgePoints;

	private final Set<GridPoint> visited;

	private final AstarSearch astar;

	private double placeToBeX;

	private double placeToBeY;

	private double speed;

	private boolean isMoving;

	private boolean finished;

	private final Dimension2D cellDimension;

	private final AgentStatistics statistics;

	private int randomTrappedMoves;

	public Agent(final int number, final Cell startingCell, final Dimension2D cellDimension) {
		super(AGENT_NAME + String.valueOf(number), startingCell.getY(), startingCell.getX(), DEPTH, cellDimension);
		astar = new AstarSearch();
		this.cellDimension = new Dimension2D(cellDimension.getWidth() * (1 / SLIM_FACTOR),
				cellDimension.getHeight() * (1 / SLIM_FACTOR));
		this.path = new Polyline();
		this.path.setTranslateX(0.5 * this.cellDimension.getWidth());
		this.path.setTranslateY(0.5 * this.cellDimension.getHeight());
		this.path.setStroke(Color.MAGENTA);
		this.path.setVisible(false);
		this.currentCell = startingCell;
		this.home = startingCell;
		addPositionToPath();
		this.knowledge = new HashMap<Character, Set<GridPoint>>();
		this.knowledgePoints = new Group();
		this.knowledgePoints.setVisible(false);
		this.visited = new HashSet<GridPoint>();
		this.statistics = new AgentStatistics();

		targets = new ArrayList<Character>();
		installMouseFunction();
		addMoveAnimation();
		speed = 0.0;
		isMoving = false;
		this.finished = false;
		this.randomTrappedMoves = 0;
		setInStartingPosition();
	}

	private void installMouseFunction() {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					knowledgePoints.setVisible(!knowledgePoints.isVisible());
				} else if (event.getButton().equals(MouseButton.SECONDARY)) {
					path.setVisible(!path.isVisible());
				}
			}
		});
	}

	public void addMoveAnimation() {
		new AnimationTimer() {

			@Override
			public void handle(final long now) {
				if (!isAtPlaceToBe() && isMoving) {
					goToX();
					goToY();
				} else if (isMoving) {
					setMoving(false);
					inspectPerimeter();
				}
			}
		}.start();
	}

	private void goToX() {
		if (isAtPlaceToBeX()) {
			return;
		}
		if (toCloseToX()) {
			setTranslateX(placeToBeX);
		} else {
			if (placeToBeX < getTranslateX()) {
				setTranslateX(getTranslateX() - speed);
			} else {
				setTranslateX(getTranslateX() + speed);
			}
		}
	}

	private void goToY() {
		if (isAtPlaceToBeY()) {
			return;
		}
		if (toCloseToY()) {
			setTranslateY(placeToBeY);
		} else {
			if (placeToBeY < getTranslateY()) {
				setTranslateY(getTranslateY() - speed);
			} else {
				setTranslateY(getTranslateY() + speed);
			}
		}
	}

	private boolean toCloseToX() {
		final float distance = (float) Math.abs(placeToBeX - getTranslateX());
		return distance < speed;
	}

	private boolean toCloseToY() {
		final float distance = (float) Math.abs(placeToBeY - getTranslateY());
		return distance < speed;
	}

	public boolean isAtPlaceToBe() {
		return isAtPlaceToBeX() && isAtPlaceToBeY();
	}

	private boolean isAtPlaceToBeX() {
		return placeToBeX == getTranslateX();
	}

	private boolean isAtPlaceToBeY() {
		return placeToBeY == getTranslateY();
	}

	private void inspectPerimeter() {
		if (PositionTools.samePoint(currentCell, home)) {
			return;
		}
		final List<GridPoint> allNeighbors = currentCell.getAllNeighboors();
		for (final GridPoint point : allNeighbors) {
			final MapEntity occupant = point.getOccupant();
			if (occupant == null) {
				continue;
			}
			if (occupant instanceof Building) {
				examineBuilding((Building) occupant);
			}
			if (occupant instanceof Agent) {
				knowledgeTransfer((Agent) occupant);
			}
		}
	}

	private void examineBuilding(final Building building) {
		if (!building.isImportant()) {
			return;
		}
		addToKnowledge(building.getIdentity(), currentCell);
	}

	private void knowledgeTransfer(final Agent agent) {
		statistics.increaseKnowledgeTransfers();
		for (final Map.Entry<Character, Set<GridPoint>> entry : agent.getKnowledge().entrySet()) {
			final Character key = entry.getKey();
			final Set<GridPoint> value = entry.getValue();
			final Iterator<GridPoint> iterator = value.iterator();
			while (iterator.hasNext()) {
				addToKnowledge(key, iterator.next());
			}
		}
	}

	private void addToKnowledge(final Character ch, final GridPoint point) {
		if (knowledge.get(ch) == null) {
			knowledge.put(ch, new HashSet<GridPoint>());
		}
		if (!knowledge.get(ch).contains(point)) {
			knowledge.get(ch).add(point);
			statistics.increaseKnowledgePoints();
			final Rectangle known = new Rectangle(cellDimension.getWidth() / 4, cellDimension.getHeight() / 4);
			known.setFill(Color.RED);
			known.setTranslateX((point.getX() + 0.5) * cellDimension.getWidth());
			known.setTranslateY((point.getY() + 0.5) * cellDimension.getHeight());
			knowledgePoints.getChildren().add(known);
		}
	}

	public void findNextMove() {
		if (!this.isVisible()) {
			return;
		}
		Set<GridPoint> target = null;
		if (isAtHome()) {
			findNextRandomMove();
		} else if (hasTarget()) {
			target = knowledge.get(targets.get(0));
			/* If already at target go to next search. */
			if (isAtTarget(target)) {
				statistics.increaseTargetsFound();
				targets.remove(0);
				if (finished) {
					setVisible(false);
					currentCell.clear();
					System.out.println(name + " " + statistics);
					return;
				}
				if (isPlanFinished()) {
					System.out.println(name + ": DONE!");
					goHome();
				} else {
					findNextMove();
				}
			} else {
				goToTarget(target);
			}
		} else {
			findNextRandomMove();
		}

	}

	private boolean isAtTarget(final Set<GridPoint> target) {
		return target.contains(currentCell);
	}

	private void goHome() {
		final int agentNumber = Integer.parseInt(name.substring(AGENT_NAME.length()));
		final int homeNumber = agentNumber % 10;
		targets.add(String.valueOf(homeNumber).charAt(0));
		finished = true;
		goToTarget(knowledge.get(targets.get(0)));
	}

	private boolean isPlanFinished() {
		return targets.size() == 0;
	}

	private boolean isAtHome() {
		return currentCell == null;
	}

	public boolean hasTarget() {
		/* If targets is not empty and agent knows where to go. */
		return targets.size() > 0 && knowledge.containsKey(targets.get(0));
	}

	private void goToTarget(final Set<GridPoint> target) {
		randomTrappedMoves = 0;
		final MoveEnum nextMove = astar.search(currentCell, target);
		if (nextMove != null) {
			move(nextMove);
			statistics.increaseAstarMoves();
		} else {
			findNextRandomMove();
			statistics.increaseBlockedMoves();
		}

	}

	public void findNextRandomMove() {
		if (randomTrappedMoves > 20) {
			visited.clear();
			randomTrappedMoves = 0;
			System.out.println(name + ": Memory cleared.");
		}
		List<GridPoint> neighbors = currentCell.getEmptyNeighboors();
		if (neighbors.size() == 0) {
			statistics.increaseBlockedMoves();
			return;
		}
		statistics.increaseRandomMoves();
		final Random random = new Random();
		neighbors = filterNeighborsByVisited(neighbors);
		final MoveEnum move = currentCell.getMoveByGridPoint(neighbors.get(random.nextInt(neighbors.size())));
		move(move);
	}

	private List<GridPoint> filterNeighborsByVisited(final List<GridPoint> neighbors) {
		final List<GridPoint> resultList = new ArrayList<GridPoint>();
		for (final GridPoint point : neighbors) {
			if (!visited.contains(point)) {
				resultList.add(point);
			}
		}
		if (resultList.size() > 0) {
			return resultList;
		} else {
			randomTrappedMoves += 1;
			return neighbors;
		}
	}

	private void moveUp() {
		final double future = getTranslateY() - cellDimension.getHeight();
		setPlaceToBeY(future);
		setPlaceToBeX(getTranslateX());
		addPositionToPath();
	}

	private void moveDown() {
		final double future = getTranslateY() + cellDimension.getHeight();
		setPlaceToBeY(future);
		setPlaceToBeX(getTranslateX());
		addPositionToPath();
	}

	private void moveRight() {
		final double future = getTranslateX() + cellDimension.getWidth();
		setPlaceToBeX(future);
		setPlaceToBeY(getTranslateY());
		addPositionToPath();
	}

	private void moveLeft() {
		final double future = getTranslateX() - cellDimension.getWidth();
		setPlaceToBeX(future);
		setPlaceToBeY(getTranslateY());
		addPositionToPath();
	}

	private void addPositionToPath() {
		this.path.getPoints().add(currentCell.getX() * cellDimension.getWidth());
		this.path.getPoints().add(currentCell.getY() * cellDimension.getHeight());
	}

	public void move(final MoveEnum move) {
		if (inspectNeighboorByMove(move)) {
			return;
		}
		swapCells(move);
		visited.add(currentCell);
		switch (move) {
		case UP:
			moveUp();
			break;
		case DOWN:
			moveDown();
			break;
		case LEFT:
			moveLeft();
			break;
		case RIGHT:
			moveRight();
			break;
		}

	}

	public boolean inspectNeighboorByMove(final MoveEnum move) {
		final GridPoint point = currentCell.getGridPointByMove(move);
		return point.isOccupied();
	}

	private void swapCells(final MoveEnum move) {

		/* If in first move do not empty the cell that holds Agent Home. */
		final MapEntity previous = currentCell.getOccupant();
		if (!(previous instanceof Building)) {
			currentCell.clear();
		}
		currentCell = currentCell.getGridPointByMove(move);
		currentCell.setOccupant(this);
	}

	private void setInStartingPosition() {
		this.setTranslateX(
				home.getX() * cellDimension.getWidth() - (STARTING_DIMENSION - cellDimension.getWidth()) / 2);
		this.setTranslateY(
				home.getY() * cellDimension.getHeight() - (STARTING_DIMENSION - cellDimension.getHeight()) / 2);
		setPlaceToBeX(getTranslateX());
		setPlaceToBeY(getTranslateY());
	}

	public void reset() {
		setVisible(true);
		isMoving = false;
		finished = false;
		setInStartingPosition();
		currentCell.clear();
		currentCell = home;
		path.getPoints().clear();
		path.setVisible(false);
		addPositionToPath();
		targets.clear();
		knowledge.clear();
		knowledgePoints.getChildren().clear();
		knowledgePoints.setVisible(false);
		visited.clear();
		statistics.reset();
		randomTrappedMoves = 0;
	}

	public double getPlaceToBeX() {
		return placeToBeX;
	}

	public void setPlaceToBeX(final double placeToBeX) {
		this.placeToBeX = placeToBeX;
	}

	public double getPlaceToBeY() {
		return placeToBeY;
	}

	public void setPlaceToBeY(final double placeToBeY) {
		this.placeToBeY = placeToBeY;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Polyline getPath() {
		return path;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(final double speed) {
		if (isMoving && speed == 0.0 && this.speed > 0.0) {
			statistics.stopWatch();
		} else if (isMoving && speed > 0.0 && this.speed == 0.0) {
			statistics.startWatch();
		}
		this.speed = speed;
	}

	public List<Character> getTargets() {
		return targets;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(final boolean isMoving) {
		this.isMoving = isMoving;
		if (isMoving) {
			statistics.startWatch();
		} else {
			statistics.stopWatch();
		}
	}

	public Map<Character, Set<GridPoint>> getKnowledge() {
		return knowledge;
	}

	public Group getKnowledgePoints() {
		return knowledgePoints;
	}

	public AgentStatistics getStatistics() {
		return statistics;
	}

}
