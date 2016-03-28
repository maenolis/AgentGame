package agentgame.entity.agent;

public class AgentStatistics {

	private int knowledgeTransfers;

	private int knowledgePoints;

	private int randomMoves;

	private int astarMoves;

	private int targetsFound;

	private int blockedMoves;

	private long timeOnTheMove;

	private long startTime;

	private long stopTime;

	public AgentStatistics() {
		reset();
	}

	public void reset() {
		knowledgeTransfers = 0;
		knowledgePoints = 0;
		randomMoves = 0;
		astarMoves = 0;
		targetsFound = 0;
		blockedMoves = 0;
		timeOnTheMove = 0;
	}

	public void increaseKnowledgeTransfers() {
		knowledgeTransfers += 1;
	}

	public void increaseKnowledgePoints() {
		knowledgePoints += 1;
	}

	public void increaseRandomMoves() {
		randomMoves += 1;
	}

	public void increaseAstarMoves() {
		astarMoves += 1;
	}

	public void increaseTargetsFound() {
		targetsFound += 1;
	}

	public void increaseBlockedMoves() {
		blockedMoves += 1;
	}

	public void startWatch() {
		startTime = System.currentTimeMillis();
	}

	public void stopWatch() {
		stopTime = System.currentTimeMillis();
		timeOnTheMove += stopTime - startTime;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("AgentStatistics [knowledgeTransfers=");
		builder.append(knowledgeTransfers);
		builder.append(", knowledgePoints=");
		builder.append(knowledgePoints);
		builder.append(", randomMoves=");
		builder.append(randomMoves);
		builder.append(", astarMoves=");
		builder.append(astarMoves);
		builder.append(", targetsFound=");
		builder.append(targetsFound);
		builder.append(", blockedMoves=");
		builder.append(blockedMoves);
		builder.append(", timeOnTheMove=");
		builder.append(timeOnTheMove);
		builder.append("]");
		return builder.toString();
	}

}
