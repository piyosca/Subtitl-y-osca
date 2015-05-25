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

		sc.close();

		String tgtFile = srcFile.substring(0, srcFile.lastIndexOf((int) '.')) + "_osca.srt";
		System.out.println("Target file will be " + tgtFile);
		
		SubtitleFileWriter sfw = new SubtitleFileWriter();
		sfw.open(tgtFile);
		
		ArrayList<Subtitle> subtitles = sfr.readAll();
		
		subtitles = Subtitles.mergeAllByTime(
						Subtitles.truncateAllByVowelCount(
							Subtitles.mergeAllToSentence(
								Subtitles.sliceAllBySentence(subtitles))));
		

		Subtitles.resetIdx(subtitles);
		
		sfw.writeAll(subtitles);
		
		sfw.close();
		sfr.close();
	}

}
