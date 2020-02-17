package lucyna.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;

public enum Language {

    EN("english-name", "english-term", new EnglishAnalyzer()),
    PL("polish-name", "polish-term", new PolishAnalyzer());

    private String name;
    private String term;
    private Analyzer analyzer;

    Language(String name, String term, Analyzer analyzer) {

        this.name = name;
        this.term = term;
        this.analyzer = analyzer;

    }

    public String getName() {
        return name;
    }


    public String getTerm() {
        return term;
    }


    public Analyzer getAnalyzer() {
        return analyzer;
    }

}
