package subtitlyosca;

public class Subtitle {
	
	private int index;
	private boolean timecoded;
	private long startTime;
	private long endTime;
	private long duration;
	private String text;
	
	public Subtitle(long s, long e, String t) {
		this.index = -1;
		this.timecoded = true;
		this.startTime = Math.min(s, e);
		this.endTime = Math.max(s, e);
		this.duration = this.startTime - this.endTime;
		this.text = t;
	}

	public Subtitle() {
		this.index = -1;
		this.timecoded = false;
		this.startTime = -1;
		this.endTime = -1;
		this.duration = -1;
		this.text = null;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isTimecoded() {
		return timecoded;
	}

	public void setTimecoded(boolean timecoded) {
		this.timecoded = timecoded;
	}
	
	public long getDuration() {
		return duration;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
		this.duration = this.startTime - this.endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
		this.duration = this.startTime - this.endTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
