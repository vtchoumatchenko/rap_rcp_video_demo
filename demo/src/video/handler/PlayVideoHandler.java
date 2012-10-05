package video.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import video.VideoHelper;
import video.VideoMetadata;

public class PlayVideoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		VideoHelper videoHelper = new VideoHelper(HandlerUtil.getActiveShell(event).getDisplay());
		VideoMetadata assignedVideo = getVideoMetadata();
		videoHelper.playVideo(assignedVideo);
		return null;
	}

	private VideoMetadata getVideoMetadata() {
		return new VideoMetadata("Demo Video", "demo", 720, 400);
	}
}
