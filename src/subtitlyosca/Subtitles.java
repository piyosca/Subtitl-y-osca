package subtitlyosca;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.*;

public class Subtitles {

	public static int TOO_LONG = 36; // vowel count to be halved
	public static int TOO_SHORT = 1000; // millisecond count to be merged;
	
	private static final char[] VOWELS = {'A', 'E', 'I', 'O', 'U', 'a', 'e', 'i', 'o', 'u'};
	private static final char[] PUNCTUATIONS = {'.', '!', '?'};
	
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
			sbHolder.add(new Subtitle(currentStartPos, currentStartPos + sliceDuration, sentence));
			currentStartPos += sliceDuration;
		}
		
		sbHolder.get(sbHolder.size() - 1).setEndTime(sb.getEndTime());

		return (Subtitle[]) sbHolder.toArray(new Subtitle[sbHolder.size()]);
	}
	
	public static Subtitle[] sliceWhereTooLong(Subtitle sb) {
		ArrayList<Subtitle> sbHolder = new ArrayList<Subtitle>();
		int srcVowelCnt = countVowels(sb.getText());
		
		// do nothing if it's not too long
		if (srcVowelCnt < TOO_LONG) {
			sbHolder.add(sb);
			return (Subtitle[]) sbHolder.toArray(new Subtitle[sbHolder.size()]);
		}
		
		// determine slice number and size
		int sliceNum = (srcVowelCnt / TOO_LONG); // actual number of returned subtitles will be sliceNum + 1;
		int sliceSize = srcVowelCnt / (sliceNum + 1);
		
		int crtPos = 0;
		int nextPos = 0;
		ArrayList<String> strHolder = new ArrayList<String>();
		
		for (int i = 0; i < sliceNum; i++) {
			nextPos = sliceByVowelCount(sb.getText(), sliceSize, crtPos);
			strHolder.add(sb.getText().substring(crtPos, nextPos).trim()); // .concat(" (&)") after trim() if needed
			crtPos = nextPos;
		}
		
		String stub = sb.getText().substring(crtPos, sb.getText().length()).trim();
		if (stub != "") strHolder.add(stub);
		
		long crtStart = sb.getStartTime();
		long crtEnd = 0;
		
		for (int i = 0; i < strHolder.size(); i++) {
			crtEnd = crtStart + (sb.getDuration() * countVowels(strHolder.get(i))) / srcVowelCnt;
			sbHolder.add(new Subtitle(crtStart, crtEnd, strHolder.get(i)));
			crtStart = crtEnd;
		}
		
		sbHolder.get(sbHolder.size() - 1).setEndTime(sb.getEndTime());
		return (Subtitle[]) sbHolder.toArray(new Subtitle[sbHolder.size()]);
	}
	
	public static boolean hasFullSentence(Subtitle sb) {
		String text = sb.getText();
		boolean ret = false;
		for (int i = 0; i < PUNCTUATIONS.length; i++) {
			if (text.charAt(text.length() - 1) == PUNCTUATIONS[i]) {
				ret = true;
				break;
			}				
		}
		return ret;
	}
	
	// merge Subtitle objects
	public static Subtitle merge(Subtitle s1, Subtitle s2) {
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
	
	public static int sliceByVowelCount(String s, int vc, int from) {
		if (countVowels(s.substring(from)) <= vc) return s.length();
		int currentVC = 0;
		int srchB = 0;
		for (int i = from; i < s.length(); i++) {
			for (int j = 0; j < VOWELS.length; j++) {
				if (VOWELS[j] == s.charAt(i)) {
					currentVC++;
					break;
				}
			}
			if (currentVC >= vc) {
				srchB = i;
				break;
			}
		}
		
		int backward = srchB;
		while (s.charAt(backward) != ' ' && backward >= from) {
			backward--;
		}
		
		int forward = srchB;
		while (s.charAt(forward) != ' ' && forward <= s.length()) {
			forward++;
		}
		
		if (Math.abs(srchB - forward) <= Math.abs(srchB - backward)) {
			return forward;
		} else {
			return backward;
		}		
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
		String ret = String.format("%d:%02d:%02d,%03d", hour, minute, sec, ms);
		return ret;
	}
}
