package agentgame.parser;

import java.util.List;

import agentgame.entity.Building;
import agentgame.entity.agent.Agent;
import agentgame.grid.Grid;
import agentgame.grid.cell.Cell;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

public class AgentParser {

	public void parseAgents(final Grid grid) {
		final List<String> lines = grid.getAgentLines();
		Agent agent = null;
		for (final String line : lines) {
			if (isNewAgent(line)) {
				/* New Agent found. */
				final int agentNumber = Integer.parseInt(line);
				final Building house = grid.getAgentHouses().get(agentNumber % 10);
				final Cell houseCell = grid.getCells().get(house.getY()).get(house.getX());
				final Dimension2D agentDimension = new Dimension2D(
						grid.getCellDimension().getWidth() * Agent.SLIM_FACTOR,
						grid.getCellDimension().getHeight() * Agent.SLIM_FACTOR);
				agent = new Agent(agentNumber, houseCell, agentDimension);
				agent.setColor(Color.CYAN);
				grid.getAgents().add(agent);
				grid.addChild(agent);
				grid.addChild(agent.getPath());
				grid.addChild(agent.getKnowledgePoints());
			}
			// else if (line.length() > 0) {
			// /* Read Agent's destinations. */
			// agent.getTargets().add(line.charAt(0));
			// }
		}
		parsePlans(grid);
	}

	private boolean isNewAgent(final String line) {
		try {
			Integer.parseInt(line);
		} catch (final NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public void parsePlans(final Grid grid) {
		final List<Agent> agents = grid.getAgents();
		final List<String> lines = grid.getAgentLines();
		Agent agent = null;
		for (final String line : lines) {
			if (isNewAgent(line)) {
				agent = findAgent(agents, line);
			} else if (line.trim().length() > 0) {
				/* Read Agent's destinations. */
				agent.getTargets().add(line.charAt(0));
			}
		}

	}

	private Agent findAgent(final List<Agent> agents, final String line) {
		for (final Agent agent : agents) {
			if (line.trim().equals(agent.getName().substring("Agent".length()))) {
				return agent;
			}
		}
		return null;
	}

}
