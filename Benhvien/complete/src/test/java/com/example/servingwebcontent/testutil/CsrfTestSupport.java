package com.example.servingwebcontent.testutil;

import com.example.servingwebcontent.security.CsrfTokenFilter;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class CsrfTestSupport {
    private CsrfTestSupport() {
    }

    public static MockHttpServletRequestBuilder csrfPost(String urlTemplate, Object... uriVariables) {
        MockHttpSession session = new MockHttpSession();
        String token = CsrfTokenFilter.getOrCreateToken(session);
        return post(urlTemplate, uriVariables)
            .session(session)
            .param(CsrfTokenFilter.PARAMETER_NAME, token);
    }
}
