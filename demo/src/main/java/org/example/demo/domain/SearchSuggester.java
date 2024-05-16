package org.example.demo.domain;

import java.util.List;
import java.util.Random;

/**
 * @author Philip Athanasopoulos
 */
public class SearchSuggester {

    private final List<String> searches;

    public SearchSuggester() {
        searches = List.of(
                "What is artificial intelligence?",
                "History of artificial intelligence",
                "Applications of artificial intelligence",
                "Artificial intelligence in healthcare",
                "Artificial intelligence in finance",
                "Artificial intelligence vs machine learning",
                "Future of artificial intelligence",
                "Artificial intelligence companies",
                "Artificial intelligence in education",
                "Artificial intelligence in gaming",
                "Artificial intelligence in business",
                "Artificial intelligence in data analysis",
                "Artificial intelligence in autonomous vehicles",
                "Artificial intelligence in robotics",
                "Ethics of artificial intelligence",
                "Artificial intelligence in cyber security",
                "Artificial intelligence in agriculture",
                "Artificial intelligence in manufacturing",
                "Artificial intelligence in marketing",
                "Artificial intelligence in music production"
        );
    }

    public String suggestRandomSearch() {
        Random random = new Random();
        return searches.get(random.nextInt(searches.size()));
    }

}
