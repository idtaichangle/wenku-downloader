package com.cvnavi.downloader.web.ws;


import com.cvnavi.downloader.Document;
import com.cvnavi.downloader.common.DownloadTask;
import com.cvnavi.downloader.common.DownloaderCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/downloader-ws")
@Log4j2
public class WebSocketServer{
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    protected static Map<Integer, WebSocketServer> webSocketMap = new ConcurrentHashMap<Integer, WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    public static DownloaderCallback callback=new DownloaderCallback() {
        @Override
        public void metaReady(DownloadTask task, Document.Meta meta) {
            WebSocketServer server=webSocketMap.get(task.getId());
            if(server!=null){
                try {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("action","fetch_meta");
                    map.put("success",true);
                    map.put("meta",meta);
                    server.sendMessage(toJsonStr(map));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }

        @Override
        public void downloadFinish(DownloadTask task, boolean success,String fileName) {
            WebSocketServer server=webSocketMap.get(task.getId());
            if(server!=null){
                try {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("action","download");
                    map.put("success",success);
                    map.put("fileName",fileName);
                    server.sendMessage(toJsonStr(map));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    };

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        String sessionId=session.getId();
        int id=Integer.parseInt((session.getRequestParameterMap().get("id").get(0)));
        webSocketMap.put(id,this);

        log.debug("有新连接加入！session id:"+sessionId+".当前在线人数为" + webSocketMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        int id=Integer.parseInt((session.getRequestParameterMap().get("id").get(0)));
        WebSocketServer server=webSocketMap.get(id);
        if(server!=null){
            webSocketMap.remove(id);
        }
        log.debug("有一连接关闭！当前在线人数为" + webSocketMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
    }

    /**
     *
     * @param session
     * @param e
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error(e.getMessage(), e);
//        error.printStackTrace();
    }


    public synchronized void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    public static  String toJsonStr(HashMap<String,Object> map){
        ObjectMapper mapper=new ObjectMapper();
        String json= "";
        try {
            json = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
        return json;
    }
}
