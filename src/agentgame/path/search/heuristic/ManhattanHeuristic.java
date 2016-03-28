package agentgame.path.search.heuristic;

import agentgame.path.point.CartesianPoint;

/**
 * Gives Manhattan Distance functionality.
 */
public class ManhattanHeuristic {

	/**
	 * Gets the distance between 2 points.
	 *
	 * @param point1
	 *            the point1
	 * @param point2
	 *            the point2
	 * @return the distance
	 */
	public static int getDistance(final CartesianPoint point1, final CartesianPoint point2) {
		final int distanceX = Math.abs(point1.getX() - point2.getX());
		final int distanceY = Math.abs(point1.getY() - point2.getY());
		return distanceX + distanceY;
	}

}
