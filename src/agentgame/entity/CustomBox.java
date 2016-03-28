package agentgame.entity;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The Class CustomBox.
 */
public class CustomBox extends Group {

	/** The Constant X_AXIS. */
	private static final Point3D X_AXIS = new Point3D(1, 0, 0);

	/** The Constant Y_AXIS. */
	private static final Point3D Y_AXIS = new Point3D(0, 1, 0);

	/** The Constant Z_AXIS. */
	private static final Point3D Z_AXIS = new Point3D(0, 0, 1);

	/** The Constant STARTING_DIMENSION. */
	public static final double STARTING_DIMENSION = 100.0f;

	/** The top face. */
	private Rectangle top;

	/** The west face. */
	private Rectangle west;

	/** The east face. */
	private Rectangle east;

	/** The south face. */
	private Rectangle south;

	/** The north face. */
	private Rectangle north;

	/** The width. */
	private final double width;

	/** The height. */
	private final double height;

	/** The depth. */
	private final double depth;

	/** The color. */
	private Color color;

	/**
	 * Instantiates a new custom box.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @param color
	 *            the color
	 */
	public CustomBox(final double width, final double height, final double depth, final Color color) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		createFaces();
		setColor(color);
		applyScale();
		alignComponents();
		this.setTranslateZ((STARTING_DIMENSION - depth) / 2);
		// this.setTranslateZ(depth / 2);

	}

	/**
	 * Creates the rectangles for the faces.
	 */
	private void createFaces() {
		top = new Rectangle(STARTING_DIMENSION, STARTING_DIMENSION);
		getChildren().add(top);
		west = new Rectangle(STARTING_DIMENSION, STARTING_DIMENSION);
		getChildren().add(west);
		east = new Rectangle(STARTING_DIMENSION, STARTING_DIMENSION);
		getChildren().add(east);
		south = new Rectangle(STARTING_DIMENSION, STARTING_DIMENSION);
		getChildren().add(south);
		north = new Rectangle(STARTING_DIMENSION, STARTING_DIMENSION);
		getChildren().add(north);

	}

	/**
	 * Sets the colors of all faces.
	 */
	private void applyColors() {
		for (final Node node : getChildren()) {
			final Rectangle rect = (Rectangle) node;
			rect.setFill(color);
		}
	}

	/**
	 * Places the faces in order.
	 */
	private void alignComponents() {
		alignNorthFace();
		alignSouthFace();
		alignWestFace();
		alignEastFace();
	}

	/**
	 * Aligns north face.
	 */
	private void alignNorthFace() {
		north.setRotationAxis(X_AXIS);
		north.setRotate(90);
		north.setTranslateY(-STARTING_DIMENSION / 2);
		north.setTranslateZ(-STARTING_DIMENSION / 2);
	}

	/**
	 * Aligns south face.
	 */
	private void alignSouthFace() {
		south.setRotationAxis(X_AXIS);
		south.setRotate(90);
		south.setTranslateY(STARTING_DIMENSION / 2);
		south.setTranslateZ(-STARTING_DIMENSION / 2);
	}

	/**
	 * Aligns west face.
	 */
	private void alignWestFace() {
		west.setRotationAxis(Y_AXIS);
		west.setRotate(90);
		west.setTranslateZ(-STARTING_DIMENSION / 2);
		west.setTranslateX(-STARTING_DIMENSION / 2);
	}

	/**
	 * Aligns east face.
	 */
	private void alignEastFace() {
		east.setRotationAxis(Y_AXIS);
		east.setRotate(90);
		east.setTranslateZ(-STARTING_DIMENSION / 2);
		east.setTranslateX(STARTING_DIMENSION / 2);
	}

	/**
	 * Applies scale to desired size.
	 */
	private void applyScale() {
		final double scaleX = this.width / STARTING_DIMENSION;
		final double scaleY = this.height / STARTING_DIMENSION;
		final double scaleZ = this.depth / STARTING_DIMENSION;
		setScaleX(scaleX);
		setScaleY(scaleY);
		setScaleZ(scaleZ);
	}

	protected double getCenterX() {
		return top.getTranslateX();
	}

	protected double getCenterY() {
		return top.getTranslateY();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
		applyColors();
	}

}
