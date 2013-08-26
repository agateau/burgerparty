package com.agateau.burgerparty.utils;

import java.util.LinkedList;

public class ConnectionManager {
	public void add(Signal signal, Signal.Handler handler) {
		mConnections.add(new Connection(signal, handler));
	}

	public void disconnectAll() {
		for (Connection conn: mConnections) {
			conn.signal.disconnect(conn.handler);
		}
		mConnections.clear();
	}

	private static class Connection {
		Connection(Signal s, Signal.Handler h) {
			signal = s;
			handler = h;
		}
		Signal signal;
		Signal.Handler handler;
	}

	private LinkedList<Connection> mConnections = new LinkedList<Connection>();
}
