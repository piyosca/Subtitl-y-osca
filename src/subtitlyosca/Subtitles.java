package subtitlyosca;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.*;

public class Subtitles {

	private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U', 'a', 'e', 'i', 'o', 'u'};
	
	public static long gapBetween(Subtitle first, Subtitle second) {
		if (first.getStartTime() < second.getStartTime()) {
			return second.getStartTime() - first.getEndTime();
		} else {
			return first.getStartTime() - second.getEndTime();
		}
	}
	
	// slice target Subtitle object at punctuation marks 
	public static Subtitle[] sliceAtPunctuations(Subtitle sb) {
		String sourceText = sb.getText(); 
		long sourceDuration = sb.getDuration();
		long sourceVowelCount = countVowels(sourceText);

		// split into sentences and create Subtitle objects
		BreakIterator bi = BreakIterator.getSentenceInstance();
		bi.setText(sourceText);
		int splitIndex = 0;
		
		ArrayList<Subtitle> sbHolder = new ArrayList<Subtitle>(); 
			
		long currentStartPos = sb.getStartTime();
		
		while (bi.next() != BreakIterator.DONE) {
			String sentence = sourceText.substring(splitIndex, bi.current()).trim();
			splitIndex = bi.current();
			long sliceDuration = sourceDuration * countVowels(sentence) / sourceVowelCount;
			if (sliceDuration == 0) sliceDuration += 500;
			sbHolder.add(new Subtitle(currentStartPos, currentStartPos + sourceDuration, sentence));
			currentStartPos += sliceDuration;
		}
		
		sbHolder.get(sbHolder.size() - 1).setEndTime(sb.getEndTime());

		return (Subtitle[]) sbHolder.toArray();
	}
	
	// merge Subtitle objects
	public static Subtitle merge(Subtitle s1, Subtitle s2, long minimumGap) {
		if (gapBetween(s1, s2) > minimumGap) return null;
		Subtitle ret = new Subtitle();
		ret.setText(s1.getText() + " " + s2.getText());
		ret.setStartTime(Math.min(s1.getStartTime(), s2.getStartTime()));
		ret.setEndTime(Math.max(s1.getEndTime(), s2.getEndTime()));
		ret.setTimecoded(true);
		return ret;
	}
	
	public static int countVowels(String s) {
		int ret = 0;
		for (int i = 0; i < s.length(); i++) {
			for (int j = 0; j < VOWELS.length; j++) {
				if (VOWELS[j] == s.charAt(i)) {
					ret++;
					break;
				}
			}
		}
		return ret;
	}
	
	public static long decodeTC(String tc) {
		Pattern digit = Pattern.compile("\\d+");
		Matcher digitM = digit.matcher(tc);
		ArrayList<Integer> values = new ArrayList<Integer>();
		while (digitM.find()) {
			values.add(Integer.parseInt(digitM.group()));
		}
		return (values.get(0) * 3600 * 1000) + (values.get(1) * 60 * 1000) + (values.get(2) * 1000) + values.get(3);
	}
	
	public static String encodeTC(long time) {
		long hour = time / (3600 * 1000);
		time = time % (3600 * 1000);
		long minute = time / (60 * 1000);
		time = time % (60 * 1000);
		long sec = time / 1000;
		long ms = time % 1000;
		String ret = String.format("%d:%02d:%02d.%03d", hour, minute, sec, ms);
		return ret;
	}
}
