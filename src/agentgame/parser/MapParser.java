package agentgame.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentgame.entity.Building;
import agentgame.grid.Grid;
import agentgame.grid.cell.Cell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/**
 * MapParser parse the result from the FileParser and constructs the entities in
 * the map.
 */
public class MapParser {

	/** The building names. */
	private final Map<String, String> buildingNames;

	/** The building heights. */
	private final Map<String, Integer> buildingHeights;

	/** The building materials. */
	private final Map<String, Material> buildingMaterials;

	/** The Constant EMPTY_CELL. */
	private final static char EMPTY_CELL = ' ';

	private final static char NOT_IMPORTANT_BUILDING = '*';

	/**
	 * Instantiates a new map parser.
	 */
	public MapParser() {
		buildingNames = new HashMap<String, String>();
		buildingHeights = new HashMap<String, Integer>();
		buildingMaterials = new HashMap<String, Material>();

		final Material phong = new PhongMaterial(Color.GREY);

		buildingMaterials.put("*", phong);
		/* Add unimportant building. */
		buildingNames.put("*", "unimportant building");
		buildingHeights.put("*", 1);
		/* Add empty space. */
		buildingNames.put(" ", "empty space");
	}

	/**
	 * Parses the lines and creates the map entities.
	 *
	 * @param grid
	 *            the grid
	 * @param lines
	 *            the lines
	 */
	public void parse(final Grid grid) {
		final List<String> lines = grid.getMapLines();
		/* For every line. */
		for (final String line : lines) {
			/* If line is empty skip. */
			if (line == null || line.isEmpty()) {
				continue;
			}

			/* Check the first character. */
			final char first = line.charAt(0);
			switch (first) {
			case '#':
				/* Comment line. Ignore. */
				break;
			case '~':
				/* Building description. */
				final String key = String.valueOf(line.charAt(1));
				final int descriptionStartAt = line.indexOf(':') + 1;
				final int descriptionEndAt = line.indexOf(',');
				final String description = line.substring(descriptionStartAt, descriptionEndAt).trim();
				final String height = line.substring(descriptionEndAt + 1).trim();
				buildingNames.put(key, description);
				buildingHeights.put(key, Integer.valueOf(height));
				break;
			case 'M':
				/* Map starts. Parse the next Lines. */
				final int mapStartsAtLine = lines.indexOf(line) + 1;
				parseMap(grid, mapStartsAtLine, lines);
				break;
			}

		}
	}

	/**
	 * Parses the map after the character 'M' in the file.
	 *
	 * @param grid
	 *            the grid
	 * @param mapStartsAtLine
	 *            the map starts at line
	 * @param lines
	 *            the lines
	 */
	private void parseMap(final Grid grid, final int mapStartsAtLine, final List<String> lines) {
		determineMapSize(grid, mapStartsAtLine, lines);
		for (int i = mapStartsAtLine; i < lines.size(); i++) {
			/* Create new row for cells. */
			final List<Cell> currentRow = new ArrayList<Cell>();
			/* Parse line to create buildings and cells. */
			final int mapRow = i - mapStartsAtLine;
			parseLine(grid, currentRow, lines.get(i), mapRow);
			/* Finally add current row to grid. */
			grid.getCells().add(currentRow);
		}

	}

	/**
	 * Determine map size.
	 *
	 * @param grid
	 *            the grid
	 * @param mapStartsAtLine
	 *            the map starts at line
	 * @param lines
	 *            the lines
	 */
	private void determineMapSize(final Grid grid, final int mapStartsAtLine, final List<String> lines) {
		final int rows = lines.size() - mapStartsAtLine;
		final int columns = lines.get(mapStartsAtLine).length();
		final double width = grid.getSize() / columns;
		final double height = grid.getSize() / rows;
		grid.createCellDimension(width, height);
	}

	/**
	 * Parses the line cell by cell.
	 *
	 * @param grid
	 *            the grid
	 * @param currentRow
	 *            the current row
	 * @param row
	 *            the row
	 * @param mapRow
	 *            the map row
	 */
	private void parseLine(final Grid grid, final List<Cell> currentRow, final String row, final int mapRow) {
		/* Character by character row parsing */
		for (int i = 0; i < row.length(); i++) {
			/* Create new cell. */
			final Cell currentCell = new Cell(grid, mapRow, i);
			currentRow.add(currentCell);
			final char ch = row.charAt(i);
			/* If the cell is empty continue to the next character. */
			if (ch == EMPTY_CELL) {
				continue;
			}
			Building building = null;
			/* If cell is an Agent home. */
			if (Character.isDigit(ch)) {
				final String name = "Agent Home " + String.valueOf(ch);
				building = new Building(name, i, mapRow, 1, grid.getCellDimension(), true, ch);
				building.setColor(Color.DARKGREEN);
				grid.getAgentHouses().put(Integer.parseInt(String.valueOf(ch)), building);
				grid.addChild(building);
				currentRow.get(i).setOccupant(building);
				currentRow.get(i).setUnreachable(true);
			} else {
				/* Determine and create building if necessary. */
				final String name = buildingNames.get(String.valueOf(ch));
				final int height = buildingHeights.get(String.valueOf(ch));
				final boolean unimportantBuilding = (ch == NOT_IMPORTANT_BUILDING);
				building = new Building(name, i, mapRow, height, grid.getCellDimension(), !unimportantBuilding, ch);
				if (!unimportantBuilding) {
					building.setColor(Color.BLUEVIOLET);
				}
				grid.addChild(building);
				currentRow.get(i).setOccupant(building);
				currentRow.get(i).setUnreachable(true);
			}

		}
	}

}
