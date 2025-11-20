package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private DatabaseCache cache;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        cache = DatabaseCache.getInstance();
        cache.clear();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        cache.clear();
    }

    @Test
    @DisplayName("Test AbstractDecorator delegates to wrapped document")
    public void testAbstractDecorator() {
        Document mockDoc = new MockDocument("Test content");

        Document decorator = new AbstractDecorator(mockDoc) {};

        assertEquals("Test content", decorator.parse());
    }

    @Test
    @DisplayName("Test TimedDocument measures parsing time")
    public void testTimedDocument() {
        Document mockDoc = new MockDocument("Timed content");
        Document timedDoc = new TimedDocument(mockDoc);

        String result = timedDoc.parse();

        assertEquals("Timed content", result);
        String output = outContent.toString();
        assertTrue(output.contains("Document parsing took"));
        assertTrue(output.contains("ms"));
    }

    @Test
    @DisplayName("Test CachedDocument caches results")
    public void testCachedDocument() {
        Document mockDoc = new MockDocument("Cached content");
        String cacheKey = "test-key-1";

        Document cachedDoc = new CachedDocument(mockDoc, cacheKey);

        String result1 = cachedDoc.parse();
        assertEquals("Cached content", result1);
        assertTrue(outContent.toString().contains("Cache miss"));

        outContent.reset();

        String result2 = cachedDoc.parse();
        assertEquals("Cached content", result2);
        assertTrue(outContent.toString().contains("Cache hit"));
    }

    @Test
    @DisplayName("Test combining TimedDocument and CachedDocument")
    public void testCombinedDecorators() {
        Document mockDoc = new MockDocument("Combined content");
        String cacheKey = "test-key-2";

        Document cachedDoc = new CachedDocument(mockDoc, cacheKey);
        Document timedCachedDoc = new TimedDocument(cachedDoc);

        String result1 = timedCachedDoc.parse();
        assertEquals("Combined content", result1);
        String output1 = outContent.toString();
        assertTrue(output1.contains("Cache miss"));
        assertTrue(output1.contains("Document parsing took"));

        outContent.reset();

        String result2 = timedCachedDoc.parse();
        assertEquals("Combined content", result2);
        String output2 = outContent.toString();
        assertTrue(output2.contains("Cache hit"));
        assertTrue(output2.contains("Document parsing took"));
    }

    @Test
    @DisplayName("Test DatabaseCache basic operations")
    public void testDatabaseCacheOperations() {
        String key = "test-key";
        String value = "test-value";

        assertNull(cache.get(key));
        assertFalse(cache.contains(key));

        cache.put(key, value);

        assertEquals(value, cache.get(key));
        assertTrue(cache.contains(key));

        cache.clear();

        assertNull(cache.get(key));
        assertFalse(cache.contains(key));
    }

    @Test
    @DisplayName("Test DatabaseCache with multiple entries")
    public void testDatabaseCacheMultipleEntries() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));

        cache.put("key1", "updated-value1");
        assertEquals("updated-value1", cache.get("key1"));
    }

    @Test
    @DisplayName("Test nested decorators maintain correct order")
    public void testNestedDecorators() {
        Document mockDoc = new MockDocument("Nested content");

        Document doc1 = new TimedDocument(mockDoc);
        Document doc2 = new CachedDocument(doc1, "nested-1");
        Document doc3 = new TimedDocument(doc2);
        Document doc4 = new CachedDocument(doc3, "nested-2");

        String result = doc4.parse();
        assertEquals("Nested content", result);

        String output = outContent.toString();
        assertTrue(output.contains("Document parsing took"));
    }

    private static class MockDocument implements Document {
        private final String content;

        public MockDocument(String content) {
            this.content = content;
        }

        @Override
        public String parse() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return content;
        }
    }
}
