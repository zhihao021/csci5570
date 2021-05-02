import java.util.List;
import java.util.Set;

import junit.framework.TestCase;


public class LinearSCCFinderTest extends TestCase {

	public void testGetSccsSortedBySize() {
		Graph graph = new Graph();
		graph.addNodeIfNotExists(1);
		graph.addNodeIfNotExists(2);
		graph.addNodeIfNotExists(3);
		graph.addEdgeIfNotExists(1, 2);
		graph.addEdgeIfNotExists(2, 1);
		graph.addEdgeIfNotExists(3, 2);

		List<Set<Integer>> sccs = new LinearSCCFinder(Graph.getTransposeGraph(graph), graph).getSccsSortedBySize();
		assertEquals(2, sccs.size());
		Set<Integer> maximalScc = sccs.get(0);
		assertEquals(2, maximalScc.size());
		assertTrue(maximalScc.contains(1));
		assertTrue(maximalScc.contains(2));
		Set<Integer> smallScc = sccs.get(1);
		assertEquals(1, smallScc.size());
		assertTrue(smallScc.contains(3));
	}
}