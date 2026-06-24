package com.example.servingwebcontent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeBaseServiceTest {

    private KnowledgeBaseService service;

    @BeforeEach
    void setUp() {
        service = new KnowledgeBaseService();
        service.loadDocuments();
    }

    @Test
    void appointmentQuestionShouldFindRelatedContext() {
        String context = service.findRelevantContext("lich kham PENDING phong kham", 2);

        assertFalse(context.isBlank());
        assertTrue(context.toLowerCase().contains("pending") || context.toLowerCase().contains("phong"));
    }

    @Test
    void hospitalServiceQuestionShouldFindDocument() {
        String context = service.findRelevantContext("benh vien dich vu cong thong tin benh nhan", 2);

        assertFalse(context.isBlank());
        assertTrue(context.contains("[Ngu"));
    }

    @Test
    void unrelatedQuestionShouldReturnEmptyContext() {
        assertEquals("", service.findRelevantContext("blockchain forex football", 2));
    }

    @Test
    void nullQuestionShouldReturnEmptyContext() {
        assertEquals("", service.findRelevantContext(null, 2));
    }

    @Test
    void resultShouldRespectLimit() {
        String context = service.findRelevantContext("benh nhan lich kham mat khau", 1);

        int sourceCount = context.split("\\[Ngu", -1).length - 1;
        assertTrue(sourceCount <= 1);
    }
}
