package com.ta9.common.Config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.net.URLDecoder;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);
	@Autowired
	QueueProducer producer;

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	// Produce message after connection established
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);

		// String ip = session.getHandshakeHeaders().get("X-Forwarded-For").toString();
		// String client = session.getHandshakeHeaders().get("User-Agent").toString();

		Map<String, List<String>> queryParams = splitQuery(session.getUri());

		String ip = queryParams.get("X-Forwarded-For").toString();
		String client = queryParams.get("User-Agent").toString();
		 producer.produce("User CONNECTED with IP: " + ip + " Client info: " +
		 client);
		System.out.println(session.getUri());

	}

	// Produce message after connection closed
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		// String ip = session.getHandshakeHeaders().get("X-Forwarded-For").toString();
		// String client = session.getHandshakeHeaders().get("User-Agent").toString();
		Map<String, List<String>> queryParams = splitQuery(session.getUri());

		String ip = queryParams.get("X-Forwarded-For").toString();
		String client = queryParams.get("User-Agent").toString();
		 producer.produce("User DISCONNECTED with IP: " + ip + " Client info: " +
		 client);
	}

	public static Map<String, List<String>> splitQuery(URI uri) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = uri.getQuery().split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1
					? URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
					: null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}

}