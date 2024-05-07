package org.example.demo.domain;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Philip Athanasopoulos
 */
public class SearchSuggester {

    private ArrayList<String> searches;
    private String allUsersSearchesFile = "./demo/src/main/resources/archive/globalUserSearches.json";

    public SearchSuggester() {
        parseAllSearches();
    }

    private void parseAllSearches() {
        searches = new ArrayList<>();
        searches.add("What is artificial intelligence?");
        searches.add("History of artificial intelligence");
        searches.add("Applications of artificial intelligence");
        searches.add("Artificial intelligence in healthcare");
        searches.add("Artificial intelligence in finance");
        searches.add("Artificial intelligence vs machine learning");
        searches.add("Future of artificial intelligence");
        searches.add("Artificial intelligence companies");
        searches.add("Artificial intelligence in education");
        searches.add("Artificial intelligence in gaming");
        searches.add("Artificial intelligence in business");
        searches.add("Artificial intelligence in data analysis");
        searches.add("Artificial intelligence in autonomous vehicles");
        searches.add("Artificial intelligence in robotics");
        searches.add("Ethics of artificial intelligence");
        searches.add("Artificial intelligence in cyber security");
        searches.add("Artificial intelligence in agriculture");
        searches.add("Artificial intelligence in manufacturing");
        searches.add("Artificial intelligence in marketing");
        searches.add("Artificial intelligence in music production");
    }


    public String suggestRandomSearch(){
        Random random = new Random();
        return searches.get(random.nextInt(searches.size()));
    }

}
