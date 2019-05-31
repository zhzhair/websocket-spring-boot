package com.example.springbootwebsocket.websocket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/systemWebsocket")
@Component
public class SystemWebSocket {

	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<SystemWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	private String message;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
		System.out.println("有新连接加入！当前在线人数为 : " + getOnlineCount());
		try {
			sendMessage("您已成功连接！");
		} catch (IOException e) {
			System.out.println("IO异常");
		}
	}

	@Scheduled(cron = "0/10 * * * * ?")//2秒钟执行一次
	public void circle(){
		onMessage(message,session);
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		this.message = message;
		this.session = session;
		System.out.println("来自客户端的消息:" + message);
		try {
			Date date = new Date(System.currentTimeMillis());
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
			long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
			long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
			int threadCount = Runtime.getRuntime().availableProcessors();
			this.message = (message == null?"":message + ":") + "<div>推送消息的时间: " + time + "</div>"
					+"<div>总内存: " + totalMemory + "kb</div>"
					+"<div>剩余内存: " + freeMemory + "kb</div>"
					+"<div>最大可用内存: " + maxMemory + "kb</div>"
					+"<div>服务器线程数: " + threadCount + "</div>";
			for (SystemWebSocket item : webSocketSet) {
				item.sendMessage(this.message + "//2秒钟执行一次<div></div>");
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
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		System.out.println("有一连接关闭！当前在线人数为 : " + getOnlineCount());
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

	private void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
		// this.session.getAsyncRemote().sendText(message);
	}

	private static synchronized int getOnlineCount() {
		return onlineCount;
	}

	private static synchronized void addOnlineCount() {
		SystemWebSocket.onlineCount++;
	}

	private static synchronized void subOnlineCount() {
		SystemWebSocket.onlineCount--;
	}
}
