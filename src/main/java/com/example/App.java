package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Document Decorator Pattern Demo ===\n");

        System.out.println("1. Basic SmartDocument (with mock GCS path):");
        System.out.println("   Note: This would normally call Google Cloud Vision API");
        System.out.println();

        System.out.println("2. TimedDocument - measures parsing performance:");
        Document mockDoc = createMockDocument("Sample document content");
        Document timedDoc = new TimedDocument(mockDoc);
        String result = timedDoc.parse();
        System.out.println("   Result: " + result.substring(0, Math.min(50, result.length())) + "...");
        System.out.println();

        System.out.println("3. CachedDocument - caches results in SQLite:");
        Document mockDoc2 = createMockDocument("Content to cache");
        Document cachedDoc = new CachedDocument(mockDoc2, "cache-key-1");

        System.out.println("   First call (cache miss):");
        cachedDoc.parse();

        System.out.println("   Second call (cache hit):");
        cachedDoc.parse();
        System.out.println();

        System.out.println("4. Combined decorators (Timed + Cached):");
        Document mockDoc3 = createMockDocument("Combined decorator content");
        Document combined = new TimedDocument(
                new CachedDocument(mockDoc3, "cache-key-2"));

        System.out.println("   First call (miss cache, timed):");
        combined.parse();

        System.out.println("   Second call (hit cache, still timed):");
        combined.parse();
        System.out.println();

        System.out.println("5. Multiple decorator layers:");
        Document mockDoc4 = createMockDocument("Multi-layer content");
        Document multiLayer = new CachedDocument(
                new TimedDocument(
                        new CachedDocument(
                                new TimedDocument(mockDoc4),
                                "inner-cache")),
                "outer-cache");
        multiLayer.parse();

        System.out.println("\n=== Demo Complete ===");
        System.out.println("\nKey Benefits of Decorator Pattern:");
        System.out.println("- Add functionality dynamically without changing original classes");
        System.out.println("- Combine multiple behaviors (timing + caching)");
        System.out.println("- Follow Open/Closed Principle (open for extension, closed for modification)");
        System.out.println("- Single Responsibility Principle (each decorator has one job)");
    }

    private static Document createMockDocument(final String content) {
        return new Document() {
            @Override
            public String parse() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return content;
            }
        };
    }
}
