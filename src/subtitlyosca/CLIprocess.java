package subtitlyosca;

import java.util.*;
import java.io.*;

public class CLIprocess {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		SubtitleFileReader sfr = new SubtitleFileReader();
		boolean opened = false;
		String srcFile = "";
		while (!opened) {
			try {
				System.out.print("Specify source file: ");
				srcFile = sc.nextLine();
				sfr.open(srcFile);
				opened = true;
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found.");
			}
		}
		
		String tgtFile = srcFile.substring(0, srcFile.lastIndexOf((int) '.')) + "_osca.srt";
		System.out.println("Target file will be " + tgtFile);
		
		SubtitleFileWriter sfw = new SubtitleFileWriter();
		sfw.open(tgtFile);
		
		ArrayList<Subtitle> srcSbt = new ArrayList<Subtitle>();
		
		Subtitle t;
		int idx = 1;
		
		while ((t = sfr.readNext()) != null) {
			t.setIndex(idx++);
			srcSbt.add(t);
		}
		
		System.out.println("Loading complete.");
		
		// slice each subtitles by sentence

		ArrayList<Subtitle> prc1Sbt = new ArrayList<Subtitle>();
		for (int i = 0; i < srcSbt.size(); i++) {
			Subtitle[] tSBs = Subtitles.sliceAtPunctuations(srcSbt.get(i));
			for (int j = 0; j < tSBs.length; j++) {
				prc1Sbt.add(tSBs[j]);
			}
		}
		
		System.out.println("Sentence slicing complete.");
		
		// merge to match sentences and subtitles
		
		ArrayList<Subtitle> prc2Sbt = new ArrayList<Subtitle>();
		int cnt = 0;
		while (cnt < prc1Sbt.size()) {
			Subtitle m = prc1Sbt.get(cnt);
			while ((!Subtitles.hasFullSentence(m)) && cnt < prc1Sbt.size() - 1) {
				if (Subtitles.gapBetween(m, prc1Sbt.get(cnt + 1)) > 1000) break;
				m = Subtitles.merge(m, prc1Sbt.get(++cnt));
			}
			prc2Sbt.add(m);
			cnt++;
		}
		
		System.out.println("Sentence-subtitle matching complete.");
		
		// slice where the text is too long
		
		ArrayList<Subtitle> prc3Sbt = new ArrayList<Subtitle>();
		cnt = 0;
		for (int i = 0; i < prc2Sbt.size(); i++) {
			Subtitle[] tSBs = Subtitles.sliceWhereTooLong(prc2Sbt.get(i));
			for (int j = 0; j < tSBs.length; j++) {
				prc3Sbt.add(tSBs[j]);
			}
		}

		System.out.println("Truncating complete.");
		
		// merge where the time is too short
		
		ArrayList<Subtitle> prc4Sbt = new ArrayList<Subtitle>();
		cnt = 0;
		while (cnt < prc3Sbt.size()) {
			Subtitle m = prc3Sbt.get(cnt);
			while ((m.getDuration() < Subtitles.TOO_SHORT) && (cnt < prc3Sbt.size() - 1)) {
				if (Subtitles.gapBetween(m, prc3Sbt.get(cnt + 1)) > 1000) break;
				m = Subtitles.merge(m, prc3Sbt.get(++cnt));
			}
			prc4Sbt.add(m);
			cnt++;
		}	

		System.out.println("Merging of stubs complete.");

		// write
		for (int i = 0; i < prc4Sbt.size(); i++) {
			Subtitle w = prc4Sbt.get(i);
//			int d = (int) (w.getDuration() / 1000);
//			int v = Subtitles.countVowels(w.getText()); 
//			double vps = (double) v / (double) d;
//			w.setText(w.getText() + "|" + d + "sec|" + v + " V's|" + String.format("%1.3f", vps));
			w.setIndex(i + 1);
			sfw.writeSRT(w);
		}
		
		sfw.close();
		sfr.close();
		sc.close();
	}

}
