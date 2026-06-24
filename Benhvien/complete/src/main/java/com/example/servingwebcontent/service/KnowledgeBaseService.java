package com.example.servingwebcontent.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class KnowledgeBaseService {
    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9đ]+");
    private static final Set<String> STOP_WORDS = Set.of(
        "va", "la", "cua", "co", "cho", "toi", "ban", "mot", "nhung", "cac", "duoc",
        "khong", "ve", "voi", "khi", "o", "thi", "gi", "nao", "nhu", "the"
    );

    private final List<KnowledgeChunk> chunks = new ArrayList<>();

    @PostConstruct
    public void loadDocuments() {
        chunks.clear();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath*:knowledge/**/*.*");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || !(filename.endsWith(".md") || filename.endsWith(".txt"))) continue;
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                addDocument(filename, content);
            }
            System.out.println("[KnowledgeBase] Đã nạp " + chunks.size() + " đoạn tài liệu.");
        } catch (Exception e) {
            throw new IllegalStateException("Không thể nạp kho tài liệu chatbot", e);
        }
    }

    public String findRelevantContext(String question, int limit) {
        Set<String> queryTokens = tokens(question);
        if (queryTokens.isEmpty()) return "";

        return chunks.stream()
            .map(chunk -> new ScoredChunk(chunk, score(queryTokens, chunk.tokens())))
            .filter(item -> item.score() > 0)
            .sorted(Comparator.comparingInt(ScoredChunk::score).reversed())
            .limit(Math.max(1, limit))
            .map(item -> "[Nguồn: " + item.chunk().source() + "]\n" + item.chunk().content())
            .reduce((left, right) -> left + "\n\n" + right)
            .orElse("");
    }

    private void addDocument(String source, String content) {
        String[] sections = content.replace("\r\n", "\n").split("\n\s*\n");
        String heading = source;
        StringBuilder chunk = new StringBuilder();
        for (String rawSection : sections) {
            String section = rawSection.trim();
            if (section.isBlank()) continue;
            if (section.startsWith("#")) heading = section.replaceFirst("^#+\\s*", "").trim();
            if (chunk.length() + section.length() > 1_200 && chunk.length() > 0) {
                store(source + " — " + heading, chunk.toString());
                chunk.setLength(0);
            }
            if (chunk.length() > 0) chunk.append("\n\n");
            chunk.append(section);
        }
        if (chunk.length() > 0) store(source + " — " + heading, chunk.toString());
    }

    private void store(String source, String content) {
        chunks.add(new KnowledgeChunk(source, content, tokens(content)));
    }

    private int score(Set<String> queryTokens, Set<String> documentTokens) {
        int score = 0;
        for (String token : queryTokens) {
            if (documentTokens.contains(token)) score += token.length() >= 6 ? 3 : 1;
        }
        return score;
    }

    private Set<String> tokens(String text) {
        String normalized = Normalizer.normalize(text == null ? "" : text, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
        Set<String> result = new HashSet<>(Arrays.asList(NON_WORD.split(normalized)));
        result.removeIf(word -> word.length() < 2 || STOP_WORDS.contains(word));
        return result;
    }

    private record KnowledgeChunk(String source, String content, Set<String> tokens) {
    }

    private record ScoredChunk(KnowledgeChunk chunk, int score) {
    }
}
