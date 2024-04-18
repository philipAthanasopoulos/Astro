package org.example.demo.luceneDemo;

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
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.*;

/**
 * @author Philip Athanasopoulos
 */
public class Demo {

    static Directory directory;

    static void loadFilesToIndex(IndexWriter writer) {
        // Load files to index
        InputStream inputStream = Demo.class.getResourceAsStream("/archive/papers.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                var fields = line.split(",");
                if (fields.length != 5) continue; // some fields are empty
                Document doc = new Document();
                doc.add(new Field("year", fields[1], TextField.TYPE_STORED));
                doc.add(new Field("title", fields[2], TextField.TYPE_STORED));
                doc.add(new Field("abstract", fields[3], TextField.TYPE_STORED));
                doc.add(new Field("full_text", fields[4], TextField.TYPE_STORED));

                writer.addDocument(doc);
            }
            System.out.println("Files loaded to index.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }


    public static void main(String[] args) throws IOException, ParseException {


        Analyzer analyzer;
        analyzer = new StandardAnalyzer();


        directory = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        loadFilesToIndex(iwriter);
        iwriter.close();

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        QueryParser parser = new QueryParser("title", analyzer);//note here we used the same analyzer object
        Query query = parser.parse("google");//test is am example for a search query
        TopDocs topDocs = isearcher.search(query, 10);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = isearcher.storedFields().document(scoreDoc.doc);
            System.out.println(document.get("title"));
            System.out.println(scoreDoc.toString());
        }

        System.out.println(topDocs.totalHits.value);
// Iterate through the results:

        ireader.close();
        directory.close();

    }
}
