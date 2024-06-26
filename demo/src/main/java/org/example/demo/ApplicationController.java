package org.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.example.demo.domain.SearchResult;
import org.example.demo.domain.SearchSuggester;
import org.example.demo.domain.Searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ApplicationController {

    private final ArrayList<String> searchHistory = new ArrayList<>();
    private final Searcher searcher;
    @FXML
    public ScrollPane searchResultsScrollPane;
    @FXML
    public ChoiceBox<String> fieldChoiceBox;
    @FXML
    public TabPane tabPane;
    @FXML
    public Button nextTabButton;
    @FXML
    public Button previousTabButton;
    @FXML
    public Label pageIndex;
    @FXML
    public ListView<String> historyList;
    @FXML
    public Button historyButton;
    @FXML
    public ChoiceBox<String> sortByChoiceBox;
    @FXML
    public ListView<String> suggestionsList;
    @FXML
    public ImageView logo;
    @FXML
    public Button searchButton;
    @FXML
    public Button trendySuggestionButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private VBox searchResultsVBox;
    private int resultsPageIndex = 1;
    private List<SearchResult> searchResults = new ArrayList<>();
    private SearchSuggester searchSuggester;


    public ApplicationController() throws IOException {
        this.searcher = new Searcher();
    }

    @FXML
    public void initialize() {
        searchSuggester = new SearchSuggester();
        addMenuFieldItems();
        historyList.setVisible(false);
        suggestionsList.setVisible(false);

        ImageView searchIcon = new ImageView(Objects.requireNonNull(getClass().getResource("/images/search.png")).toExternalForm());
        searchButton.setGraphic(searchIcon);

        addListeners();
        addToolips();
    }

    private void addToolips() {
        List<Tooltip> tooltips = new ArrayList<>();

        Tooltip trendyButtonTooltip = new Tooltip("Get a random search which other users found interesting");
        tooltips.add(trendyButtonTooltip);
        trendySuggestionButton.setTooltip(trendyButtonTooltip);
        Tooltip historyButtonTooltip = new Tooltip("See your search history");
        historyButton.setTooltip(historyButtonTooltip);
        tooltips.add(historyButtonTooltip);


        tooltips.forEach(tooltip -> {
            tooltip.setHideDelay(new Duration(0));
            tooltip.setShowDelay(new Duration(0));
        });
    }

    private void addListeners() {
        sortByChoiceBox.setOnAction(event -> sortResults());
    }

    @FXML
    public void searchTrendyQuery() throws InvalidTokenOffsetsException, ParseException, IOException {
        searchTextField.setText(searchSuggester.suggestRandomSearch());
        search();
    }

    private void sortResults() {
        System.out.println("User tried to sort results");
        String sortMethod = sortByChoiceBox.getValue();
        switch (sortMethod) {
            case "oldest first" -> searchResults.sort(Comparator.comparing(SearchResult::getYear));
            case "newest first" -> searchResults.sort(Comparator.comparing(SearchResult::getYear).reversed());
            case "relevance" -> searchResults.sort(Comparator.comparing(SearchResult::getScore).reversed());
        }
        //refresh
        renderSearchResults();
    }

    @FXML
    private void addMenuFieldItems() {
        fieldChoiceBox.getItems().addAll("all","title", "year", "full_text", "abstract", "authors_full_names");
        sortByChoiceBox.getItems().addAll("newest first", "oldest first", "relevance");
        sortByChoiceBox.setValue("relevance");
        fieldChoiceBox.setValue("all");
    }

    @FXML
    protected void search() throws ParseException, IOException, InvalidTokenOffsetsException {
        String searchField = fieldChoiceBox.getValue();
        System.out.println(searchField);
        searchResults = searcher.getSearchResults(searchField, searchTextField.getText());
        resultsPageIndex = 1;
        renderSearchResults();
        searchHistory.add(searchTextField.getText());
    }

    private void renderSearchResults() {
        searchResultsVBox.getChildren().clear();
        List<SearchResult> selectedResults = searchResults.subList(10 * (resultsPageIndex - 1), Math.min(searchResults.size(), 10 * resultsPageIndex));
        for (SearchResult result : selectedResults) addResultToPage(result);
        pageIndex.setText(String.valueOf(resultsPageIndex));
    }

    private void addResultToPage(SearchResult result) {
        Label titleLabel = new Label(result.getTitle());
        titleLabel.getStyleClass().add("title-label");
        titleLabel.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            openResultInNewTab(result);
            System.out.println("Clicked on: " + result.getTitle());
        });

        Label yearLabel = new Label(result.getYear());
        yearLabel.getStyleClass().add("year-label");

        Label authorsLabel = new Label();
        List<String> authors = result.getAuthors();
        if (!authors.isEmpty()) {
            authorsLabel.setText("By: ");
            authorsLabel.setText(authorsLabel.getText() + authors.get(0));
            if (authors.size() > 1)
                authorsLabel.setText(authorsLabel.getText() + " and " + (authors.size() - 1) + " more");
        }

        TextFlow textFlow = new TextFlow();
        textFlow.setPrefWidth(500);
        addFragmentsToTextFlow(result, textFlow);

        textFlow.getChildren().add(new Text("\n \n \n \n \n \n "));

        searchResultsVBox.getChildren().add(titleLabel);
        searchResultsVBox.getChildren().add(yearLabel);
        searchResultsVBox.getChildren().add(authorsLabel);
        searchResultsVBox.getChildren().add(textFlow);
    }

    private void addFragmentsToTextFlow(SearchResult result, TextFlow textFlow) {
        for (String fragment : result.getBestFragments()) {
            String[] words = fragment.split(" ");
            for (String word : words) {
                if (word.contains("<B>")) {
                    Text boldText = new Text(word.replace("<B>", " ").replace("</B>", " "));
                    boldText.getStyleClass().add("bold-text");
                    textFlow.getChildren().add(boldText);
                } else {
                    Text normalText = new Text(word + " ");
                    normalText.getStyleClass().add("normal-text");
                    textFlow.getChildren().add(normalText);
                }
            }
        }
    }

    private void openResultInNewTab(SearchResult result) {

        Label titleLabel = new Label(result.getTitle());
        titleLabel.getStyleClass().add("title-label");

        Label yearLabel = new Label("Published: " + result.getYear());
        yearLabel.getStyleClass().add("year-label");

        Label authorsLabel = new Label("Authors: \n");
        authorsLabel.getStyleClass().add("authors-label");
        for (String author : result.getAuthors()) authorsLabel.setText(authorsLabel.getText() + author + "\n");
        System.out.println(result.getAuthors());

        Label textLabel = new Label("Text:");
        textLabel.getStyleClass().add("text-label");
        textLabel.setText(result.getFullText());

        VBox vBox = new VBox(titleLabel, yearLabel, authorsLabel, textLabel);
        vBox.getStyleClass().add("results-vbox");

        ScrollPane tabScrollPane = new ScrollPane();
        tabScrollPane.getStyleClass().add("tab-scroll-pane");
        tabScrollPane.setContent(vBox);
        tabScrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/result_tab.css")).toExternalForm());

        Tab tab = new Tab(result.getTitle());
        tab.setContent(tabScrollPane);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void loadNextResultsPage() {
        if (resultsPageIndex < searchResults.size()) resultsPageIndex++;
        renderSearchResults();
    }

    public void loadPreviousResultsPage() {
        if (resultsPageIndex > 1) resultsPageIndex--;
        renderSearchResults();
    }

    //TODO
    //Use history to suggest search queries
    public void handleHistoryButtonPress() {
        if (historyList.isVisible()) historyList.setVisible(false);
        else {
            historyList.setVisible(true);
            historyList.getItems().clear();
            historyList.getItems().addAll(searchHistory);
        }
    }
}