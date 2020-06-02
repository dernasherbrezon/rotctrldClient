package ru.r2cloud.rotctrld;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotctrldClient {

	private static final String RESPONSE_STATUS = "RPRT ";

	private static final Logger LOG = LoggerFactory.getLogger(RotctrldClient.class);

	private final String hostname;
	private final int port;
	private final int timeout;

	private Socket socket;

	public RotctrldClient(String hostname, int port, int timeout) {
		this.hostname = hostname;
		this.port = port;
		this.timeout = timeout;
	}

	public Position getPosition() throws RotCtrlException, IOException {
		List<String> response = sendRequest("\\get_pos", 2);
		Position result = new Position();
		result.setAzimuth(Double.parseDouble(response.get(0)));
		result.setElevation(Double.parseDouble(response.get(1)));
		return result;
	}

	public void setPosition(Position position) throws RotCtrlException, IOException {
		sendCommand("\\set_pos " + position.getAzimuth() + " " + position.getElevation());
	}

	public void move(Direction direction, int speed) throws RotCtrlException, IOException {
		if (speed < 1 || speed > 100) {
			throw new IllegalArgumentException("invalid speed value: " + speed);
		}
		sendCommand("\\move " + direction.getCode() + " " + speed);
	}

	public void stopRotator() throws RotCtrlException, IOException {
		sendCommand("\\stop");
	}

	public void park() throws RotCtrlException, IOException {
		sendCommand("\\park");
	}

	public void reset() throws RotCtrlException, IOException {
		sendCommand("\\reset 0");
	}

	public void resetAll() throws RotCtrlException, IOException {
		sendCommand("\\reset 1");
	}

	public String getModelName() throws RotCtrlException, IOException {
		List<String> values = sendRequest("\\get_info", 1);
		return values.get(0);
	}

	private void sendCommand(String request) throws RotCtrlException, IOException {
		// 1 is for response status
		sendRequest(request, 1);
	}

	private List<String> sendRequest(String request, int numberOfLinesExpected) throws RotCtrlException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("request: {}", request);
		}
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write((request + "\n").getBytes(StandardCharsets.ISO_8859_1));
		outputStream.flush();
		BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
		List<String> result = new ArrayList<>();
		for (int i = 0; i < numberOfLinesExpected; i++) {
			String curLine = r.readLine();
			if (curLine == null) {
				throw new EOFException();
			}
			if (curLine.startsWith(RESPONSE_STATUS)) {
				int statusCode = readStatusCode(curLine);
				if (statusCode != 0) {
					throw new RotCtrlException(statusCode, "unable to run: " + request);
				}
				break;
			}
			result.add(curLine);
		}
		return result;
	}

	private static int readStatusCode(String line) {
		String code = line.substring(RESPONSE_STATUS.length());
		try {
			return Integer.parseInt(code.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public void start() throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(hostname, port), timeout);
		socket.setSoTimeout(timeout);
	}

	public void stop() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				LOG.error("unable to close socket", e);
			}
		}
	}
}
