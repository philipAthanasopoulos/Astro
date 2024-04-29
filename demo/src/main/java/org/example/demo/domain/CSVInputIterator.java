package org.example.demo.domain;

import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Philip Athanasopoulos
 */
public class CSVInputIterator implements InputIterator {

    private final Iterator<CSVRecord> csvRecordIterator;

    public CSVInputIterator(Iterator<CSVRecord> csvRecordIterator) {
        this.csvRecordIterator = csvRecordIterator;
    }

    @Override
    public long weight() {
        return 0;
    }

    @Override
    public BytesRef payload() {
        return null;
    }

    @Override
    public boolean hasPayloads() {
        return false;
    }

    @Override
    public Set<BytesRef> contexts() {
        return null;
    }

    @Override
    public boolean hasContexts() {
        return false;
    }

    @Override
    public BytesRef next() throws IOException {
        if (!csvRecordIterator.hasNext()) {
            return null;
        }
        // Assuming the title field is what you want to suggest
        return new BytesRef(csvRecordIterator.next().get(2));
    }
}
