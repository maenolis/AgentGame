package agentgame.grid;

import agentgame.path.point.CartesianPoint;

/**
 * Provides position related functionality.
 */
public final class PositionTools {

	/**
	 * Checks if the given argument is negative.
	 *
	 * @param arg
	 *            the arg
	 * @param name
	 *            the name
	 */
	public static void checkNegative(final double arg, final String name) {
		if (arg < 0.0f) {
			throw new IllegalArgumentException("Position is always positive: " + name + ": " + arg);
		}
	}

	/**
	 * Checks if a point is within the grid and not occupied.
	 *
	 * @param grid
	 *            the grid
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	public static boolean checkEmptyCell(final Grid grid, final int x, final int y) {
		if (!checkCell(grid, x, y)) {
			return false;
		}
		final boolean checkOccupant = grid.getCells().get(x).get(y).isOccupied();
		return !checkOccupant;
	}

	public static boolean checkCell(final Grid grid, final int x, final int y) {
		final boolean checkX = (x >= 0) && (x < grid.getRows());
		final boolean checkY = (y >= 0) && (y < grid.getColumns());
		return checkX && checkY;
	}

	/**
	 * Checks is 2 points for equality.
	 *
	 * @param point1
	 *            the point1
	 * @param point2
	 *            the point2
	 * @return true, if successful
	 */
	public static boolean samePoint(final CartesianPoint point1, final CartesianPoint point2) {
		final boolean checkX = (point1.getX() == point2.getX());
		final boolean checkY = (point1.getY() == point2.getY());
		return checkX && checkY;
	}

	/**
	 * Gets the move towards to the destination.
	 *
	 * @param destination
	 *            the destination
	 * @param source
	 *            the source
	 * @return the move
	 */
	public static MoveEnum getMove(final CartesianPoint destination, final CartesianPoint source) {
		/* No move can be made for the same point. */
		if (samePoint(destination, source)) {
			throw new IllegalArgumentException("Cannot get move for the same point.");
		}
		/* Check if x is different. */
		final boolean diffX = (destination.getX() != source.getX());
		/* Check if x is different. */
		final boolean diffY = (destination.getY() != source.getY());
		/* Both x and y changed is not allowed. */
		if (diffX && diffY) {
			throw new IllegalArgumentException("Cross moves are not allowed.");
		}

		/* Check for left or right. */
		if (diffX) {
			if (destination.getX() > source.getX()) {
				return MoveEnum.RIGHT;
			} else {
				return MoveEnum.LEFT;
			}
		} else {
			/* Check for up or down. */
			if (destination.getY() > source.getY()) {
				return MoveEnum.DOWN;
			} else {
				return MoveEnum.UP;
			}
		}
	}

}
