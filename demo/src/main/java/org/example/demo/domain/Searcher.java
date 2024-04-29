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
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.example.demo.luceneDemo.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Philip Athanasopoulos
 */
public class Searcher {

    private final int MAX_NUM_OF_RESULTS = 10;
    private final String PAPERS_FOLDER_LOCATION = "/archive/data.csv";
    private final String INDEX_DIRECTORY_PATH = "/directory";
    private final Directory directory;
    private final DirectoryReader directoryReader;
    private final Analyzer analyzer;
    private final IndexSearcher indexSearcher;
    private IndexWriter indexWriter;
    private AnalyzingInfixSuggester suggester;


    public Searcher() throws IOException, URISyntaxException {
        this.analyzer = new StandardAnalyzer();
//        this.directory = new ByteBuffersDirectory();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
        this.suggester = new AnalyzingInfixSuggester(this.directory, this.analyzer);
        createDirectoryIfNotExist();
        this.directoryReader = DirectoryReader.open(this.directory);
        this.indexSearcher = new IndexSearcher(this.directoryReader);

        InputStream inputStream = Demo.class.getResourceAsStream(PAPERS_FOLDER_LOCATION);

        assert inputStream != null;
        Reader reader = new InputStreamReader(inputStream);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        CSVInputIterator csvInputIterator = new CSVInputIterator(csvParser.iterator());
        this.suggester.build(csvInputIterator);

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

            doc.add(new Field("authors_first_names", csvRecord.get(5), TextField.TYPE_STORED));
            doc.add(new Field("authors_last_names", csvRecord.get(6), TextField.TYPE_STORED));
            doc.add(new Field("authors_institutions", csvRecord.get(7), TextField.TYPE_STORED));

            writer.addDocument(doc);
            lineCounter++;
        }

        System.out.println("Files loaded to index.");
        System.out.println("Loaded " + lineCounter + " files");

    }

    private TopDocs searchForResults(String field, String text) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser(field, this.analyzer);
        Query query = queryParser.parse(text);
        return indexSearcher.search(query, directoryReader.numDocs());
    }

    private Document getDocumentFromDB(ScoreDoc scoreDoc) throws IOException {
        return this.indexSearcher.storedFields().document(scoreDoc.doc);
    }

    private List<Document> getDocumentsFromDB(TopDocs topDocs) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) documents.add(getDocumentFromDB(scoreDoc));
        return documents;
    }


    public List<SearchResult> getSearchResults(String field, String plain_text_query) throws ParseException, IOException {
        List<SearchResult> searchResults = new ArrayList<>();
        int resultCounter = 0;
        for (Document document : getDocumentsFromDB(searchForResults(field, plain_text_query))) {
            searchResults.add(new SearchResult(document, resultCounter));
            resultCounter++;
        }
        return searchResults;
    }

    public List<String> getSuggestions(String query) throws IOException {
        List<String> suggestions = new ArrayList<>();
        for (Lookup.LookupResult lookupResult : suggester.lookup(query, false, 7)) {
            suggestions.add(lookupResult.key.toString());
        }
        return suggestions;
    }

    public static void main(String[] args) {
        try {
            Searcher searcher = new Searcher();
            searcher.getSearchResults("title", "machine learning");
            System.out.println(searcher.getSuggestions("ai"));
        } catch (IOException | URISyntaxException | ParseException e) {
            e.printStackTrace();
        }

    }
}
