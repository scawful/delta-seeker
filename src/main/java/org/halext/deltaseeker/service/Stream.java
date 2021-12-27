package org.halext.deltaseeker.service;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.RemoteEndpoint.Async;

@ClientEndpoint
public class Stream {

    Session userSession = null;
    private MessageHandler messageHandler; 
    Async asyncPeerConnection;
    String loginRequest;

    private final LinkedList<String> messagesToSend = new LinkedList<String>();

    /**
     * If this client is currently sending a messages asynchronously.
     */
    private volatile boolean isSendingMessage = false;

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
            container.setAsyncSendTimeout(1000);
            container.setDefaultMaxSessionIdleTimeout(10000);
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
        this.userSession.getUserProperties().put("USERNAME", "DELTASEEKER");
        this.asyncPeerConnection = this.userSession.getAsyncRemote();
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        String peer = (String) userSession.getUserProperties().get("USERNAME");
        CloseReason.CloseCode closeReasonCode = reason.getCloseCode();
        String closeReasonMsg = reason.getReasonPhrase();
        System.out.println("User "+ peer + " disconnected. Code: "+ closeReasonCode + ", Message: "+ closeReasonMsg);
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
            // System.out.println("Client msg: " + message);
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
        // Future<Void> deliveryTracker = userSession.getAsyncRemote().sendText(message);
        // deliveryTracker.isDone(); //blocks
        synchronized (messagesToSend) {
            if (isSendingMessage) {
                messagesToSend.add(message);
            } else {
                isSendingMessage = true;
                internalSendMessageAsync(message);
            }
        }
    }

    /**
     * Message handler.
     */
    public static interface MessageHandler {
        public void handleMessage(String message);
    }
    
    /**
     * Internally sends the messages asynchronously.
     * @param msg
     */
    private void internalSendMessageAsync(String message) {
        try {
            asyncPeerConnection.sendText(message, sendHandler);
        } catch (IllegalStateException ex) {
            // Trying to write to the client when the session has
            // already been closed.
            // Ignore
        }
    }

    /**
     * SendHandler that will continue to send buffered messages.
     */
    private final SendHandler sendHandler = new SendHandler() {
        @Override
        public void onResult(SendResult result) {
            if (!result.isOK()) {
                try {
                    userSession.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
            synchronized (messagesToSend) {
                if (!messagesToSend.isEmpty()) {
                    String msg = messagesToSend.remove();
                    internalSendMessageAsync(msg);
                } else {
                    isSendingMessage = false;
                }

            }
        }
    };
}