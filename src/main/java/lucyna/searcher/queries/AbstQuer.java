package lucyna.searcher.queries;

import lucyna.index.Language;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;


//Abstrakcyjna klasa implementujaca wspolne metody dla generatorow kwerend uzywanych przez wyszukiwarke
//Kod w podklasach zastal ograniczony do minimum (bez copy-pasty)
abstract class AbstQuer implements InterfaceQuery {

    private Query queryName;
    private Query queryCont;


    //Konstruktor dla fasady generatora
    protected AbstQuer() { }


    //Konstruktor do generowania kwerend
    protected AbstQuer(Query queryName, Query queryCont) {

        this.queryName = queryName;
        this.queryCont = queryCont;

    }


    //Metoda generujaca kwerende z interfejsu
    public abstract Query generator(Language lang, String content);


    //Wspolna dla wszystkich generatorow metoda skladajaca dwie kwerendy w jedna
    protected Query buildQuery() {

        BooleanQuery query = new BooleanQuery.Builder()
                .add(queryName, BooleanClause.Occur.SHOULD)
                .add(queryCont, BooleanClause.Occur.SHOULD)
                .build();

        return query;

    }

}
