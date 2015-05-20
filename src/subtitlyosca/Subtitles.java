package subtitlyosca;

public class Subtitles {

	public static long gapBetween(Subtitle first, Subtitle second) {
		if (first.getStartTime() < second.getStartTime()) {
			return second.getStartTime() - first.getEndTime();
		} else {
			return first.getStartTime() - second.getEndTime();
		}
	}
	
	public static void merge(Subtitle first, Subtitle second, long gapLimit) {
		
	}
}
