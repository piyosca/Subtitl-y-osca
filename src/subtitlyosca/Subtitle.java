package subtitlyosca;

public class Subtitle {
	
	private long startTime;
	private long endTime;
	private long duration;
	private StringBuffer text;
	
	
	public Subtitle(long s, long e, StringBuffer t) {
		startTime = Math.min(s, e);
		endTime = Math.max(s, e);
		duartion = startTime - endTime;
		text = t;
	}

	public long getDuration() {
		return duration;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		if ()
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public StringBuffer getText() {
		return text;
	}

	public void setText(StringBuffer text) {
		this.text = text;
	}
	
}
