import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Node {

	public final int id;
	public int currentPartitionId;
	public int nextPartitionId;
	protected Set<Integer> neighbors;
	private List<Message> currentMessages;
	private List<Message> incomingMessages;
	private int minPreviousSuperStep;
	private double pageRank;
	public Status status = Status.ACTIVE;
    public int searchIndex;
    public Color searchColor;
    public int distance;
    public int[] distancesToPartitionCores = null;
    public int[] machineCommunicationHistogram = null;
   
	public Node(int id) {
		this.id = id;
		this.neighbors = new HashSet<Integer>();
		this.minPreviousSuperStep = id;
		this.pageRank = -1.0;
		this.currentMessages = new ArrayList<Message>();
		this.incomingMessages = new ArrayList<Message>();
	    this.searchIndex = 0;
	    this.searchColor = Color.WHITE;
	    this.distance = -1;
	    this.currentPartitionId = -1;
	    this.nextPartitionId = -1;
	}
	
	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}

	public void initializeDistancesToPartitionCores(int numberOfPartitions) {
		distancesToPartitionCores = new int[numberOfPartitions];
		for (int i = 0; i < distancesToPartitionCores.length; ++i) {
			distancesToPartitionCores[i] = -1;
		}
	}

	public void setPartitionId(int partitionId) {
		this.currentPartitionId = partitionId;
	}

	public void addMessage(Message message, Collection<Node> activeNodes) {
		incomingMessages.add(message);
		status = Status.ACTIVE;
		activeNodes.add(this);
	}
	
	public void initializeMachineCommunicationHistogram(int numberOfMachines) {
		this.machineCommunicationHistogram = new int[numberOfMachines];
	}

	public Pair doWork(Collection<Node> activeNodes, int superStepNo, String algorithm,
			boolean isDynamic, boolean setNextPartitionIdToNodes,
			int dynamismThreshold, double dynamismProbability, Graph graph) {
//		System.out.print("\nNode with id " + this.id +" doWork called\tcurrentPartition " + this.currentPartitionId);
		Map<Integer, Message> messagesToSend = null;
		if ("scc".equals(algorithm)) {
			messagesToSend = findConnectedComponents(superStepNo);			
		} else if ("pagerank".equals(algorithm)) {
			messagesToSend = findPageRank(superStepNo);
		} else {
			return null;
		}

		int numberOfInMachineCommunication = 0;
		for (Message message : currentMessages) {
			if (message.partitionId == this.currentPartitionId) {
				numberOfInMachineCommunication++;
			} else {
				if (isDynamic) {
					machineCommunicationHistogram[message.partitionId]++;
				}
			}
		}
		int numberOfInterMachineCommunication = 0;
		int numberOfNodeTransfers = 0;
		for (int nodeIdToSendMessageTo : messagesToSend.keySet()) {
			Node nodeToSendMessageTo = graph.getNode(nodeIdToSendMessageTo);
			nodeToSendMessageTo.addMessage(messagesToSend.get(nodeIdToSendMessageTo), activeNodes);
			if (nodeToSendMessageTo.currentPartitionId != this.currentPartitionId) {
				numberOfInterMachineCommunication++;
				if (isDynamic) {
					machineCommunicationHistogram[nodeToSendMessageTo.currentPartitionId]++;
				}
			} else {
				numberOfInMachineCommunication++;
			}
		}
//		System.out.print("\tnumberOfInMachineCommunication: " + numberOfInMachineCommunication);

		if (isDynamic) {
			int maxCommunicationMachineIndex = 0;
			int maxCommunication = machineCommunicationHistogram[0];
			for (int i = 1; i < machineCommunicationHistogram.length; ++i) {
				if (machineCommunicationHistogram[i] > maxCommunication) {
					maxCommunication = machineCommunicationHistogram[i];
					maxCommunicationMachineIndex = i;
				}
			}
//			System.out.print("\tmaxCommunicationMachineIndex: " + maxCommunicationMachineIndex);
//			System.out.print("\tmaxCommunication: " + maxCommunication);
			int threshold = dynamismThreshold > 0 ? dynamismThreshold : 1; 
			if (maxCommunication > numberOfInMachineCommunication + threshold) {
				assert dynamismProbability <= 1.0;
				boolean changePartition = false;
				if (dynamismProbability >= 0.0) {
					if (new Random().nextDouble() <= dynamismProbability) {
//						System.out.println("Changing the partition because random " +
//								"number was below " + dynamismProbability);
						changePartition = true;
					} else {
//						System.out.println("Not changing the partition because random" +
//								"number was above " + dynamismProbability);
						changePartition = false;
					}
				} else {
					changePartition = true;
				}
				if (changePartition) {
					this.nextPartitionId = maxCommunicationMachineIndex;
					numberOfNodeTransfers++;
					if (setNextPartitionIdToNodes) {
						for (Message message : messagesToSend.values()) {
							message.partitionId = this.nextPartitionId;
						}
					}
				}
			}
		}
		//TODO(semih): Resetting the communication at every step. Change this!
		machineCommunicationHistogram = new int[machineCommunicationHistogram.length];
		status = Status.INACTIVE;
//		System.out.print("\tnumberOfInterMachineCommunication: " + numberOfInterMachineCommunication);
//		System.out.print("\tnumberOfNodeTransfers: " + numberOfNodeTransfers);
//		System.out.println();
		return new Pair(numberOfInterMachineCommunication, numberOfNodeTransfers);
	}

	private Map<Integer, Message> findPageRank(int superStepNo) {
		Map<Integer, Message> messagesToSend = new HashMap<Integer, Message>();
		if (superStepNo >= 1) {
			double sum = 0.0;
			for (Message message : currentMessages) {
				sum += message.pageRank;
			}
			pageRank = sum;
		}

		if (superStepNo < 40) {
			double valueToSendtoNeighbors = pageRank / neighbors.size();
			for (int neighborId : neighbors) {
				messagesToSend.put(neighborId, new Message(
						this.currentPartitionId, valueToSendtoNeighbors));
			}
		}
		return messagesToSend;
	}

	private Map<Integer, Message> findConnectedComponents(int superStepNo) {
		Map<Integer, Message> messagesToSend = new HashMap<Integer, Message>();
		if (superStepNo < 60) {
			int min = minPreviousSuperStep;
			if (!currentMessages.isEmpty()) {
					for (Message message : currentMessages) {
						min = Math.min(min, message.minSoFar);
					}
			}
			if (min != minPreviousSuperStep || superStepNo == 1) {
				assert superStepNo == 1 || min < minPreviousSuperStep;
				for (int neighborId : neighbors) {
					messagesToSend.put(neighborId, new Message(this.currentPartitionId, min));
				}
				minPreviousSuperStep = min;
			}
		}
		return messagesToSend;
	}

	public void switchIncomingAndCurrentMessagesAndPartitionIds() {
		boolean positiveNumberOfIncomingMessages = !incomingMessages.isEmpty();
		currentMessages = new ArrayList<Message>(incomingMessages);
		incomingMessages.clear();
		if (positiveNumberOfIncomingMessages) {
			assert incomingMessages.isEmpty();
			assert !currentMessages.isEmpty();
		}
		if (nextPartitionId != -1) {
			this.currentPartitionId = nextPartitionId;
			this.nextPartitionId = -1;
		}
	}
	
	public enum Status {
		ACTIVE,
		INACTIVE
	}

    public static enum Color {
      WHITE,
      GRAY,
      BLACK
    }
}
