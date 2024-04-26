package org.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.lucene.queryparser.classic.ParseException;
import org.example.demo.domain.SearchResult;
import org.example.demo.domain.Searcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ApplicationController {

    @FXML
    public ScrollPane searchResultsScrollPane;
    @FXML
    public MenuButton fieldMenuButton;
    @FXML
    public ChoiceBox fieldChoiceBox;
    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    private Searcher searcher;

    @FXML
    private VBox searchResultsVBox;


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
//        fieldMenuButton.getItems().addAll(new MenuItem("title"), new MenuItem("year"), new MenuItem("content"), new MenuItem("abstract"));
        fieldChoiceBox.getItems().addAll("title", "year", "full_text", "abstract");
    }

    @FXML
    protected void handleEnterPressed(KeyEvent keyEvent) throws ParseException, IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) search();
    }

    @FXML
    protected void search() throws ParseException, IOException {
        String plain_text_query = searchTextField.getText();
        String searchField =  (String) fieldChoiceBox.getValue();
        System.out.println(searchField);
        List<SearchResult> results = searcher.getSearchResults(searchField, searchTextField.getText());
        renderSearchResults(results);
    }

    private void renderSearchResults(List<SearchResult> results) {

        searchResultsVBox.getChildren().clear();

        for (SearchResult result : results) {
            Label titleLabel = new Label(result.getTitle());
            titleLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 20px;");

            Label yearLabel = new Label(result.getYear());
            yearLabel.setStyle("-fx-text-fill: green; -fx-font-size: 15px;");


            TextFlow textFlow = new TextFlow();
            textFlow.setPrefWidth(500);
            Text text = new Text(result.getAbstract());
            text.setStyle("-fx-font-size: 15px;");
            textFlow.getChildren().add(text);

            searchResultsVBox.getChildren().add(titleLabel);
            searchResultsVBox.getChildren().add(yearLabel);
            searchResultsVBox.getChildren().add(textFlow);
        }
    }


}