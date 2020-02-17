package lucyna.searcher.queries;


import lucyna.index.Language;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;


//Klasa generujaca kwerendy PhraseQuery
public class PhraQueries extends AbstQuer {

    //Konstruktor fasady
    public PhraQueries() {

        super();

    }


    //Konstruktor pomocny przy generowaniu kwerendy
    private PhraQueries(Language lang, String content) {

        super(makePraQ(lang.getName(), content), makePraQ(lang.getTerm(), content));

    }


    //Praser do PhraseQuery, @field - pole w ktorym sie wyszukuje
    //@content - szukana wartosc
    private static PhraseQuery makePraQ(String field, String content) {

        PhraseQuery.Builder builder = new PhraseQuery.Builder();

        int licz = 0;

        //Rozdziela spacjami
        for (String i: content.split(" ")) {

            builder.add(new Term(field, i), licz);
            licz++;
        }

        return builder.build();

    }


    //Metoda generujaca z interfejsu
    @Override
    public Query generator(Language lang, String content) {

        return new PhraQueries(lang, content).buildQuery();

    }

}

