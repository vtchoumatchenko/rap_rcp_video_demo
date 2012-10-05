package video;

public class VideoMetadata {

	private final String title;
	private final String filename;
	private final int frameWidth;
	private final int frameHeight;

	public VideoMetadata(String title, String filename, int frameWidth,
			int frameHeight) {
		this.title = title;
		this.filename = filename;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public String getTitle() {
		return title;
	}

	public String getFilename() {
		return filename;
	}
}
