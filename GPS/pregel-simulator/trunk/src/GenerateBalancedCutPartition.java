import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Recursively generates balanced cuts in the graph.
 * @author semihsalihoglu
 *
 */
public class GenerateBalancedCutPartition {

	public static void main(String[] args) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		int numberOfMachines = Integer.parseInt(args[0]);
		String inputFile = args[1];
		String outputFileNamePrefix = args[2];
		boolean isBalanced1 = false;
		if ("balanced1".equals(args[3])) {
			isBalanced1 = true;
		} else if ("balanced2".equals(args[3])) {
			isBalanced1 = false;
		} else {
			System.err.println("The type of balanced partitioning should be \"balanced1\" or \"balanced2\"");
			System.exit(-1);
		}
		String outputFileName = outputFileNamePrefix + "-" + args[3] + "-partition-" + numberOfMachines + "-machines.txt";
		System.out.println("OutputFileName: " + outputFileName);
		System.out.println("Free memory: " + runtime.freeMemory());
		Graph directedGraph = Graph.readNewGraph(inputFile, false /* directed */,
				-1 /* not passing number of machines */);
		// Graph undirectedGraph = Graph.readNewGraph(inputFile, true /* undirected */);
		int originalGraphSize = directedGraph.numberOfNodes();
		double partitionSizeThreshold = (double) originalGraphSize / numberOfMachines;
		System.out.println("partitionSizeThreshold: " + partitionSizeThreshold);
		System.out.println("Free memory: " + runtime.freeMemory());
		List<Set<Integer>> maximalSccs = new LinearSCCFinder(directedGraph, Graph.getTransposeGraph(directedGraph)).getSccsSortedBySize();
		runtime.gc();
		System.out.println("Free memory: " + runtime.freeMemory());
		List<Graph> partitions = new LinkedList<Graph>();
		int totalNumberOfNodesToPartition = 0;
		for (int i = 0; i < maximalSccs.size(); i++) {
			int sccSize = maximalSccs.get(i).size();
			if (sccSize > 100) {
				totalNumberOfNodesToPartition += sccSize;
				System.out.println((i+1) + "th maximal scc size: " + sccSize);
				partitions.add(Graph.getSubgraph(directedGraph, maximalSccs.get(i)));
			} else {
				break;
			}
		}
		System.out.println("Percentage of all nodes to partition: " +
				(double) totalNumberOfNodesToPartition / directedGraph.numberOfNodes());
		while (true) {
			if (partitions.get(0).numberOfNodes() < partitionSizeThreshold) {
				System.out.println("Breaking because the largest partition is smaller than the threshold");
				break;
			}
			Graph currentGraphToPartition = partitions.remove(0);
			// TODO(semih): Remove Edges From the Graphs when partitioning
			boolean success = false;
			int numberOfTries = 0;
			Graph bestPartitionedGraph1 = null;
			Graph bestPartitionedGraph2 = null;
			double bestPartitionDifferenceAbsValue = 1.0;
			Random random = new Random();
			int maxNumberOfTrials = 150;
			Map<Integer, Integer> numberOfTriedPairs = new HashMap<Integer, Integer>();
			while (!success) {
				runtime.gc();
				System.out.println("Free memory: " + runtime.freeMemory());
				if (numberOfTries >= maxNumberOfTrials) {
					System.out.println("Tried " + maxNumberOfTrials + " times, adding the best found partitions");
					if (bestPartitionedGraph1 != null && bestPartitionedGraph2 != null) {
						partitions.add(bestPartitionedGraph1);
						partitions.add(bestPartitionedGraph2);
					} else {
						System.out.println("Couldn't find any partitions!");
						partitions.add(currentGraphToPartition);
					}
					numberOfTries = 0;
					bestPartitionedGraph1 = null;
					bestPartitionedGraph2 = null;
					random = new Random();
					break;
				}
				System.out.println("Starting " + numberOfTries + "th try....");
				Pair clusterNodeIds = getClusterNodeIds(currentGraphToPartition, random, isBalanced1);
				if (clusterNodeIds == null) {
					numberOfTries++;
					continue;
				}
				int clusterNode1Id = clusterNodeIds.value1;
				int clusterNode2Id = clusterNodeIds.value2;
				int sumClusterNodeIds = clusterNode1Id + clusterNode2Id;
				if (numberOfTriedPairs.containsKey(sumClusterNodeIds)) {
					int previousValue = numberOfTriedPairs.get(sumClusterNodeIds);
					if (previousValue >= 3) {
						System.out.println("Same leaves!");
						numberOfTries = maxNumberOfTrials;
					} else {
						numberOfTriedPairs.put(sumClusterNodeIds, previousValue + 1);
					}
					continue;
				}

				numberOfTriedPairs.put(sumClusterNodeIds, 1);
				PartitionResult partitionResult = partitionGraphAround(random, currentGraphToPartition,
						clusterNode1Id, clusterNode2Id);
//				System.out.println("First Partition size: " + partitionResult.graph1.nodes.size());
//				System.out.println("Second Partition size: " + partitionResult.graph2.nodes.size());
//				System.out.println("Number of Randomly Partitioned nodes: " + partitionResult.numberOfRandomlyPartitionedNodes);
				double graph1Fraction = (double) partitionResult.graph1.numberOfNodes()/currentGraphToPartition.numberOfNodes();
				double graph2Fraction = (double) partitionResult.graph2.numberOfNodes()/currentGraphToPartition.numberOfNodes();
				double partitionFractionDifferenceAbsValue = Math.abs(graph1Fraction - graph2Fraction);
				double randomlyPartitionFraction =
					(double) partitionResult.numberOfRandomlyPartitionedNodes/currentGraphToPartition.numberOfNodes();
				System.out.println("garph1 fraction:" + graph1Fraction);
				System.out.println("graph2 fraction:" + graph2Fraction);
				System.out.println("randomly partition fraction: " + randomlyPartitionFraction);
				if (graph1Fraction > 0.33 && graph2Fraction > 0.33 && randomlyPartitionFraction < 0.33) {
					System.out.println("Successful Partitioning");
					if (partitionResult.graph1.numberOfNodes() > 0) {
						partitions.add(partitionResult.graph1);
					}
					if (partitionResult.graph2.numberOfNodes() > 0) {
						partitions.add(partitionResult.graph2);
					}
					success = true;
					numberOfTries = 0;
					bestPartitionedGraph1 = null;
					bestPartitionedGraph2 = null;
				} else {
					System.out.println("Failed Partition");
					if (bestPartitionedGraph1 == null && bestPartitionedGraph2 == null) {
						bestPartitionedGraph1 = partitionResult.graph1;
						bestPartitionedGraph2 = partitionResult.graph2;
						bestPartitionDifferenceAbsValue = partitionFractionDifferenceAbsValue;
					} else {
						if (randomlyPartitionFraction < 0.4 && partitionFractionDifferenceAbsValue < bestPartitionDifferenceAbsValue) {
							bestPartitionDifferenceAbsValue = partitionFractionDifferenceAbsValue;
							System.out.print("New best partition found!");
							System.out.print("\tgraph1 fraction:" + graph1Fraction);
							System.out.print("\tgraph2 fraction:" + graph2Fraction);
							System.out.print("\tbestPartitionDifferenceAbsValue" + bestPartitionDifferenceAbsValue);
							System.out.println("randomly partition fraction: " + randomlyPartitionFraction);
							bestPartitionedGraph1 = partitionResult.graph1;
							bestPartitionedGraph2 = partitionResult.graph2;
						}
					}
				}
				numberOfTries++;
			}
			Collections.sort(partitions, new Graph.GraphComparator());
		}

		//doBFSFromAllPartitions(partitions, directedGraph);
		Collections.sort(partitions, new Graph.GraphComparator());
		writePartitionsToFile(partitions, outputFileName);
	}

	private static Pair getClusterNodeIds(Graph currentGraphToPartition, Random random,
			boolean isBalanced1) {
		int clusterNode1Id;
		int clusterNode2Id;
		if (isBalanced1) {
			clusterNode1Id = currentGraphToPartition.getNodeIdWithIndex(
					random.nextInt(currentGraphToPartition.numberOfNodes()));
			clusterNode2Id = currentGraphToPartition.getNodeIdWithIndex(
					random.nextInt(currentGraphToPartition.numberOfNodes()));
		} else {
			int randomNodeId = currentGraphToPartition.getNodeIdWithIndex(
					random.nextInt(currentGraphToPartition.numberOfNodes()));
			System.out.println("Random Node Id: " + randomNodeId);
			BFS.BFSResult bfsResult = BFS.doBFS(currentGraphToPartition, randomNodeId, false, -1);
			if (bfsResult.leaves.size() < 1) {
				System.out.println("No leaf 1st bfs. continuing.");
				return null;
			}
			clusterNode1Id = bfsResult.leaves.get(0);
			BFS.BFSResult bfsResult2 = BFS.doBFS(currentGraphToPartition, clusterNode1Id,
				 false, -1);
			if (bfsResult2.leaves.isEmpty()) {
				System.out.println("No leaf 2nd bfs. continuing.");
				return null;
			}
			clusterNode2Id = bfsResult2.leaves.get(bfsResult2.leaves.size() - 1);
		}
		return new Pair(clusterNode1Id, clusterNode2Id);
	}

//	private static void doBFSFromAllPartitions(List<Graph> partitions,
//			Graph directedGraph) {
//		directedGraph.initializedAllNodeDistancesToPartition(partitions.size());
//		Random random = new Random();
//		for (int i = 0; i < partitions.size(); ++i) {
//			List<Integer> partitionedNodes = partitions.get(i).getNodeIds();
//			BFS.doBFS(directedGraph, partitionedNodes.get(random.nextInt(partitionedNodes.size())),
//				 true, i);
//		}
//		int[] numberOfNewAdditionsToPartitions = new int[partitions.size()];
//		for (int i = 0; i < numberOfNewAdditionsToPartitions.length; ++i) {
//			numberOfNewAdditionsToPartitions[i] = 0;
//		}
//		int totalNumberOfNodesPartitioning = 0;
//		int numberOfDecisionsByRandomness = 0;
//		for (Node node : directedGraph.getAllNodes()) {
//			if (!nodeBelongsToAPartition(partitions, node.id)) {
//				totalNumberOfNodesPartitioning++;
//				boolean finalDecisionByRandomness = false;
//				int closestPartitionIndex = 0;
//				int closestDistance = node.distancesToPartitionCores[0];
//				for (int j = 1; j < node.distancesToPartitionCores.length; ++j) {
//					int distanceToCorej = node.distancesToPartitionCores[j];
//					if (distanceToCorej < closestDistance) {
//						finalDecisionByRandomness = false;
//						closestPartitionIndex = j;
//						closestDistance = distanceToCorej;
//					} else if (distanceToCorej == closestDistance) {
//						if (random.nextBoolean()) {
//							finalDecisionByRandomness = true;
//							closestPartitionIndex = j;
//							closestDistance = distanceToCorej;
//						}
//					}
//				}
//				if (finalDecisionByRandomness) {
//					numberOfDecisionsByRandomness++;
//				}
//				numberOfNewAdditionsToPartitions[closestPartitionIndex]++;
//				partitions.get(closestPartitionIndex).addNodeIfNotExists(node.id);
//			}
//		}
//		System.out.println("PARTITIONING COMPLETE: total number of nodes partitioning: " + totalNumberOfNodesPartitioning + 
//				" numberOfDecisionsByRandomness" + numberOfDecisionsByRandomness + " percentage of randomly partitioned nodes: " +
//				(double) numberOfDecisionsByRandomness / totalNumberOfNodesPartitioning);
//	}

	private static boolean nodeBelongsToAPartition(List<Graph> partitions,
			int nodeId) {
		for (Graph graph : partitions) {
			if (graph.containsNode(nodeId)) {
				return true;
			}
		}
		return false;
	}

	private static PartitionResult partitionGraphAround(Random random, Graph graph, int node1Id, int node2Id) {
		Set<Integer> nodeIds1 = new HashSet<Integer>();
		Set<Integer> nodeIds2 = new HashSet<Integer>();
		System.out.println("Starting Partitioning...node1: " + node1Id + " node2: " + node2Id);
		graph.initializedAllNodeDistancesToPartition(2);
		BFS.doBFS(graph, node1Id, true, 0 /* partition index */);
//		System.out.println("First BFS done...");
//		System.out.println("Second BFS done...");
		BFS.doBFS(graph, node2Id, true, 1  /* partition index */);
		int numberOfRandomlyPartitionedNodes = 0;
//		System.out.println("Number of nodes partitioning: " + graph.nodes.size());
		for (Node node: graph.getAllNodes()) {
			if (node.distancesToPartitionCores[0] < node.distancesToPartitionCores[1]) {
				nodeIds1.add(node.id);
			} else if (node.distancesToPartitionCores[0] > node.distancesToPartitionCores[1]) {
				nodeIds2.add(node.id);
			} else {
				numberOfRandomlyPartitionedNodes++;
				if (random.nextBoolean()) {
					nodeIds1.add(node.id);					
				} else {
					nodeIds2.add(node.id);
				}
			}
		}
//		System.out.println("Number of randomly partitioned nodes: " + numberOfRandomlyPartitionedNodes);
		return new PartitionResult(Graph.getSubgraph(graph, nodeIds1), Graph.getSubgraph(graph, nodeIds2),
				numberOfRandomlyPartitionedNodes);
	}

	private static void writePartitionsToFile(List<Graph> partitions,
			String outputFileName) throws IOException {
	    File file = new File(outputFileName);
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (Graph partition : partitions) {
			System.out.println(partition.numberOfNodes());
		}
		for (Graph partition : partitions) {
			boolean first = true;
			for (Node node : partition.getAllNodes()) {
				if (first) {
					output.write("" + node.id);
					first = false;
				} else {
					output.write("\t" + node.id);
				}
			}
			output.write("\n");
		}
	    output.close();
	}

	public static class PartitionResult {
		public final Graph graph1;
		public final Graph graph2;
		private final int numberOfRandomlyPartitionedNodes;

		public PartitionResult(Graph graph1, Graph graph2, int numberOfRandomlyPartitionedNodes) {
			this.graph1 = graph1;
			this.graph2 = graph2;
			this.numberOfRandomlyPartitionedNodes = numberOfRandomlyPartitionedNodes;
		}
	}
}

//private static BFS.BFSResult findFurthestPairOfNodes(Random random, Graph connectedGraph) {
//int maxNumberOfTimes = connectedGraph.nodes.size() / 500;
//int numberOfRuns = Math.max(maxNumberOfTimes, 50);
//
//BFS.BFSResult furthestBfsResultSoFar = null;
//System.out.println("NumberOfRuns: " + numberOfRuns);
//for (int i = 0; i < numberOfRuns; i++) {
//	if (i % 20 == 0) {
//		System.out.println("Starting run: " + i + " ..");
//	}
//	int randomNodeIdIndex = random.nextInt(
//			connectedGraph.nodes.size());
//	BFS.BFSResult bfsResult = BFS.doBFS(connectedGraph,
//			connectedGraph.nodeIds.get(randomNodeIdIndex), null, false, -1);
//	if (furthestBfsResultSoFar == null ||
//			furthestBfsResultSoFar.diameter < bfsResult.diameter) {
//		furthestBfsResultSoFar = bfsResult;
//	}
//}
//System.out.println("Furthest node distance found: " + furthestBfsResultSoFar.diameter);
//return furthestBfsResultSoFar;
//}
