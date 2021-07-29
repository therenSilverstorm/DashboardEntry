package com.ta9.common.Config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);
	@Autowired
	QueueProducer producer;

	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();


	
    //Produce message after connection established
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		String ip = session.getHandshakeHeaders().get("X-Forwarded-For").toString();
		String client = session.getHandshakeHeaders().get("User-Agent").toString();
		producer.produce("User CONNECTED  with IP: " + ip + " Client info: " + client);

	}
	 //Produce message after connection closed
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		String ip = session.getHandshakeHeaders().get("X-Forwarded-For").toString();
		String client = session.getHandshakeHeaders().get("User-Agent").toString();
		producer.produce("User DISCONNECTED  with IP: " + ip + " Client info: " + client );
	}

}