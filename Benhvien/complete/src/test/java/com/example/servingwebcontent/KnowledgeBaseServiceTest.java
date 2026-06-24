package com.example.servingwebcontent;

import com.example.servingwebcontent.service.KnowledgeBaseService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgeBaseServiceTest {
    @Test
    void findsAppointmentInstructionsFromHospitalDocuments() {
        KnowledgeBaseService service = new KnowledgeBaseService();
        service.loadDocuments();

        String context = service.findRelevantContext("Tôi muốn đặt lịch khám", 3);

        assertTrue(context.contains("Đặt lịch khám"));
        assertTrue(context.contains("PENDING"));
    }
}
