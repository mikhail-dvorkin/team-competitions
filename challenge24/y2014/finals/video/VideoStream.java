package challenge24.y2014.finals.video;

import java.io.*;
import java.net.*;

public class VideoStream {
	private static final String CONTENT_LENGTH = "Content-Length: ";
	private static final String CONTENT_TYPE = "Content-Type: image/jpeg";

	private InputStream stream;
	private StringWriter stringWriter;

	public VideoStream(String url_name) throws IOException {
		URL url = new URL(url_name);
		URLConnection urlConn = url.openConnection();
		urlConn.setReadTimeout(3000);
		urlConn.connect();
		stream = urlConn.getInputStream();
		stringWriter = new StringWriter(128);
	}

	/**
	 * Using the <i>urlStream</i> get the next JPEG image as a byte[]
	 * 
	 * @return byte[] of the JPEG
	 * @throws IOException
	 */
	public byte[] retrieveNextImage() throws IOException {
		boolean haveHeader = false;
		int currByte = -1;

		String header = null;
		// build headers
		// the DCS-930L stops it's headers
		while ((currByte = stream.read()) > -1 && !haveHeader) {
			stringWriter.write(currByte);

			String tempString = stringWriter.toString();

			int indexOf = tempString.indexOf(CONTENT_TYPE);
			if (indexOf > 0) {
				haveHeader = true;
			}
		}

		// 255 indicates the start of the jpeg image
		while ((currByte = stream.read()) != 255) {
			stringWriter.write(currByte);
			// just skip extras
		}

		header = stringWriter.toString();

		// rest is the buffer
		int contentLength = contentLength(header);
		byte[] imageBytes = new byte[contentLength + 1];
		// since we ate the original 255 , shove it back in
		imageBytes[0] = (byte) 255;
		int offset = 1;
		int numRead = 0;
		while (offset < imageBytes.length
				&& (numRead = stream.read(imageBytes, offset, imageBytes.length
						- offset)) >= 0) {
			offset += numRead;
		}

		stringWriter = new StringWriter(128);

		return imageBytes;
	}

	private static int contentLength(String header) {
		int indexOfContentLength = header.indexOf(CONTENT_LENGTH);
		int valueStartPos = indexOfContentLength + CONTENT_LENGTH.length();
		int indexOfEOL = header.indexOf('\n', indexOfContentLength);

		String lengthValStr = header.substring(valueStartPos, indexOfEOL)
				.trim();

		int retValue = Integer.parseInt(lengthValStr);

		return retValue;
	}
}
