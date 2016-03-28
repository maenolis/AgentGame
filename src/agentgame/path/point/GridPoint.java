package agentgame.path.point;

import java.util.List;

import agentgame.entity.MapEntity;
import agentgame.grid.MoveEnum;

/**
 * GridPoint implements the needed functionality for Astar computations
 */
public interface GridPoint extends CartesianPoint {

	public boolean isOccupied();

	public List<GridPoint> getEmptyNeighboors();

	public List<GridPoint> getAllNeighboors();

	/**
	 * Gives the GridPoint towards to move's direction.
	 *
	 * @param move
	 *            the move
	 * @return the grid point by move
	 */
	public GridPoint getGridPointByMove(MoveEnum move);

	/**
	 * Gives the move towards to GridPoint's direction.
	 *
	 * @param point
	 *            the point
	 * @return the move by grid point
	 */
	public MoveEnum getMoveByGridPoint(GridPoint point);

	public MapEntity getOccupant();

	public void setOccupant(MapEntity entity);

	public void clear();

	public GridPoint fetchGridPoint(int x, int y);

}
