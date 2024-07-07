//package org.luismore.hlvsapi.controllers;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@Controller
//public class WebSocketController extends TextWebSocketHandler {
//
//    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.add(session);
//        System.out.println("New WebSocket connection: " + session.getId());
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
//        System.out.println("WebSocket connection closed: " + session.getId());
//    }
//
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        String payload = message.getPayload();
//        System.out.println("Received message: " + payload);
//
//        if ("moveServo".equals(payload)) {
//            session.sendMessage(new TextMessage("Servo command received"));
//        }
//    }
//
//    @PreAuthorize("permitAll()")
//    @PostMapping("/api/servo/move")
//    public ResponseEntity<String> moveServo() {
//        TextMessage message = new TextMessage("moveServo");
//        for (WebSocketSession session : sessions) {
//            try {
//                System.out.println("Sending moveServo command to WebSocket session: " + session.getId());
//                session.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message to WebSocket");
//            }
//        }
//        return ResponseEntity.ok("Servo move command sent");
//    }
//}
//
//

package org.luismore.hlvsapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
public class WebSocketController extends TextWebSocketHandler {

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New WebSocket connection: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        if ("moveServo".equals(payload)) {
            session.sendMessage(new TextMessage("Servo command received"));
        } else if ("moveServoP".equals(payload)) {
            session.sendMessage(new TextMessage("Servo P command received"));
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/api/servo/move")
    public ResponseEntity<String> moveServo() {
        return sendWebSocketCommand("moveServo");
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/api/servo/moveP")
    public ResponseEntity<String> moveServoP() {
        return sendWebSocketCommand("moveServoP");
    }

    private ResponseEntity<String> sendWebSocketCommand(String command) {
        TextMessage message = new TextMessage(command);
        for (WebSocketSession session : sessions) {
            try {
                System.out.println("Sending " + command + " command to WebSocket session: " + session.getId());
                session.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message to WebSocket");
            }
        }
        return ResponseEntity.ok("Command " + command + " sent");
    }
}
