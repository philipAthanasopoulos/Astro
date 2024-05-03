package org.example.demo.domain;

import org.apache.lucene.document.Document;

import java.util.ArrayList;

/**
 * @author Philip Athanasopoulos
 */
public class SearchResult {
    private final Document document;
    private String[] bestFragments;

    public SearchResult(Document document, String[] fragment) {
        this.document = document;
        this.bestFragments = fragment;
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

    public String getBestFragment() {
        return this.bestFragments[0];
    }

    public String[] getBestFragments() {
        return this.bestFragments;
    }

    public ArrayList<String> getAuthors() {
        String[] authorFullNames = this.document.getValues("authors_full_names");
        String[] institutions = this.document.getValues("authors_institutions");

        ArrayList<String> authors = new ArrayList<>();
        for (int i = 0; i < authorFullNames.length; i++) {
            authors.add(authorFullNames[i] + ", "+  institutions[i]);
        }
        return authors;
    }

    public String getSummary() {
        return document.get("full_text").substring(0, 100);
    }
}
