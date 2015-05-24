package subtitlyosca;

import java.io.*;
import java.util.regex.*;

public class SubtitleFileReader {

	private BufferedReader br;
		
	public void open(String fileName) {
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (IOException e) {
			System.err.println("SubtitleFile.open() Error: " + e.toString());
		} 		
	}
	
	public void close() {
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("SubtitleFile.close() Error: " + e.toString());
		}
	}
	
	// parse and return a Subtitle object from the file
	// returns null if there's no Subtitle to read
	public Subtitle readNext() {
		try {
			
			String sbText = "";
			
			// jump through the empty lines
			String line = br.readLine();
			if (line == null) return null;
			line = line.trim();
			while (line.isEmpty()) {
				line = br.readLine();
				if (line == null) return null;
				line = line.trim();
			}
			
			// decide whether a line index is found 
			Pattern singledigitP = Pattern.compile("^\\d+$");
			Matcher singledigitM = singledigitP.matcher(line);
			int sbIndex = -1;
			if (singledigitM.find()) {
				sbIndex = Integer.parseInt(singledigitM.group());
				// read next line
				line = br.readLine();
			}
			
			// decide whether timecodes are found
			Pattern timecodeP = Pattern.compile("\\d+\\:\\d+\\:\\d+\\.\\d+");
			Matcher timecodeM = timecodeP.matcher(line);
			long sbStart = -1;
			long sbEnd = -1;
			if (timecodeM.find()) {
				String tc = timecodeM.group();
				sbStart = Subtitles.decodeTC(tc);
				if (timecodeM.find()) {
					tc = timecodeM.group();
					sbEnd = Subtitles.decodeTC(tc);
				}
			} else {
				sbText += line;
			}
			
			if (sbIndex == -1 && (sbStart == -1 || sbEnd == -1)) return null;
			
			line = br.readLine();
			
			while (line != null) {
				line = line.trim();
				if (line.isEmpty()) break;
				if (!sbText.isEmpty()) sbText += " ";
				sbText += line;
				line = br.readLine();
			}
			
			Subtitle ret = new Subtitle(sbStart, sbEnd, sbText);
			if (sbIndex != -1) ret.setIndex(sbIndex);
			
			return ret;
			
		} catch (Exception e) {
			System.err.println("SubtitleFile.readNext() Error: " + e.toString());
		}
		return null;
	}
}
