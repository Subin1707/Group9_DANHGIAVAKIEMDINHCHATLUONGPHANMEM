package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.Controller.ChatController;
import com.example.servingwebcontent.service.AIService;
import com.example.servingwebcontent.service.AIService.ChatServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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

    @Test
    void nullRequestShouldReturnBadRequest() {
        ChatController controller = new ChatController(mock(AIService.class));

        ResponseEntity<Map<String, String>> response = controller.chat(null);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void nullHistoryShouldUseEmptyHistory() {
        AIService aiService = mock(AIService.class);
        when(aiService.ask(eq("hello"), anyList())).thenReturn("ok");
        ChatController controller = new ChatController(aiService);

        controller.chat(new ChatController.ChatRequest("hello", null));

        verify(aiService).ask(eq("hello"), eq(List.of()));
    }

    @Test
    void historyShouldBePassedToAIService() {
        AIService aiService = mock(AIService.class);
        List<AIService.ChatMessage> history = List.of(
                new AIService.ChatMessage("user", "old question"),
                new AIService.ChatMessage("assistant", "old answer")
        );
        when(aiService.ask(eq("new question"), same(history))).thenReturn("new answer");
        ChatController controller = new ChatController(aiService);

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest("new question", history));

        assertEquals(200, response.getStatusCode().value());
        verify(aiService).ask("new question", history);
    }

    @Test
    void messageShouldBeTrimmedBeforeSendingToAIService() {
        AIService aiService = mock(AIService.class);
        when(aiService.ask(eq("trim me"), anyList())).thenReturn("trimmed");
        ChatController controller = new ChatController(aiService);

        controller.chat(new ChatController.ChatRequest("   trim me   ", List.of()));

        verify(aiService).ask(eq("trim me"), anyList());
    }

    @Test
    void messageWithExactlyTwoThousandCharactersShouldBeAccepted() {
        AIService aiService = mock(AIService.class);
        String message = "a".repeat(2000);
        when(aiService.ask(eq(message), anyList())).thenReturn("ok");
        ChatController controller = new ChatController(aiService);

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest(message, List.of()));

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void messageWithTwoThousandOneCharactersShouldBeRejected() {
        AIService aiService = mock(AIService.class);
        ChatController controller = new ChatController(aiService);

        ResponseEntity<Map<String, String>> response =
                controller.chat(new ChatController.ChatRequest("a".repeat(2001), List.of()));

        assertEquals(400, response.getStatusCode().value());
        verifyNoInteractions(aiService);
    }
}
