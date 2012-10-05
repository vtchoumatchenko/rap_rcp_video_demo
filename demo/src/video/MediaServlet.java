package video;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This code is borrowed from Tomcat's
 * org.apache.catalina.servlets.DefaultServlet.java
 */

public class MediaServlet extends HttpServlet {

	private static final long serialVersionUID = -1686884122100446910L;

	/** Query parameter - the name of the video file */
	private static final String PARAM_VIDEO = "video"; 
	private static final String VIDEO_DIR = "c:/video/";

	
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		String videoFileName = req.getParameter(PARAM_VIDEO);
		File videoFile = new File(VIDEO_DIR + videoFileName);

		serveResource(req, resp, videoFile);

	}

	/**
	 * Full range marker.
	 */
	protected static ArrayList<Range> FULL = new ArrayList<Range>();
	/**
	 * The output buffer size to use when serving resources.
	 */
	protected int output = 2048;
	/**
	 * The input buffer size to use when serving resources.
	 */
	protected int input = 2048;

	/**
	 * Serve the specified resource, optionally including the data content.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet-specified error occurs
	 */
	protected void serveResource(final HttpServletRequest request,
			final HttpServletResponse response, final File file)
			throws IOException, ServletException {

		// Find content type.
		// String contentType = getServletContext().getMimeType(file.getName());
		String contentType = getMimeType(file.getName());

		ArrayList<Range> ranges = null;
		long contentLength = -1L;

		// Accept ranges header
		response.setHeader("Accept-Ranges", "bytes");

		// ETag header
		// response.setHeader("ETag", cacheEntry.attributes.getETag());

		// Last-Modified header
		response.setHeader("Last-Modified", Long.toString(file.lastModified()));

		// Get content length
		contentLength = file.length();
		// Special case for zero length files, which would cause a
		// (silent) ISE when setting the output buffer size
		// if (contentLength == 0L) {
		// serveContent = false;
		// }

		// Parse range specifier
		ranges = parseRange(request, response, contentLength);

		FileInputStream fin = new FileInputStream(file);
		ServletOutputStream ostream = response.getOutputStream();

		if ((((ranges == null) || (ranges.isEmpty())) && (request
				.getHeader("Range") == null)) || (ranges == FULL)) {

			// Set the appropriate output headers
			if (contentType != null) {
				response.setContentType(contentType);
			}
			if ((contentLength >= 0)) {
				if (contentLength < Integer.MAX_VALUE) {
					response.setContentLength((int) contentLength);
				} else {
					// Set the content-length as String to be able to use a long
					response.setHeader("content-length", "" + contentLength);
				}
			}

			// Copy the input stream to our output stream
			try {
				response.setBufferSize(output);
			} catch (IllegalStateException e) {
				// Silent catch
			}
			copy(fin, ostream);
		} else {

			if ((ranges == null) || (ranges.isEmpty())) {
				return;
			}

			// Partial content response.
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

			if (ranges.size() == 1) {
				Range range = ranges.get(0);
				response.addHeader("Content-Range", "bytes " + range.start
						+ "-" + range.end + "/" + range.length);
				long length = range.end - range.start + 1;
				if (length < Integer.MAX_VALUE) {
					response.setContentLength((int) length);
				} else {
					// Set the content-length as String to be able to use a long
					response.setHeader("content-length", "" + length);
				}

				if (contentType != null) {
					response.setContentType(contentType);
				}

				try {
					response.setBufferSize(output);
				} catch (IllegalStateException e) {
					// Silent catch
				}
				copy(fin, ostream, range);

			} else {
				// response.setContentType("multipart/byteranges; boundary=" +
				// mimeSeparation);

				// if (serveContent) {
				// try {
				// response.setBufferSize(output);
				// } catch (IllegalStateException e) {
				// // Silent catch
				// }
				// if (ostream != null) {
				// copy(cacheEntry, ostream, ranges.iterator(), contentType);
				// } else {
				// copy(cacheEntry, writer, ranges.iterator(), contentType);
				// }
				// }

			}

		}

	}

	private String getMimeType(final String name) {
		String lowerName = name.toLowerCase();
		if (lowerName.endsWith(".mp4") || lowerName.endsWith(".m4v")) {
			return "video/mp4";
		} else if (lowerName.endsWith(".webm")) {
			return "video/webm";
		}
		return null;
	}

	/**
	 * Copy the contents of the specified input stream to the specified output
	 * stream, and ensure that both streams are closed before returning (even in
	 * the face of an exception).
	 * 
	 * @param cacheEntry
	 *            The cache entry for the source resource
	 * @param is
	 *            The input stream to read the source resource from
	 * @param ostream
	 *            The output stream to write to
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	protected void copy(final InputStream is, final ServletOutputStream ostream)
			throws IOException {

		IOException exception = null;

		InputStream istream = new BufferedInputStream(is, input);

		// Copy the input stream to the output stream
		exception = copyRange(istream, ostream);

		// Clean up the input stream
		istream.close();

		// Rethrow any exception that has occurred
		if (exception != null) {
			throw exception;
		}

	}

	/**
	 * Copy the contents of the specified input stream to the specified output
	 * stream, and ensure that both streams are closed before returning (even in
	 * the face of an exception).
	 * 
	 * @param cacheEntry
	 *            The cache entry for the source resource
	 * @param ostream
	 *            The output stream to write to
	 * @param range
	 *            Range the client wanted to retrieve
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	protected void copy(final InputStream is,
			final ServletOutputStream ostream, final Range range)
			throws IOException {

		IOException exception = null;

		InputStream istream = new BufferedInputStream(is, input);
		exception = copyRange(istream, ostream, range.start, range.end);

		// Clean up the input stream
		istream.close();

		// Rethrow any exception that has occurred
		if (exception != null) {
			throw exception;
		}

	}

	/**
	 * Copy the contents of the specified input stream to the specified output
	 * stream, and ensure that both streams are closed before returning (even in
	 * the face of an exception).
	 * 
	 * @param istream
	 *            The input stream to read from
	 * @param ostream
	 *            The output stream to write to
	 * @return Exception which occurred during processing
	 */
	protected IOException copyRange(final InputStream istream,
			final ServletOutputStream ostream) {

		// Copy the input stream to the output stream
		IOException exception = null;
		byte buffer[] = new byte[input];
		int len = buffer.length;
		while (true) {
			try {
				len = istream.read(buffer);
				if (len == -1) {
					break;
				}
				ostream.write(buffer, 0, len);
			} catch (IOException e) {
				exception = e;
				len = -1;
				break;
			}
		}
		return exception;

	}

	/**
	 * Copy the contents of the specified input stream to the specified output
	 * stream, and ensure that both streams are closed before returning (even in
	 * the face of an exception).
	 * 
	 * @param istream
	 *            The input stream to read from
	 * @param ostream
	 *            The output stream to write to
	 * @param start
	 *            Start of the range which will be copied
	 * @param end
	 *            End of the range which will be copied
	 * @return Exception which occurred during processing
	 */
	protected IOException copyRange(final InputStream istream,
			final ServletOutputStream ostream, final long start, final long end) {
		try {
			istream.skip(start);
		} catch (IOException e) {
			return e;
		}

		IOException exception = null;
		long bytesToRead = end - start + 1;

		byte buffer[] = new byte[input];
		int len = buffer.length;
		while ((bytesToRead > 0) && (len >= buffer.length)) {
			try {
				len = istream.read(buffer);
				if (bytesToRead >= len) {
					ostream.write(buffer, 0, len);
					bytesToRead -= len;
				} else {
					ostream.write(buffer, 0, (int) bytesToRead);
					bytesToRead = 0;
				}
			} catch (IOException e) {
				exception = e;
				len = -1;
			}
			if (len < buffer.length) {
				break;
			}
		}

		return exception;

	}

	/**
	 * Parse the range header.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 * @return Vector of ranges
	 */
	protected ArrayList<Range> parseRange(final HttpServletRequest request,
			final HttpServletResponse response, final long fileLength)
			throws IOException {

		if (fileLength == 0) {
			return null;
		}

		// Retrieving the range header (if any is specified
		String rangeHeader = request.getHeader("Range");

		if (rangeHeader == null) {
			return null;
		}
		// bytes is the only range unit supported (and I don't see the point
		// of adding new ones).
		if (!rangeHeader.startsWith("bytes")) {
			response.addHeader("Content-Range", "bytes */" + fileLength);
			response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			return null;
		}

		rangeHeader = rangeHeader.substring(6);

		// Vector which will contain all the ranges which are successfully
		// parsed.
		ArrayList<Range> result = new ArrayList<Range>();
		StringTokenizer commaTokenizer = new StringTokenizer(rangeHeader, ",");

		// Parsing the range list
		while (commaTokenizer.hasMoreTokens()) {
			String rangeDefinition = commaTokenizer.nextToken().trim();

			Range currentRange = new Range();
			currentRange.length = fileLength;

			int dashPos = rangeDefinition.indexOf('-');

			if (dashPos == -1) {
				response.addHeader("Content-Range", "bytes */" + fileLength);
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				return null;
			}

			if (dashPos == 0) {

				try {
					long offset = Long.parseLong(rangeDefinition);
					currentRange.start = fileLength + offset;
					currentRange.end = fileLength - 1;
				} catch (NumberFormatException e) {
					response.addHeader("Content-Range", "bytes */" + fileLength);
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return null;
				}

			} else {

				try {
					currentRange.start = Long.parseLong(rangeDefinition
							.substring(0, dashPos));
					if (dashPos < rangeDefinition.length() - 1) {
						currentRange.end = Long.parseLong(rangeDefinition
								.substring(dashPos + 1,
										rangeDefinition.length()));
					} else {
						currentRange.end = fileLength - 1;
					}
				} catch (NumberFormatException e) {
					response.addHeader("Content-Range", "bytes */" + fileLength);
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return null;
				}

			}

			if (!currentRange.validate()) {
				response.addHeader("Content-Range", "bytes */" + fileLength);
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				return null;
			}

			result.add(currentRange);
		}

		return result;
	}

	// ------------------------------------------------------ Range Inner Class

	protected class Range {

		public long start;
		public long end;
		public long length;

		/**
		 * Validate range.
		 */
		public boolean validate() {
			if (end >= length) {
				end = length - 1;
			}
			return ((start >= 0) && (end >= 0) && (start <= end) && (length > 0));
		}

		public void recycle() {
			start = 0;
			end = 0;
			length = 0;
		}

	}
}