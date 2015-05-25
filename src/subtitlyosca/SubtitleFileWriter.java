package subtitlyosca;

import java.io.*;
import java.util.*;

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
	
	public void writeAll(ArrayList<Subtitle> sb) {
		// write
		for (int i = 0; i < sb.size(); i++) 
			this.writeSRT(sb.get(i));
	}
	
	//		int d = (int) (w.getDuration() / 1000);
	//		int v = Subtitles.countVowels(w.getText()); 
	//		double vps = (double) v / (double) d;
	//		w.setText(w.getText() + "|" + d + "sec|" + v + " V's|" + String.format("%1.3f", vps));
}
