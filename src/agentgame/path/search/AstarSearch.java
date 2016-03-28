package agentgame.path.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agentgame.grid.MoveEnum;
import agentgame.grid.PositionTools;
import agentgame.path.point.GridPoint;
import agentgame.path.search.heuristic.ManhattanHeuristic;

/**
 * Provides Astar(A*) search related functionality.
 */
public class AstarSearch {

	/** The open (frontier). */
	private final List<GridPoint> open;

	/** The closed (visited). */
	private final List<GridPoint> closed;

	/** Map holding best case scenario for each point. */
	private final Map<GridPoint, GridPoint> cameFrom;

	/** The cost so far for every point. */
	private final Map<GridPoint, Integer> gScore;

	/** The estimated score to the goal for each point */
	private final Map<GridPoint, Integer> fScore;

	private MoveEnum bestMoveSoFar;

	private int bestScoreSoFar;

	/**
	 * Instantiates a new astar search.
	 */
	public AstarSearch() {
		open = new ArrayList<GridPoint>();
		closed = new ArrayList<GridPoint>();
		cameFrom = new HashMap<GridPoint, GridPoint>();
		gScore = new HashMap<GridPoint, Integer>();
		fScore = new HashMap<GridPoint, Integer>();
		bestScoreSoFar = -1;
		bestMoveSoFar = null;
	}

	/**
	 * Inits the search.
	 *
	 * @param startingPoint
	 *            the starting point
	 * @param target
	 *            the target
	 * @return the move enum
	 */
	public MoveEnum search(final GridPoint startingPoint, final Set<GridPoint> target) {
		if (target.contains(startingPoint)) {
			System.out.println("Already on destination.");
			return null;
		}

		/* Clear previous searches if any. */
		clear();
		resetSoFarFields();

		final Iterator<GridPoint> iterator = target.iterator();
		while (iterator.hasNext()) {
			calculateNextMove(startingPoint, iterator.next());
			clear();
		}

		return bestMoveSoFar;
	}

	/**
	 * Calculates the next move based on A* shortest path at current grid state.
	 *
	 * @param startingPoint
	 *            the starting point
	 * @param target
	 *            the target
	 * @return the move enum
	 */
	private void calculateNextMove(final GridPoint startingPoint, final GridPoint target) {

		gScore.put(startingPoint, 0);
		open.add(startingPoint);

		while (!open.isEmpty()) {

			final GridPoint current = getCurrentBestPoint();

			final List<GridPoint> neighbors = current.getEmptyNeighboors();
			for (final GridPoint neighbor : neighbors) {
				if (closed.contains(neighbor)) {
					continue;
				}
				final int tentativeGScore = gScore.get(current) + 1;
				if (!open.contains(neighbor)) {
					open.add(neighbor);
				} else if (tentativeGScore >= gScore.get(neighbor)) {
					continue;
				}
				cameFrom.put(neighbor, current);
				gScore.put(neighbor, tentativeGScore);
				final int estimatedDistanceFromGoal = tentativeGScore
						+ ManhattanHeuristic.getDistance(neighbor, target);
				fScore.put(neighbor, estimatedDistanceFromGoal);
				if (PositionTools.samePoint(current, target)) {
					break;
				}
			}

			if (PositionTools.samePoint(current, target)) {
				if (bestScoreSoFar < 0 || bestScoreSoFar > gScore.get(current)) {
					bestScoreSoFar = gScore.get(current);
					final List<GridPoint> path = reconstructPath(startingPoint, target);
					bestMoveSoFar = getFirstMoveFromPath(path, startingPoint);
				}
			}
		}

		/* Failure scenario. */
	}

	/**
	 * Gets the current best point from the opened points and move it to closed.
	 *
	 * @return the current best point
	 */
	private GridPoint getCurrentBestPoint() {
		sortOpenedList();
		final GridPoint current = open.get(0);
		open.remove(0);
		closed.add(current);
		return current;
	}

	/**
	 * Sorts opened list.
	 */
	private void sortOpenedList() {
		open.sort(new Comparator<GridPoint>() {
			@Override
			public int compare(final GridPoint o1, final GridPoint o2) {
				return fScore.get(o1) - fScore.get(o2);
			}
		});
	}

	/**
	 * Reconstructs path from finish to start based on cameFrom entries.
	 *
	 * @param finishPoint
	 *            the finish point
	 * @param target
	 * @return the list
	 */
	private List<GridPoint> reconstructPath(final GridPoint startPoint, final GridPoint finishPoint) {
		final List<GridPoint> path = new ArrayList<GridPoint>();

		path.add(finishPoint);
		GridPoint current = finishPoint;
		while (!PositionTools.samePoint(startPoint, current)) {
			current = cameFrom.get(current);
			path.add(current);
		}

		return path;
	}

	/**
	 * Extracts the first move from the current A* given path.
	 *
	 * @param path
	 *            the path
	 * @param start
	 *            the start
	 * @return the first move from path
	 */
	private MoveEnum getFirstMoveFromPath(final List<GridPoint> path, final GridPoint start) {
		final GridPoint first = path.get(path.size() - 2);
		return PositionTools.getMove(first, start);
	}

	/**
	 * Clear the structures for the next search.
	 */
	public void clear() {
		open.clear();
		closed.clear();
		cameFrom.clear();
		fScore.clear();
		gScore.clear();
	}

	private void resetSoFarFields() {
		bestScoreSoFar = -1;
		bestMoveSoFar = null;
	}

}
