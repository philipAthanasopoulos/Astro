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

    private final int MAX_NUM_OF_RESULTS = 20;
    private final String PAPERS_FOLDER_LOCATION = "/archive/papers.csv";
    private final String INDEX_DIRECTORY_PATH = "/directory";
    private final Directory directory;
    private final DirectoryReader directoryReader;
    private final Analyzer analyzer;
    private IndexWriter indexWriter;
    private final IndexSearcher indexSearcher;

    public Searcher() throws IOException, URISyntaxException {
        this.analyzer = new StandardAnalyzer();
//        this.directory = new ByteBuffersDirectory();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY_PATH));
        if (!DirectoryReader.indexExists(this.directory)) {
            this.indexWriter = new IndexWriter(this.directory, new IndexWriterConfig(this.analyzer));
            loadFilesToIndex(indexWriter);
            indexWriter.close();
        }
        this.directoryReader = DirectoryReader.open(this.directory);
        this.indexSearcher = new IndexSearcher(this.directoryReader);

        System.out.println("Successfully initialized Searcher!");
    }


    private void loadFilesToIndex(IndexWriter writer) throws IOException {
        // Load files to index
        InputStream inputStream = Demo.class.getResourceAsStream(PAPERS_FOLDER_LOCATION);

        Reader reader = new InputStreamReader(inputStream);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

        int likeCounter = 0;

        for (CSVRecord csvRecord : csvParser) {

            Document doc = new Document();
            doc.add(new Field("year", csvRecord.get(1), TextField.TYPE_STORED));
            doc.add(new Field("title", csvRecord.get(2), TextField.TYPE_STORED));
            doc.add(new Field("abstract", csvRecord.get(3), TextField.TYPE_STORED));
            doc.add(new Field("full_text", csvRecord.get(4), TextField.TYPE_STORED));

            writer.addDocument(doc);
            likeCounter++;
        }

        System.out.println("Files loaded to index.");
        System.out.println("Loaded " + likeCounter + " files");

    }

    private TopDocs searchForResults(String field, String text) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser(field, this.analyzer);
        Query query = queryParser.parse(text);
        return indexSearcher.search(query, MAX_NUM_OF_RESULTS);
    }

    private Document getDocumentFromDB(ScoreDoc scoreDoc) throws IOException {
        return this.indexSearcher.storedFields().document(scoreDoc.doc);
    }

    private List<Document> getDocumentsFromDB(TopDocs topDocs) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(getDocumentFromDB(scoreDoc));
        }
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
}
