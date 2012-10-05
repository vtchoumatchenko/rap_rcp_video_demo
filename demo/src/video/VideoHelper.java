package video;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import demo.HostInfo;


public class VideoHelper {
	
    private static final String PREVIEW_URL = "http://%s:%d%s";

//    private static final String VIDEO_QUERY = "/media?video="; 
    private static final String VIDEO_QUERY = "/res/"; 
    private static final String PLAYER_PATH = "/res/"; 
    private static final String SOURCE_TAG = "<source src=\"%s\" type='%s' />\n"; 
    private static final String HTML_TEMPLATE = "<html><head><title>[@title@]</title><link rel=\"stylesheet\" href=\"[@playerPath@]video-js.min.css\" ><script src=\"[@playerPath@]video.min.js\"></script><script>_V_.options.flash.swf=\"[@playerPath@]video-js.swf\";</script></head><body><div class=\"video-js-box\" style=\"width:[@width@]px; height:[@height@]px; margin:0 auto;\"><video id=\"video_demo\" class=\"video-js vjs-default-skin\" controls width=\"[@width@]\" height=\"[@height@]\" autoplay preload=\"auto\" data-setup=\"{}\">[@sources@]</video></div></body></html>"; 

    private static final int VIDEO_DIALOG_VERTICAL_PADDING = 50;
    private static final int VIDEO_DIALOG_HORIZONTAL_PADDING = 25;
    private final Display display;

    public VideoHelper(final Display display) {
        this.display = display;
    }

    public void playVideo(final VideoMetadata assignedVideo) {
        Assert.isNotNull(assignedVideo);

        VideoDialog videoDialog = createVideoDialog(assignedVideo);

        String htmlForVideo = createHtmlForVideo(assignedVideo);
        videoDialog.setHTMLContent(htmlForVideo);

        videoDialog.open();
    }

    protected VideoDialog createVideoDialog(final VideoMetadata assignedVideo) {
        int dialogWidth = assignedVideo.getFrameWidth() + 2 * VIDEO_DIALOG_HORIZONTAL_PADDING;
        int dialogHeight = assignedVideo.getFrameHeight() + 2 * VIDEO_DIALOG_VERTICAL_PADDING;
        return new VideoDialog(new Shell(display), assignedVideo.getTitle(), dialogWidth, dialogHeight);
    }

    protected String createHtmlForVideo(final VideoMetadata assignedVideo) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("title", assignedVideo.getTitle()); 
        hashMap.put("width", Integer.toString(assignedVideo.getFrameWidth())); 
        hashMap.put("height", Integer.toString(assignedVideo.getFrameHeight())); 
        hashMap.put("sources", createSourcesHtml(getUrl() + VIDEO_QUERY, assignedVideo)); 
        hashMap.put("playerPath", getUrl() + PLAYER_PATH); 
        hashMap.put("videoPath", getUrl() + VIDEO_QUERY); 
        return createHtml(HTML_TEMPLATE, hashMap);
    }

    private String getUrl() {
        String host = HostInfo.getInstance().getHost();
        int port = HostInfo.getInstance().getPort();
        String context = HostInfo.getInstance().getContext();
    	return String.format(PREVIEW_URL, host, port, context);
    }

    protected String createSourcesHtml(final String videoPath, final VideoMetadata assignedVideo) {
        StringBuilder sb = new StringBuilder();
                sb.append(String.format(SOURCE_TAG, videoPath + assignedVideo.getFilename()+".m4v",
                		"video/mp4"));
                sb.append(String.format(SOURCE_TAG, videoPath + assignedVideo.getFilename()+".webm",
                		"video/webm"));               
        return sb.toString();
    }

    protected String createHtml(String template, final HashMap<String, String> hashMap) {
        for (String key : hashMap.keySet()) {
            template = template.replace("[@" + key + "@]", hashMap.get(key)); 
        }
        return template;
    }
}
