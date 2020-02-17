package lucyna.searcher.queries;

import lucyna.index.Language;

import org.apache.lucene.search.Query;


//Wspolny interfejs dla generatorow kwerend
public interface InterfaceQuery {

    //Generuje odpowiednia kwerende
    //@lang - enumerator jezyka; @content - szukana wartosc
    Query generator(Language lang, String content);

}
