package lucyna.index;

import lucyna.ReportException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class IndexReading {

    private static IndexReader indexReader;


    //Zwraca instancje czytnika indeksu (klasa IndexReader)
    private static IndexReader getReader() throws IOException {


        if (indexReader == null) {
            IndexConnection conn = IndexConnection.getInstance();
            indexReader = conn.getReader();
        }

        return indexReader;
    }


    //Zamyka otwarta instancje czytnika
    public static void closeReader() {

        if (indexReader != null) {

            try {
                indexReader.close();
            } catch (IOException e) {

                ReportException.report(e, "Couldn't close reader");
            }
        }
    }


    //Przeszukuje indeks przy uzyciu @query, nastepnie zwraca zawartosc
    // pola @field ze znalezionych dokumentow; @n - liczba oczekiwanych rezultatow
    public static List<String> searchIndex(Query query, String field, int n) {

        if (n == 0) {

            n = Integer.MAX_VALUE;
        }

        try (IndexReader indexReader = getReader()) {

            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, n);

            List<String> result = new ArrayList<>();

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

                Document d = searcher.doc(scoreDoc.doc);
                result.add(d.get(field));

            }

            return result;

        } catch (IOException e) {

            ReportException.report(e, "Errors during searching in index");
        }

        return new ArrayList<>();
    }


    //Wykonuje kwerende do indexu przy uzyciu @query, zwraca pierwsze @n wynikow
    public static TopDocs searchIndex(Query query, int n) {

        if (n == 0) {

            n = Integer.MAX_VALUE;
        }

        try {

            IndexSearcher searcher = new IndexSearcher(getReader());
            TopDocs res = searcher.search(query, n);

            return res;

        } catch (IOException e) {

            ReportException.report(e, "Errors during searching in index");
        }

        return new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO),
                            new ScoreDoc[0]);

    }


    //Zwraca dokument z indexu o podanym identyfikatorze @docID
    public static Document getDocById(int docID) {

        try {

            IndexReader indexReader = getReader();
            return indexReader.document(docID);

        } catch (IOException e) {

            ReportException.report(e, "Errors during searching in index");
        }

        return new Document();
    }


    //Podaje calkowita liczbe dokumentow w indexie
    public static int countDocIndex() {

        try {

            IndexReader indexReader = getReader();
            return indexReader.numDocs();

        } catch (IOException e) {

            ReportException.report(e, "Couldn't count documents in index");
        }

        return 0;
    }

}
