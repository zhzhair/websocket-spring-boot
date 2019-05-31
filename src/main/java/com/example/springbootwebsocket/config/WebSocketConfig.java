package com.example.springbootwebsocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**参考了这篇文章
 * https://blog.csdn.net/ffj0721/article/details/82630134
 */
@Configuration
public class WebSocketConfig {

	/**
	 * 自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
	 * 
	 * @return ServerEndpointExporter
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
