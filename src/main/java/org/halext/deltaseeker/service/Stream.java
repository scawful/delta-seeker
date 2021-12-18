package org.halext.deltaseeker.service;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class Stream {

    Session userSession = null;
    private MessageHandler messageHandler;

    /**
     * Stream Class for WebSocket streaming
     * 
     * @param endpointURI
     * @throws DeploymentException
     * @throws IOException
     */
    public Stream(URI endpointURI) throws DeploymentException, IOException {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("Closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            System.out.println("Client msg: " + message);
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * Callback hook for Message Events as ByteBuffer
     * @param bytes
     */
   @OnMessage
   public void onMessage(ByteBuffer bytes) {
        System.out.println("Handle byte buffer");
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     */
    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}