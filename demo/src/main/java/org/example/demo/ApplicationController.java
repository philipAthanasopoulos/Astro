package org.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.lucene.queryparser.classic.ParseException;
import org.example.demo.domain.SearchResult;
import org.example.demo.domain.Searcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationController {

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
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    @FXML
    private TextField searchTextField;

    private Searcher searcher;
    private int resultsPageIndex = 1;

    @FXML
    private VBox searchResultsVBox;

    private ArrayList<String> searchHistory = new ArrayList<>();


    public ApplicationController() {
        try {
            this.searcher = new Searcher();
        } catch (IOException e) {
            System.out.println("Could not initialize the searcher!");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.out.println("Could not find the index directory!");
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        addMenuFieldItems();

    }

    @FXML
    private void addMenuFieldItems() {
        fieldChoiceBox.getItems().addAll("title", "year", "full_text", "abstract");
        fieldChoiceBox.setValue("title");
    }


    @FXML
    protected void search() throws ParseException, IOException {
        String searchField = fieldChoiceBox.getValue();
        System.out.println(searchField);
        List<SearchResult> results = searcher.getSearchResults(searchField, searchTextField.getText());
        searchResults.clear();
        searchResults.addAll(results);
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

        TextFlow textFlow = new TextFlow();
        textFlow.setPrefWidth(500);

        Text text = new Text(result.getAbstract());
        text.setStyle("-fx-font-size: 15px;");
        textFlow.getChildren().add(text);

        searchResultsVBox.getChildren().add(titleLabel);
        searchResultsVBox.getChildren().add(yearLabel);
        searchResultsVBox.getChildren().add(textFlow);
    }

    private void openResultInNewTab(SearchResult result) {

        Label titleLabel = new Label(result.getTitle());
        titleLabel.getStyleClass().add("title-label");

        Label yearLabel = new Label(result.getYear());
        yearLabel.getStyleClass().add("year-label");

        Label authorsLabel = new Label();
        result.getAuthors().forEach(author -> authorsLabel.setText(authorsLabel.getText() + author + "\n"));
        authorsLabel.getStyleClass().add("authors-label");

        TextArea textArea = new TextArea(result.getFullText());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(1900, 900);

        VBox vBox = new VBox();
        vBox.getChildren().add(titleLabel);
        vBox.getChildren().add(yearLabel);
        vBox.getChildren().add(authorsLabel);
        vBox.getChildren().add(textArea);
        vBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/result_tab.css")).toExternalForm());

        Tab tab = new Tab(result.getTitle());
        tab.setContent(vBox);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }


    public void loadNextResultsPage(ActionEvent actionEvent) {
        if (resultsPageIndex < searchResults.size()) resultsPageIndex++;
        renderSearchResults();
    }

    public void loadPreviousResultsPage(ActionEvent actionEvent) {
        if (resultsPageIndex > 1) resultsPageIndex--;
        renderSearchResults();
    }
}