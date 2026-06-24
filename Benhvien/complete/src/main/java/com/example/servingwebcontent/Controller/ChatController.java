package com.example.servingwebcontent.Controller;

import com.example.servingwebcontent.service.AIService;
import com.example.servingwebcontent.service.AIService.ChatMessage;
import com.example.servingwebcontent.service.AIService.ChatServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final AIService aiService;

    public ChatController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String message = request != null ? request.message() : null;
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Câu hỏi không được để trống."));
        }
        if (message.length() > 2_000) {
            return ResponseEntity.badRequest().body(Map.of("error", "Câu hỏi không được vượt quá 2.000 ký tự."));
        }

        try {
            String reply = aiService.ask(message.trim(),
                request.history() != null ? request.history() : Collections.emptyList());
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (ChatServiceException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        }
    }

    public record ChatRequest(String message, List<ChatMessage> history) {
    }
}
