package sim.loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

class JoinedParser {
	
	/* DEBUG
	private String[] getObfuscatedNames() {
		
		int size = Constants.REQUIREDCLASSES.length;
		List<String> classNames = new ArrayList<String>(size);
		
		for (String requiredClass : Constants.REQUIREDCLASSES)			
			classNames.add(getClassName(requiredClass));
		
		size = classNames.size();
		String[] output = new String[size];
		
		for (int i = 0; i < size; i++)
			output[i] = classNames.get(i);			
		
		return output;
	}
	*/
	
	/**
	 * Random parser crap
	 */
	private String getTrailingPart(String source) {
		String[] sArr = source.split("/");
        return sArr[sArr.length - 1];
	}
	
	/**
	 * Reads the deobfuscated from the joined.srg file
	 * @param srgFile The joined.srg file
	 * @param reqClasses The required classes
	 * @param reqMethods The semi-deobfuscated methods
	 * @param reqFields The semi-deobfuscated fields
	 * @return The completely ready translator object
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Translator readDeobfuscation(File srgFile, String[] reqClasses,
														HashMap<String, String> reqMethods, HashMap<String, String> reqFields) throws IOException, ClassNotFoundException {
		
		Translator translator = new Translator();
		
		ArrayList<String> requiredClasses = new ArrayList<>(reqClasses.length);

        Collections.addAll(requiredClasses, reqClasses);
		
		BufferedReader br = null;
		String line = "";
		String shortName;
			 
		try {
	 
			br = new BufferedReader(new FileReader(srgFile));
			while ((line = br.readLine()) != null) {
				
				String[] values = line.split(" ");
				
				switch (values[0]) {
					
				// CL: aqb net/minecraft/src/BlockRedstoneWire
				case "CL:":					
					shortName = getTrailingPart(values[2]);
					
					if (requiredClasses.contains(shortName)) {
						
						translator.addClass(shortName, values[1]);
						requiredClasses.remove(shortName);
					}
					
					break;
					
				// FD: sg/b net/minecraft/entity/ai/EntityLookHelper/field_75657_b
				case "FD:":
					shortName = getTrailingPart(values[2]);
					
					if (reqFields.containsKey(shortName)) {
						String original = reqFields.get(shortName);
						
						translator.addField(original, getTrailingPart(values[1]));
					}
					break;
					
				// MD: fe/e ()Z net/minecraft/util/ChatStyle/func_150234_e ()Z
				case "MD:":
					shortName = getTrailingPart(values[3]);
					
					if (reqMethods.containsKey(shortName)) {
						String original = reqMethods.get(shortName);
						
						translator.addMethod(original, getTrailingPart(values[1]));
					}
				}
			}
			
		} finally {
			
			if (requiredClasses.size() != 0) {
				
				StringBuilder sb = new StringBuilder();
				
				Iterator<?> i = requiredClasses.iterator();
				sb.append(i.next());
				
				while (i.hasNext()) {
					
					sb.append(", ");
					sb.append(i.next());
				}
				
				sb.append(".");
				
				throw new ClassNotFoundException(sb.toString());
			}
			
			if (br != null) {

				br.close();

			}
		}
		
		return translator;
	}
}
