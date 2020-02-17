package lucyna.index;


import lucyna.ReportException;
import lucyna.index.IndexConnection;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;


//Klasa z metodami do modyfikacji indexu. Wzorzec obiektowy- singleton
public class IndexWriting {

    private static IndexWriting instance;

    private IndexConnection conn;
    private IndexWriter writer;


    //Prywatny konstruktor singletona
    private IndexWriting() throws IOException {

        if (instance != null) {
            throw new IllegalStateException("Cannot create new instance, please use getInstance method instead.");
        }

        conn = IndexConnection.getInstance();
        writer = conn.getWriter();

    }


    //Zwraca instancje klasy-singletona
    public static IndexWriting getInstance() throws IOException {

        if (instance == null) {
            instance = new IndexWriting();
        }

        return instance;
    }


    //Dodaje dokument do indexu
    public void addDoc(Document d) {

        try {
            writer.addDocument(d);

        } catch (IOException e) {

            ReportException.report(e, "Document " + d.get(Constants.NAME)
                                                + " hasn't been added to index properly");
        }
    }


    //Aktualizuje dokument w indexie
    public void updateDoc(Term term, Document d) {

        try {
            writer.updateDocument(term, d);

        } catch (IOException e) {

            ReportException.report(e, "Document " + d.get(Constants.NAME) + " hasn't been updated properly");
        }
    }


    //Usuwa dokument z indexu
    public long removeDoc(Term term) {

        try {
            return writer.deleteDocuments(term);

        } catch (IOException e) {

            ReportException.report(e, "Error: Document(s) " + term + " hasn't been removed properly");
        }

        return 0;
    }


    //Czysci index
    public void clearIndex() {

        try {

            writer.deleteAll();

        } catch (IOException e) {

            ReportException.report(e, "Index hasn't beed cleared properly");
        }
    }


    //Zamyka polaczenie z indeksem
    public void closeWriter() {

        try {

            writer.close();
            conn.closeConnection();

        } catch (IOException e) {

            ReportException.report(e, "Index hasn't been saved properly");
        }
    }

}
