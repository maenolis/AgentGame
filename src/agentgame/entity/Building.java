package agentgame.entity;

import javafx.geometry.Dimension2D;

/**
 * Building represents a static entity on the map.
 */
public class Building extends MapEntity {

	/** The Constant LEVEL_HEIGHT. */
	private static final int LEVEL_HEIGHT = 30;

	private boolean isImportant;

	private final char identity;

	/**
	 * Instantiates a new building.
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
	public Building(final String name, final int x, final int y, final double height, final Dimension2D cellDimension,
			final boolean isImportant, final char identity) {
		super(name, x, y, height * LEVEL_HEIGHT, cellDimension);
		this.isImportant = isImportant;
		this.identity = identity;
	}

	public boolean isImportant() {
		return isImportant;
	}

	public void setImportant(final boolean isImportant) {
		this.isImportant = isImportant;
	}

	public char getIdentity() {
		return identity;
	}

}
