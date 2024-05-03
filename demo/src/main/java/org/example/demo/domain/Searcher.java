package org.example.demo.domain;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.demo.luceneDemo.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Philip Athanasopoulos
 */
public class Searcher {

    //If the user cannot find what he is looking for in the first 200 results
    //they should refine their search query
    //or we should fix the search engine...
    public static final int MAX_NUMBER_OF_SEARCH_RESULTS = 100;
    private final String PAPERS_FOLDER_LOCATION = "/archive/data.csv";
    private final String INDEX_DIRECTORY_PATH = "./demo/src/main/resources/directory/";
    private final Directory directory;
    private final DirectoryReader directoryReader;
    private final Analyzer analyzer;
    private final IndexSearcher indexSearcher;
    private IndexWriter indexWriter;
    private AnalyzingInfixSuggester suggester;
    private Highlighter highlighter;


    public Searcher() throws IOException, URISyntaxException {
        this.analyzer = new StandardAnalyzer();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
        createDirectoryIfNotExist();
        this.directoryReader = DirectoryReader.open(this.directory);
        this.indexSearcher = new IndexSearcher(this.directoryReader);

        System.out.println("Successfully initialized Searcher!");
    }


    private void createDirectoryIfNotExist() throws IOException {
        if (!DirectoryReader.indexExists(this.directory)) {
            this.indexWriter = new IndexWriter(this.directory, new IndexWriterConfig(this.analyzer));
            loadFilesToIndex(indexWriter);
            indexWriter.close();
        }
    }

    private void loadFilesToIndex(IndexWriter writer) throws IOException {
        InputStream inputStream = Demo.class.getResourceAsStream(PAPERS_FOLDER_LOCATION);

        assert inputStream != null;
        Reader reader = new InputStreamReader(inputStream);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        int lineCounter = 0;
        for (CSVRecord csvRecord : csvParser) {

            Document doc = new Document();
            doc.add(new Field("source_id", csvRecord.get(0), TextField.TYPE_STORED));
            doc.add(new Field("year", csvRecord.get(1), TextField.TYPE_STORED));
            doc.add(new Field("title", csvRecord.get(2), TextField.TYPE_STORED));
            doc.add(new Field("abstract", csvRecord.get(3), TextField.TYPE_STORED));
            doc.add(new Field("full_text", csvRecord.get(4), TextField.TYPE_STORED));

            //Add authors and institutions
            String[] firstNames = csvRecord.get(5).split(",");
            String[] lastNames = csvRecord.get(6).split(",");
            String[] institutions = csvRecord.get(7).split(",");
            for (int i = 0; i < firstNames.length; i++) {
                doc.add(new Field("authors_full_names", firstNames[i] + " " + lastNames[i], TextField.TYPE_STORED));
                doc.add(new Field("authors_institutions", institutions[i], TextField.TYPE_STORED));
            }

            writer.addDocument(doc);
            lineCounter++;
        }

        System.out.println("Files loaded to index.");
        System.out.println("Loaded " + lineCounter + " files");

    }

        private TopDocs searchForResults(String field, String text) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser(field, this.analyzer);
        Query query = queryParser.parse(text);
        QueryScorer queryScorer = new QueryScorer(query, field);
        Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);
        this.highlighter = new Highlighter(queryScorer);
        this.highlighter.setTextFragmenter(fragmenter);
        return indexSearcher.search(query, MAX_NUMBER_OF_SEARCH_RESULTS);
    }

    private Document getDocumentFromDB(ScoreDoc scoreDoc) throws IOException {
        return this.indexSearcher.storedFields().document(scoreDoc.doc);
    }

    private List<Document> getDocumentsFromDB(TopDocs topDocs) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) documents.add(getDocumentFromDB(scoreDoc));
        return documents;
    }

    public List<SearchResult> getSearchResults(String field, String plain_text_query) throws ParseException, IOException, InvalidTokenOffsetsException {
        List<SearchResult> searchResults = new ArrayList<>();
        TopDocs topDocs = searchForResults(field, plain_text_query);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = getDocumentFromDB(scoreDoc);
            String text = document.get(field);
            String[] fragments = highlighter.getBestFragments(analyzer, field, text, 5);
            searchResults.add(new SearchResult(document, fragments));
        }
        System.out.println("Found " + searchResults.size() + " results");
        return searchResults;
    }

    public List<String> getSuggestions(String query) throws IOException {
        List<String> suggestions = new ArrayList<>();
        for (Lookup.LookupResult lookupResult : suggester.lookup(query, false, 7))
            suggestions.add(lookupResult.key.toString());
        System.out.println("Found " + suggestions.size() + " suggestions");
        return suggestions;
    }
}
