package ru.r2cloud.rotctrld;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.BindException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotctrldClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(RotctrldClientTest.class);

	private RotctrldMock server;
	private RotctrldClient client;

	@Test
	public void testGetPosition() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("1.0\n2.0\nRPRT 0\n");
		server.setHandler(handler);
		Position position = client.getPosition();
		assertEquals("\\get_pos", handler.getRequest());
		assertEquals(1.0, position.getAzimuth(), 0.0);
		assertEquals(2.0, position.getElevation(), 0.0);
	}

	@Test
	public void testSetPosition() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		Position position = new Position();
		position.setAzimuth(1.0);
		position.setElevation(2.0);
		client.setPosition(position);
		assertEquals("\\set_pos 1.0 2.0", handler.getRequest());
	}

	@Test(expected = RotCtrlException.class)
	public void testInvalidResponseCode() throws Exception {
		server.setHandler(new SimpleRequestHandler("RPRT 1\n"));
		client.getModelName();
	}

	@Test(expected = RotCtrlException.class)
	public void testWeirdResponseCode() throws Exception {
		server.setHandler(new SimpleRequestHandler("RPRT abc\n"));
		client.getModelName();
	}

	@Test
	public void testModelName() throws Exception {
		server.setHandler(new SimpleRequestHandler("test\n"));
		assertEquals("test", client.getModelName());
	}

	@Test
	public void testMove() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		client.move(Direction.RIGHT, 54);
		assertEquals("\\move 16 54", handler.getRequest());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidMoveSpeed() throws Exception {
		client.move(Direction.DOWN, -10);
	}

	@Test
	public void testReset() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		client.reset();
		assertEquals("\\reset 0", handler.getRequest());
	}

	@Test
	public void testResetAll() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		client.resetAll();
		assertEquals("\\reset 1", handler.getRequest());
	}

	@Test
	public void testPark() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		client.park();
		assertEquals("\\park", handler.getRequest());
	}

	@Test
	public void testStop() throws Exception {
		SimpleRequestHandler handler = new SimpleRequestHandler("RPRT 0\n");
		server.setHandler(handler);
		client.stopRotator();
		assertEquals("\\stop", handler.getRequest());
	}

	@Before
	public void start() throws IOException {
		int port = 8000;
		for (int i = 0; i < 10; i++) {
			port += i;
			server = new RotctrldMock(port);
			try {
				server.start();
				break;
			} catch (BindException e) {
				LOG.info("port: {} taken. trying new", port);
				continue;
			}
		}
		client = new RotctrldClient("127.0.0.1", port, 10000);
		client.start();
	}

	@After
	public void stop() {
		if (client != null) {
			client.stop();
		}
		if (server != null) {
			server.stop();
		}
	}

}
