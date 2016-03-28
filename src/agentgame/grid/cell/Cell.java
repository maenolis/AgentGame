package agentgame.grid.cell;

import java.util.List;

import agentgame.entity.MapEntity;
import agentgame.grid.Grid;
import agentgame.grid.MoveEnum;
import agentgame.grid.PositionTools;
import agentgame.path.point.GridPoint;

/**
 * Represents everything on the map. Holds information like occupant(building,
 * agent) etc.
 */
public final class Cell implements GridPoint {

	/** The grid. */
	private Grid grid;

	/** The x. */
	private int x;

	/** The y. */
	private int y;

	/** The occupant. */
	private MapEntity occupant;

	/** The unreachable. */
	private boolean unreachable;

	/**
	 * Instantiates a new cell.
	 */
	public Cell() {
		this.occupant = null;
		this.x = -1;
		this.y = -1;
	}

	/**
	 * Instantiates a new cell.
	 *
	 * @param grid
	 *            the grid
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public Cell(final Grid grid, final int x, final int y) {
		PositionTools.checkNegative(x, "x");
		PositionTools.checkNegative(y, "y");
		this.occupant = null;
		unreachable = false;
		this.x = y;
		this.y = x;
		this.grid = grid;
	}

	/**
	 * Empties the cell from occupants.
	 */
	@Override
	public void clear() {
		occupant = null;
	}

	@Override
	public int getX() {
		return x;
	}

	public void setX(final int x) {
		PositionTools.checkNegative(x, "x");
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see agentgame.path.point.GridPoint#getGridPointByMove(agentgame.grid.
	 * MoveEnum)
	 */
	@Override
	public GridPoint getGridPointByMove(final MoveEnum move) {
		GridPoint point = null;
		switch (move) {
		case UP:
			point = grid.getCells().get(y - 1).get(x);
			break;
		case DOWN:
			point = grid.getCells().get(y + 1).get(x);
			break;
		case LEFT:
			point = grid.getCells().get(y).get(x - 1);
			break;
		case RIGHT:
			point = grid.getCells().get(y).get(x + 1);
			break;

		}
		return point;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * agentgame.path.point.GridPoint#getMoveByGridPoint(agentgame.path.point.
	 * GridPoint)
	 */
	@Override
	public MoveEnum getMoveByGridPoint(final GridPoint point) {
		if (point.getX() < x) {
			return MoveEnum.LEFT;
		} else if (point.getX() > x) {
			return MoveEnum.RIGHT;
		} else if (point.getY() > y) {
			return MoveEnum.DOWN;
		} else {
			return MoveEnum.UP;
		}
	}

	@Override
	public boolean isOccupied() {
		return unreachable || occupant != null;
	}

	@Override
	public List<GridPoint> getEmptyNeighboors() {
		return grid.getEmptyNeighboors(this);
	}

	@Override
	public List<GridPoint> getAllNeighboors() {
		return grid.getAllNeighboors(this);
	}

	@Override
	public void setOccupant(final MapEntity occupant) {
		this.occupant = occupant;
	}

	public void setY(final int y) {
		PositionTools.checkNegative(y, "y");
		this.y = y;
	}

	@Override
	public MapEntity getOccupant() {
		return occupant;
	}

	public boolean isUnreachable() {
		return unreachable;
	}

	public void setUnreachable(final boolean unreachable) {
		this.unreachable = unreachable;
	}

	@Override
	public GridPoint fetchGridPoint(final int x, final int y) {
		return grid.getCells().get(x).get(y);
	}

	@Override
	public void showCartesianPoint() {
		System.out.println("Cell=" + "x:" + x + ",y:" + y);

	}

}
