import java.util.List;

import junit.framework.TestCase;


public class GPSSimulatorTest extends TestCase {

	public void testDynamicPartitioningOscillation() {
		Graph graph = new Graph();
		Node node1 = graph.addNodeIfNotExists(1);
		Node node2 = graph.addNodeIfNotExists(2);
		graph.addEdgeIfNotExists(1, 2);
		graph.addEdgeIfNotExists(2, 1);

		node1.initializeMachineCommunicationHistogram(2);
		node2.initializeMachineCommunicationHistogram(2);
		node1.setPartitionId(0);
		node2.setPartitionId(1);
		List<Pair> stepByStepCosts = GPSSimulator.simulateGPS(graph, "pagerank", true,
				true /* set next partition id to messages */, 1, -1.0);
		for (int i = 1; i < stepByStepCosts.size() - 1; ++i) {
			Pair costsPerStep = stepByStepCosts.get(i);
			assertEquals(2, costsPerStep.value1);
			assertEquals(2, costsPerStep.value2);
		}

	}
}
