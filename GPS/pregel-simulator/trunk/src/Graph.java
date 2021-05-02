import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Graph {
	private HashMap<Integer, Node> nodes;
	private List<Integer> nodeIds;

	public Graph() {
		nodes = new HashMap<Integer, Node>();
		nodeIds = new ArrayList<Integer>();
	}
	
	public Collection<Node> getAllNodes() {
		return nodes.values();
	}

	public Node getNode(int nodeId) {
		return nodes.get(nodeId);
	}

	public boolean containsNode(int id) {
		return nodes.containsKey(id);
	}

	public int getNodeIdWithIndex(int index) {
		return nodeIds.get(index);
	}

	public List<Integer> getNodeIds() {
		return nodeIds;
	}

	public int numberOfNodes() {
		return nodes.size();
	}

	public Node addNodeIfNotExists(int id) {
		if (!nodes.containsKey(id)) {
			return addNode(new Node(id));
		}
		return nodes.get(id);
	}
	
	public void addEdgeIfNotExists(int fromId, int toId) {
		nodes.get(fromId).neighbors.add(toId);
	}

	private Node addNode(Node node) {
		nodes.put(node.id, node);
		nodeIds.add(node.id);
		return node;		
	}

	public void initializedAllNodeDistancesToPartition(int numberOfPartitions) {
		for (Node node : nodes.values()) {
			node.initializeDistancesToPartitionCores(numberOfPartitions);
		}
	}

	public static Graph getSubgraph(Graph graph, Collection<Integer> nodeIds) {
		Graph subGraph = new Graph();
		for (Integer nodeId : nodeIds) {
			subGraph.addNodeIfNotExists(nodeId);
		}
		for (Node node : subGraph.nodes.values()) {
			Node originalNode = graph.nodes.get(node.id);
			for (int originalNeighborId : originalNode.neighbors) {
				if (subGraph.nodes.containsKey(originalNeighborId)) {
					subGraph.nodes.get(node.id).neighbors.add(originalNeighborId);
				}
			}
		}
		return subGraph;
	}

	public static Graph readNewGraph(String inputFile, boolean undirected,
			int numberOfMachines) throws IOException {
		FileInputStream fileStream = new FileInputStream(inputFile);
	    DataInputStream dataInputStream = new DataInputStream(fileStream);
	    BufferedReader bufferedReader = new BufferedReader(
	        		new InputStreamReader(dataInputStream));
	    String line;
	    Graph graph = new Graph();
	    while ((line = bufferedReader.readLine()) != null) {
	    	String[] split = line.split("\\s+");
	    	try {
	    		Integer source = Integer.parseInt(split[0]);
	    		Integer destination = Integer.parseInt(split[1]);
	    		Node sourceNode = graph.addNodeIfNotExists(source);
	    		Node destinationNode = graph.addNodeIfNotExists(destination);
	    		// TODO(semih): Understand why this initialization is being done here.
	    		if (numberOfMachines > 0) {
	    			sourceNode.initializeMachineCommunicationHistogram(numberOfMachines);
	    			destinationNode.initializeMachineCommunicationHistogram(numberOfMachines);
	    		}
	    		if (!sourceNode.neighbors.contains(destinationNode)) {
		    		sourceNode.neighbors.add(destinationNode.id);	    			
	    		}
	    		if (undirected) {
		    		if (!destinationNode.neighbors.contains(sourceNode)) {
			    		destinationNode.neighbors.add(sourceNode.id);	    			
		    		}	
	    		}
	    	} catch(NumberFormatException e) {
	    	}
	    }
	    return graph;
	}

	public static Graph getTransposeGraph(Graph graph) {
		Graph newGraph = new Graph();
		for (Node node: graph.nodes.values()) {
			newGraph.addNodeIfNotExists(node.id);
		}
		for (Node node: graph.nodes.values()) {
			for (int neighborId : node.neighbors) {
				Node neighborCopy = newGraph.nodes.get(neighborId);
				if (neighborCopy != null) {
					neighborCopy.neighbors.add(node.id);
				}
			}
		}
		return newGraph;
	}

	public static class GraphComparator implements Comparator<Graph> {

		@Override
		public int compare(Graph o1, Graph o2) {
			if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			} else {
				return o1.nodes.size() > o2.nodes.size() ? -1 : 1;				
			}
		}
	}
}
