import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BFS {
	
	public static BFSResult doBFS(Graph graph, Integer startNodeId, boolean partitioning, int partitionIndex) {
		Node startNode = graph.getNode(startNodeId);
		startNode.distance = 0;
		if (partitioning) {
			startNode.distancesToPartitionCores[partitionIndex] = 0;
		}
		Node furthestNode = startNode;
		List<Integer> leaves = new ArrayList<Integer>();
		int furthestDistance = 0;
		List<Node> nodes = new LinkedList<Node>();
		nodes.add(startNode);
		int searchIndex = new Random().nextInt();
		startNode.searchIndex = searchIndex;
		startNode.searchColor = Node.Color.GRAY;
		while (!nodes.isEmpty()) {
			Node currentNode = nodes.remove(0);
			for (int childId : currentNode.neighbors) {
				Node child = graph.getNode(childId);
				if (child == null) {
					continue;
				}
				if (child.searchIndex != searchIndex) {
					if (partitioning) {
						child.distancesToPartitionCores[partitionIndex] = -1;
					}
					child.searchIndex = searchIndex;
					child.searchColor = Node.Color.WHITE;
				}
				if (child.searchColor == Node.Color.WHITE) {
					child.searchColor = Node.Color.GRAY;
					int currentNodeDistance = currentNode.distance;
					if (furthestDistance != currentNodeDistance + 1) {
						leaves.clear();
					}
					leaves.add(child.id);
					furthestDistance = currentNodeDistance + 1;
					child.distance = furthestDistance;
					if (partitioning) {
						child.distancesToPartitionCores[partitionIndex] = furthestDistance;
					}
					nodes.add(child);
					furthestNode = child;
				}
			}
		}
		BFSResult retVal = new BFSResult();
		retVal.diameter = furthestNode.distance;
		retVal.sourceNode = startNode;
		retVal.furthestNode = furthestNode;
		retVal.leaves = leaves;
		return retVal;
	}
	
	public static class BFSResult {
		int diameter;
		Node sourceNode;
		Node furthestNode;
		List<Integer> leaves;
	}
}
