package lucyna.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


//Klasa dostarcza narzedzi potrzebnych do polaczenia z indexem. Wzorzec singleton.
class IndexConnection {

    private static IndexConnection instance;
    private Directory memoryIndex;
    private PerFieldAnalyzerWrapper wrapper;


    //Konstruuje instancje singletonu: wrapper oraz lokalizacje indexu w pamieci.
    private IndexConnection() throws IOException {

        String homePath = System.getProperty("user.home");
        memoryIndex = FSDirectory.open(Paths.get(homePath + "/.index"));

        Analyzer engAnaly = Language.EN.getAnalyzer();
        Analyzer polAnaly = Language.PL.getAnalyzer();
        Analyzer keyAnaly = new KeywordAnalyzer();

        Map<String, Analyzer> analyzerMap = new HashMap<>();

        analyzerMap.put(Language.EN.getName(), engAnaly);
        analyzerMap.put(Language.PL.getName(), polAnaly);

        analyzerMap.put(Language.EN.getTerm(), engAnaly);
        analyzerMap.put(Language.PL.getTerm(), polAnaly);

        analyzerMap.put(Constants.ABSPATH, keyAnaly);

        analyzerMap.put(Constants.INDEXDIR, keyAnaly);
        analyzerMap.put(Constants.ISINDDIR, keyAnaly);

        wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);

    }


    //Zwraca instancje klasy
    static IndexConnection getInstance() throws IOException {
        if (instance == null) {
            instance = new IndexConnection();
        }

        return instance;
    }


    //Zamyka polaczenie z lokalizacja indeksu w pamieci
    void closeConnection() throws IOException{
        memoryIndex.close();
    }


    //Zwraca klase IndexWriter do modyfikowania w indexie
    IndexWriter getWriter() throws IOException {
        return new IndexWriter(memoryIndex, new IndexWriterConfig(wrapper));
    }


    //Zwraca klase IndexReader do czytania z indexu
    IndexReader getReader() throws IOException {
        return DirectoryReader.open(memoryIndex);
    }

}
