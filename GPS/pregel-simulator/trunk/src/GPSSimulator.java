import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Simulates GPS on a single thread by going over each nodes computation
 * one by one. Keeps track of how much communication is happening
 * in each superstep.
 * 
 * @author semihsalihoglu
 */
public class GPSSimulator {

	public static void main(String[] args) throws IOException {
		assert args.length == 5;
		int numberOfMachines = Integer.parseInt(args[0]);
		String partitioningScheme = args[1];
		String inputFile = args[2];
		String outputFilePrefix = args[3];
		String runSpecificSuffix = args[4]; // Could be anything (1, dynamic-with-cumulative-history), etc..
		String algorithm = args[5];
		int dynamismThreshold = -1;
		double dynamismProbability = -1.0;
		boolean isDynamic = false;
		boolean setNextPartitionIdToMessages = false;
		if (args.length >= 7) {
			assert "dynamic".equals(args[6]);
			isDynamic = "dynamic".equals(args[6]);
			assert "next-partition".equals(args[7]) || "current-partition".equals(args[7]);
			setNextPartitionIdToMessages = "next-partition".equals(args[7]);
			if (args.length >= 9) {
				dynamismThreshold = Integer.parseInt(args[8]);
			}
			if (args.length >= 10) {
				dynamismProbability = Double.parseDouble(args[9]);
			}
		}
		System.out.println("dynamic:" + isDynamic + " setNextPartitionIdToMessages: "
				+ setNextPartitionIdToMessages + " dynamismThreshold:" + dynamismThreshold
				+ " dynamismProbability" + dynamismProbability);
		String isDynamicSuffix = "";
		if (isDynamic) {
			isDynamicSuffix += "dynamic-";
			if (setNextPartitionIdToMessages) {
				isDynamicSuffix += "next-partition-id-";
			} else {
				isDynamicSuffix += "current-partition-id-";
			}
		}
		if (dynamismThreshold > 0) {
			isDynamicSuffix += "threshold" + dynamismThreshold + "-";
		}
		if (dynamismProbability > 0.0) {
			isDynamicSuffix += "dynamismprob" + dynamismProbability + "-";
		}
		String networkName = getNetworkName(inputFile);
		File outputDirectory = new File(outputFilePrefix + networkName + "/" + numberOfMachines +
			"-machines" + "/" + partitioningScheme 	+ "/");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}
		String outputFileName = outputDirectory.getAbsolutePath() + "/output-" + algorithm + "-" + isDynamicSuffix
			+ runSpecificSuffix + ".txt";
	    simulateGPS(Graph.readNewGraph(inputFile, false /* directed */, numberOfMachines),
	    		numberOfMachines, outputFileName, partitioningScheme,
	    		networkName, algorithm, isDynamic, setNextPartitionIdToMessages,
	    		dynamismThreshold, dynamismProbability);
	}

	private static String getNetworkName(String inputFile) {
		String[] split = inputFile.split("/");
		return split[split.length - 1].replaceAll(".txt", "");
	}

	public static void simulateGPS(Graph graph, int numberOfMachines, String outputFileName,
			String partitioningScheme, String networkName, String algorithm, boolean isDynamic,
			boolean setNextPartitionIdToMessages, int dynamismThreshold, double dynamismProbability) throws IOException {
	    int[] machineLoadHistogram = new int[numberOfMachines];
	    for (int i = 0; i < numberOfMachines; ++i) {
	    	machineLoadHistogram[i] = 0;
	    }
	    if ("random".equals(partitioningScheme)) {
		    randomlyPartitionUnpartitionedNodes(graph, numberOfMachines, machineLoadHistogram);
	    } else {
	    	String partitionFileName = "/Users/semihsalihoglu/projects/GPS/partition-files/"
	    		+ networkName + "-" + partitioningScheme + "-partition-"
	    		+ numberOfMachines + "-machines.txt";
	    	partitionGraphAccordingToFile(graph, numberOfMachines, partitionFileName,
	    			machineLoadHistogram);
	    }
	    List<Pair> communicationsPerStep = simulateGPS(graph, algorithm,
				isDynamic, setNextPartitionIdToMessages, dynamismThreshold, dynamismProbability);
	    machineLoadHistogram = new int[numberOfMachines];
	    for (Node node : graph.getAllNodes()) {
	    	machineLoadHistogram[node.currentPartitionId]++;
	    }
	    writeResultsToFile(machineLoadHistogram, communicationsPerStep, outputFileName);
 	}

	public static List<Pair> simulateGPS(Graph graph, String algorithm,
			boolean isDynamic, boolean setNextPartitionIdToMessages,
			int dynamismThreshold, double dynamismProbability) {
		Set<Node> activeNodes = new HashSet<Node>(graph.getAllNodes());
	    int superStepNo = 0;
	    List<Pair> communicationsPerStep = new ArrayList<Pair>();
	    while (!activeNodes.isEmpty()) {
	    	superStepNo++;
			Set<Node> newActiveNodes = new HashSet<Node>();
	    	communicationsPerStep.add(simulateSuperStep(graph, activeNodes, newActiveNodes,
	    			superStepNo, algorithm, isDynamic, setNextPartitionIdToMessages,
	    			dynamismThreshold, dynamismProbability));
	    	activeNodes = newActiveNodes;
	    	for (Node node : graph.getAllNodes()) {
	    		node.switchIncomingAndCurrentMessagesAndPartitionIds();
	    	}
	    }
		return communicationsPerStep;
	}

	private static void partitionGraphAccordingToFile(Graph graph, int numberOfMachines,
			String partitioningFile, int[] machineLoadHistogram) throws IOException {
		FileInputStream fileStream = new FileInputStream(partitioningFile);
	    DataInputStream dataInputStream = new DataInputStream(fileStream);
	    BufferedReader bufferedReader = new BufferedReader(
	        		new InputStreamReader(dataInputStream));
	    String line;
	    int currentMachineNumber = 0;
	    while ((line = bufferedReader.readLine()) != null) {
	    	String[] split = line.split("\\s+");
	    	try {
	    		currentMachineNumber = findLeastLoadedMachine(machineLoadHistogram);
	    		for (int i = 0; i < split.length; ++i) {
		    		Integer nodeId = Integer.parseInt(split[i]);
		    		Node node = graph.getNode(nodeId);
		    		node.setPartitionId(currentMachineNumber);
		    		machineLoadHistogram[currentMachineNumber]++;
	    		}
	    	} catch(NumberFormatException e) {
	    		System.out.println("Unexpected nodeId in the partition File! Exiting!!");
	    		System.exit(-1);
	    	}
	    }
	    randomlyPartitionUnpartitionedNodes(graph, numberOfMachines, machineLoadHistogram);
	}

	private static int findLeastLoadedMachine(int[] machineLoadHistogram) {
		int leastLoadedMachineIndex = 0;
		int leastLoad = machineLoadHistogram[0];
		for (int i = 1; i < machineLoadHistogram.length; ++i) {
			if (machineLoadHistogram[i] < leastLoad) {
				leastLoadedMachineIndex = i;
				leastLoad = machineLoadHistogram[i];
			}
		}
		return leastLoadedMachineIndex;
	}

	public static void randomlyPartitionUnpartitionedNodes(Graph graph, int numberOfMachines,
			int[] machineLoadHistogram) {
		List<Integer> unloadedMachineIndices = new ArrayList<Integer>();
		double partitionThreshold = (double) graph.numberOfNodes() / numberOfMachines;
		for (int i = 0; i < machineLoadHistogram.length; ++i) {
			if (machineLoadHistogram[i] < partitionThreshold) {
				unloadedMachineIndices.add(i);
			}
		}
	    Random randomNumberGenerator = new Random();
	    for (Node node : graph.getAllNodes()) {
	    	if (node.currentPartitionId != -1) {
	    		continue;
	    	}
	    	int nextMachine = unloadedMachineIndices.get(
	    			randomNumberGenerator.nextInt(unloadedMachineIndices.size()));
	    	node.setPartitionId(nextMachine);
	    	machineLoadHistogram[nextMachine]++;
	    	if (machineLoadHistogram[nextMachine] > 1.05 * partitionThreshold) {
	    		System.out.println("REMOVING LOADED MACHINE");
	    		unloadedMachineIndices.remove((Object) nextMachine);
	    	}
//	    	System.out.println("machine " + nextMachine + " load" + machineLoadHistogram[nextMachine]);
	    }
	}

	private static void writeResultsToFile(int[] machineLoadHistogram, List<Pair> communicationsPerStep,
			String outputFileName) throws IOException {
		System.out.println("Writing to file: " + outputFileName);
	    File file = new File(outputFileName);
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    int superStepCounter = 0;
    	output.write("machine load:\n");
	    for (int i = 0; i < machineLoadHistogram.length; ++i) {
	    	output.write((i + 1) + "\t");
	    }
	    output.write("\n");
	    for (int i = 0; i < machineLoadHistogram.length; ++i) {
	    	output.write(machineLoadHistogram[i] + "\t");
	    }
	    output.write("\n");	    
	    for (Pair communicationPerStep : communicationsPerStep) {
	    	int totalCommunication = communicationPerStep.value1 + communicationPerStep.value2;
		    output.write(++superStepCounter + " " + totalCommunication +
		    		" " + communicationPerStep.value1 + " " + communicationPerStep.value2 + " \n");
	    }
	    output.close();
	}

	public static Pair simulateSuperStep(Graph graph, Set<Node> activeNodes, Set<Node> newActiveNodes,
			int superStepNo, String algorithm, boolean isDynamic,
			boolean setNextPartitionIdToMessages, int dynamismThreshold,
			double dynamismProbability) {
		System.out.print("Started superstep: " + superStepNo +
				". numberOfActiveNodes: " + activeNodes.size());
		int totalCommunicationInSuperStep = 0;
		int totalMovementInSuperStep = 0;
	    for (Node activeNode : activeNodes) {
	    	 Pair communicationCosts = activeNode.doWork(newActiveNodes, superStepNo,
	    			 algorithm, isDynamic, setNextPartitionIdToMessages,
	    			 dynamismThreshold, dynamismProbability, graph);
	    	 totalCommunicationInSuperStep += communicationCosts.value1;
	    	 totalMovementInSuperStep += communicationCosts.value2;
	    }
	    System.out.println("......Finished superstep: " + superStepNo 
	    		+ "\ttotalCommunication: " + totalCommunicationInSuperStep +
	    		"\ttotalMovement: " + totalMovementInSuperStep);
		return new Pair(totalCommunicationInSuperStep, totalMovementInSuperStep);
	}
}
