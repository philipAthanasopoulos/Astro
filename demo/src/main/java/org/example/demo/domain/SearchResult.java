package org.example.demo.domain;

import org.apache.lucene.document.Document;

/**
 * @author Philip Athanasopoulos
 */
public class SearchResult {
    private final Document document;
    private int number;

    public SearchResult(Document document, int number) {
        this.document = document;
        this.number = number;
    }

    public String getTitle() {
        return this.document.get("title");
    }

    public String getAbstract() {
        return this.document.get("abstract");
    }

    public String getYear() {
        return this.document.get("year");
    }

    public String getFullText() {
        return this.document.get("full_text");
    }

    public String getSummary() {
        return document.get("full_text").substring(0, 100);
    }
}
