package org.example.demo.domain;

import org.apache.lucene.document.Document;

import java.util.ArrayList;

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

    public ArrayList<String> getAuthors() {
        String[] firstNames = this.document.get("authors_first_names").split(",");
        String[] lastNames = this.document.get("authors_last_names").split(",");
        String[] institutions = this.document.get("authors_institutions").split(",");

        ArrayList<String> authors = new ArrayList<>();
        for (int i = 0; i < firstNames.length-1; i++) {
            authors.add(firstNames[i] + " " + lastNames[i] + "," + institutions[i]);
        }
        System.out.println(authors);

        return authors;
    }

    public String getSummary() {
        return document.get("full_text").substring(0, 100);
    }
}
