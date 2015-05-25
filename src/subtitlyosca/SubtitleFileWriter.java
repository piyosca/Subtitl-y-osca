package subtitlyosca;

import java.io.*;

public class SubtitleFileWriter {

	private PrintWriter pw;
	
	public void open(String fileName) {
		try {
			pw = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			System.err.println("SubtitleFileWriter.open() Error: " + e.toString());
		} 		
	}
	
	public void close() {
		pw.close();
	}
	
	public void writeSRT(Subtitle sb) {
		if (sb.getIndex() != -1) pw.println(sb.getIndex());
		pw.println(Subtitles.encodeTC(sb.getStartTime()) + " --> " + Subtitles.encodeTC(sb.getEndTime()));
		pw.println(sb.getText());
		pw.println();
	}
	
	
}
