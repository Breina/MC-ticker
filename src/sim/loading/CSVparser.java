package sim.loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import logging.Log;
import sim.constants.Constants;

/**
 * This class is used to parse fields.csv and methods.csv in MCP's conf folder
 */
public class CSVparser {

	/**
	 * Reads the first step of the deobfuscation process, does so both for fields and methods
	 * @param csvFile Which of the 2 files to parse
	 * @param requirements What to search for
	 * @return A hashmap of < obfuscated name, full name >
	 * @throws IOException 
	 */
	public HashMap<String, String> readDeobfuscation(File csvFile, String[][] requirements) throws IOException {
		
		HashMap<String, String> obfuscatedNames = new HashMap<String, String>();
		
		int length = requirements.length;
		boolean[] foundArray = new boolean[length];
		
		BufferedReader br = null;
		String line = "";
	 
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			
			// Iterate through all lines
			while ((line = br.readLine()) != null) {

				// {func_####_a , original name , 0=client 1=server}
				String[] parsingLine = line.split(",");
				
				// are we dealing with client?
				if (parsingLine[2].equals("0")) {
					
					// loop through requirements
					for (int i = 0; i < length; i++) {
						
						// {name} or {name, part of description}
						String[] requirement = requirements[i];
						
						// if
						if (parsingLine[1].equals(requirement[0]) &&		// the line matches AND (
								(requirement.length == 1 ||					// no description is given OR
																			// the description is given and found)
								(parsingLine.length >= 4 && parsingLine[3].contains(requirement[1])))) {
								
								// store the obfuscated name
								if (requirement.length != 3)
									obfuscatedNames.put(parsingLine[0], requirement[0]);
								else
									obfuscatedNames.put(parsingLine[0], requirement[2]);
								foundArray[i] = true;
							}
						}
				}
					// if dealing with server, we can abort since the list is sorted client first
//				} else
//					break; // TODO commented this because the first line is commented				
			}
			
			// Check for what is not found
			for (int i = 0; i < length; i++) {
				
				if (!foundArray[i]) {
					
					String name = requirements[i][0];
					obfuscatedNames.put(name, name);
					
					Log.w(name + " was not found in " + csvFile.getName() + ", it should be in " + Constants.JOINEDSRG + " then.");

				}
			}
			
		} finally {
			
			if (br != null) {
					
				br.close();
					
			}
		}
		
		return obfuscatedNames;
	}
}
