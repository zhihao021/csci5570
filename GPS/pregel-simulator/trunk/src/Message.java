
public class Message {

	public int partitionId;
	public int minSoFar;
	public double pageRank;

	public Message(int partitionId, int value) {
		this.partitionId = partitionId;
		this.minSoFar = value;
	}
	
	public Message(int partitionId, double pageRank) {
		this.partitionId = partitionId;
		this.pageRank = pageRank;
	}
}
