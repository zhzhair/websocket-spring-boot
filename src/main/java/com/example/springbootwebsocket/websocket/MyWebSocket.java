package com.example.springbootwebsocket.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

/**
 * 聊天功能
 */
@ServerEndpoint(value = "/websocket/{sid}")
@Component
public class MyWebSocket {

	private static Map<String, Session> sessionPool = new ConcurrentHashMap<>();//session池
	private static Map<String, String> sessionId = new ConcurrentHashMap<>();//用户id的集合

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "sid") String userid) {
		this.session = session;
		sessionPool.put(userid,session);
		sessionId.put(session.getId(),userid);
		System.out.println("有新连接加入！当前在线人数为 : " + sessionId.size());
		try {
			this.session.getBasicRemote().sendText("您已成功连接！");
		} catch (IOException e) {
			System.out.println("IO异常");
		}
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		this.session = session;
		System.out.println(sessionId.get(session.getId()) + ": 来自客户端的消息:" + message);
		try {
			for (String userId : sessionPool.keySet()) {
				sessionPool.get(userId).getBasicRemote().sendText(sessionId.get(session.getId()) + "说: " + message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		sessionPool.remove(sessionId.get(session.getId()));
		sessionId.remove(session.getId());
		System.out.println("有一连接关闭！当前在线人数为 : " + sessionId.size());
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		this.session = session;
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 信息群发
	 *		-----可以Controller调用
	 * @param msg msg
	 * @param userId userId
	 */
	public void sendAll(String msg,String userId) {
		for (String key : sessionPool.keySet()) {
			if (!userId.equals(sessionId.get(key))) {
				try {
					sessionPool.get(key).getBasicRemote().sendText(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
