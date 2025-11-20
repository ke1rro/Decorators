package com.example;

public class CachedDocument extends AbstractDecorator {
    private final String cacheKey;
    private final DatabaseCache cache;

    public CachedDocument(Document document, String cacheKey) {
        super(document);
        this.cacheKey = cacheKey;
        this.cache = DatabaseCache.getInstance();
    }

    @Override
    public String parse() {
        String cachedResult = cache.get(cacheKey);
        if (cachedResult != null) {
            System.out.println("Cache hit for key: " + cacheKey);
            return cachedResult;
        }

        System.out.println("Cache miss for key: " + cacheKey + " - parsing document");
        String result = super.parse();
        cache.put(cacheKey, result);

        return result;
    }
}
