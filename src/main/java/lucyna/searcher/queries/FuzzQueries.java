package lucyna.searcher.queries;

import lucyna.index.Language;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;


//Generatora kwerend do wyszukiwania przy uzyciu FuzzyQuery
public class FuzzQueries extends AbstQuer {

    //Konstruktor fasady
    public FuzzQueries() {

        super();

    }


    //Konstruktor uzywany do generatora
    private FuzzQueries(Language lang, String content) {

        super(new FuzzyQuery(new Term(lang.getName(), content)),
                new FuzzyQuery(new Term(lang.getTerm(), content)));

    }


    //Metoda generujaca kwerende z interfejsu
    @Override
    public Query generator(Language lang, String content) {

        return new FuzzQueries(lang, content).buildQuery();

    }

}
