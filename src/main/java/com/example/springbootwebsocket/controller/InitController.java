package com.example.springbootwebsocket.controller;

import com.example.springbootwebsocket.websocket.MyWebSocket;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class InitController {

	@Resource
	private MyWebSocket myWebSocket;

	@RequestMapping(value = "websocket",method = RequestMethod.GET)
	public String init1() {
		return "websocket.html";
	}

	@RequestMapping(value = "system",method = RequestMethod.GET)
	public String init2() {
		return "system.html";
	}

	@RequestMapping(value = "/sendToAll",method = {RequestMethod.GET})
	@ResponseBody
	public void sendToAll(){
		myWebSocket.sendAll("mygod","1");
	}

}
