package lucyna.searcher.queries;

import lucyna.index.Language;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;


//Generator kwerend przy uzyciu obiektu klasy TermQuery
public class TermQueries extends AbstQuer {

    //Fasada generatora
    public TermQueries() {

        super();

    }


    //Konstruktor budujacy generator
    private TermQueries(Language lang, String content) {

        super(new TermQuery(new Term(lang.getName(), content)),
                new TermQuery(new Term(lang.getTerm(), content)));

    }


    //Metoda budujaca kwerende z interfejsu
    @Override
    public Query generator(Language lang, String content) {

        return new TermQueries(lang, content).buildQuery();

    }

}
