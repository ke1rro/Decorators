package com.example;

public class TimedDocument extends AbstractDecorator {

    public TimedDocument(Document document) {
        super(document);
    }

    @Override
    public String parse() {
        long startTime = System.currentTimeMillis();

        try {
            String result = super.parse();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Document parsing took " + duration + " ms");
        }
    }
}
