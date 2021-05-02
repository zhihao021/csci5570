import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class LinearSCCFinder {

	private final Graph transposeGraph;
	private final Graph regularGraph;
	public LinearSCCFinder(Graph transposeGraph, Graph regularGraph) {
		this.transposeGraph = transposeGraph;
		this.regularGraph = regularGraph;
	}

	public List<Set<Integer>> getSccsSortedBySize() {
		Stack<Node> finishingTimes = new Stack<Node>();
		for (Node node : transposeGraph.getAllNodes()) {
			if (node.searchColor == Node.Color.WHITE) {
				iterativeDfs(node, finishingTimes, null, transposeGraph);
			} else if (node.searchColor == Node.Color.BLACK) {
				continue;
			} else {
				System.out.println("FOUND A GRAY NODE IN THE SECOND DFS." +
						"SHOULD NOT HAVE HAPPENED!! id: " +
						node.id);
			}
		}

		List<Set<Integer>> sccs = new LinkedList<Set<Integer>>();
		while (!finishingTimes.isEmpty()) {
			Node node = finishingTimes.pop();
			if (node.searchColor == Node.Color.WHITE) {
				Set<Integer> scc = new HashSet<Integer>();
				iterativeDfs(node, null, scc, regularGraph);
				//runDfs(node, null, scc);                                                                                                             
				sccs.add(scc);
			} else if (node.searchColor == Node.Color.BLACK) {
				continue;
			} else {
				System.out.println("FOUND A GRAY NODE IN THE SECOND DFS." +
						"SHOULD NOT HAVE HAPPENED!! id: " + node.id);
			}
		}
		Collections.sort(sccs, new SccComparator());
		return sccs;
	}
	
	class SccComparator implements Comparator<Set<Integer>> {

		@Override
		public int compare(Set<Integer> o1, Set<Integer> o2) {
			if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			} else {
				return o1.size() > o2.size() ? -1 : 1;				
			}
		}
	}
	private void iterativeDfs(Node node, Stack<Node> finishingTimes,
			Set<Integer> scc, Graph graph) {
		Stack<Node> stack = new Stack<Node>();
		stack.push(node);
		while (!stack.isEmpty()) {
			Node currentNode = stack.pop();
			if (currentNode.searchColor == Node.Color.WHITE) {
				currentNode.searchColor = Node.Color.GRAY;
				if (scc != null) {
					scc.add(currentNode.id);
				}
				stack.push(currentNode);
			} else if (currentNode.searchColor == Node.Color.GRAY) {
				currentNode.searchColor = Node.Color.BLACK;
				if (finishingTimes != null) {
					finishingTimes.push(regularGraph.getNode(currentNode.id));
				}
				continue;
			}
			for (int childIdOfCurrentNode: currentNode.neighbors) {
				Node childOfCurrentNode = graph.getNode(childIdOfCurrentNode);
				if (childOfCurrentNode.searchColor == Node.Color.WHITE) {
					stack.push(childOfCurrentNode);

				}
			}
		}
	}
}