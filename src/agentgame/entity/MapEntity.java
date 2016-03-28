package agentgame.entity;

import agentgame.path.point.CartesianPoint;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 * MapEntity provides the basic functionality for every Entity on the map.
 * 
 * @See CartesianPoint Class.
 */
public abstract class MapEntity extends CustomBox implements CartesianPoint {

	/** The x position. */
	protected int x;

	/** The y position. */
	protected int y;

	/** Entity name. */
	protected String name;

	/** Entity tooltip. */
	protected Tooltip tooltip;

	/**
	 * Instantiates a new map entity.
	 *
	 * @param name
	 *            the name
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param height
	 *            the height
	 * @param cellDimension
	 *            the cell dimension
	 */
	public MapEntity(final String name, final int x, final int y, final double height,
			final Dimension2D cellDimension) {
		super(cellDimension.getWidth(), cellDimension.getHeight(), height, Color.BLACK);
		this.x = x;
		this.y = y;

		final double translateX = (x) * cellDimension.getWidth() - (STARTING_DIMENSION - cellDimension.getWidth()) / 2;
		this.setTranslateX(translateX);
		final double translateY = (y) * cellDimension.getHeight()
				- (STARTING_DIMENSION - cellDimension.getHeight()) / 2;
		this.setTranslateY(translateY);
		this.name = name;
		this.tooltip = new Tooltip();
		this.tooltip.setText(name);
		Tooltip.install(this, this.tooltip);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	@Override
	public void showCartesianPoint() {
		System.out.println("MapEntity=name:" + name + ",x:" + x + ",y:" + y);

	}

}
