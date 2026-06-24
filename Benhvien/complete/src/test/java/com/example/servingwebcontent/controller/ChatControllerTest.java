package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.Controller.ChatController;
import com.example.servingwebcontent.service.AIService;
import com.example.servingwebcontent.service.AIService.ChatServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Test
    void blankMessageShouldReturnBadRequest() {
        ChatController controller = new ChatController(mock(AIService.class));

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest("   ", null));

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void tooLongMessageShouldReturnBadRequest() {
        ChatController controller = new ChatController(mock(AIService.class));

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest("a".repeat(2001), null));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void validMessageShouldReturnReply() {
        AIService aiService = mock(AIService.class);
        when(aiService.ask(eq("xin chao"), anyList())).thenReturn("chao ban");
        ChatController controller = new ChatController(aiService);

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest(" xin chao ", null));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("chao ban", response.getBody().get("reply"));
    }

    @Test
    void serviceExceptionShouldReturnBadGateway() {
        AIService aiService = mock(AIService.class);
        when(aiService.ask(anyString(), anyList())).thenThrow(new ChatServiceException("service down"));
        ChatController controller = new ChatController(aiService);

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest("hoi dap", null));

        assertEquals(502, response.getStatusCode().value());
        assertEquals("service down", response.getBody().get("error"));
    }
}
