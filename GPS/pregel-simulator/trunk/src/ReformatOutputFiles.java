import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReformatOutputFiles {

	public static void main(String[] args) throws IOException {
		File mainDirectory = new File("/Users/semihsalihoglu/projects/GPS/output/simulation_results");
		for (File networkDirectory : mainDirectory.listFiles()) {
			if (!networkDirectory.isDirectory()) {
				continue;
			}
			for (File machineSizeDirectory : networkDirectory.listFiles()) {
				if (!machineSizeDirectory.isDirectory()) {
					continue;
				}
				for (File partitionSchemeDirectory : machineSizeDirectory.listFiles()) {
					if (!partitionSchemeDirectory.isDirectory()) {
						continue;
					}
					for (File outputFile : partitionSchemeDirectory.listFiles()) {
						if (!outputFile.isFile() || outputFile.getName().contains("reformatted")) {
							continue;
						}
						reformatOutputFile(outputFile);
					}
				}
			}
		}
	}

	private static void reformatOutputFile(File outputFile) throws IOException {
		FileInputStream inputFileStream = new FileInputStream(outputFile);
	    DataInputStream dataInputStream = new DataInputStream(inputFileStream);
	    BufferedReader bufferedReader = new BufferedReader(
	        		new InputStreamReader(dataInputStream));

	    File totalCommunicationsFile = new File(getReformattedOutputFileName(outputFile.getAbsolutePath(), false /* not shuffles */));
	    File nodeShufflesFile = new File(getReformattedOutputFileName(outputFile.getAbsolutePath(), true /* is shuffles */));
	    BufferedWriter totalCommunicationsOutput = new BufferedWriter(new FileWriter(totalCommunicationsFile));
	    BufferedWriter nodeShufflesOutput = new BufferedWriter(new FileWriter(nodeShufflesFile));

	    String line;
	    while ((line = bufferedReader.readLine()) != null) {
	    	if (line.contains("machine")) {
	    		continue;
	    	}
	    	String[] split = line.split("\\s+");
	    	if (split.length > 4) {
	    		continue;
	    	}
	        totalCommunicationsOutput.write(split[1]);
	        totalCommunicationsOutput.write("\n");
	        if (split.length == 4) {
	        	nodeShufflesOutput.write(split[3]);
	        	nodeShufflesOutput.write("\n");
	        }
	    }
	    totalCommunicationsOutput.close();
	    nodeShufflesOutput.close();
	    inputFileStream.close();
	}

	private static String getReformattedOutputFileName(String name, boolean isShuffles) {
		String reformattedName = name.substring(0, name.indexOf(".txt"));
		String suffix = isShuffles ? "node-shuffles" : "total-communication";
		return reformattedName + "-reformatted-" + suffix + ".txt";
	}
}
