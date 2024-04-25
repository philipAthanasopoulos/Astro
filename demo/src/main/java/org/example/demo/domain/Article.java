package org.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Philip Athanasopoulos
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class Article {
    private int year;
    private String title;
    private String article_abstract;
    private String full_text;
}
