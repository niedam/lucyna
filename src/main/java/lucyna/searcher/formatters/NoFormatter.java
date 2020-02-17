package lucyna.searcher.formatters;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;


//Formatter bez formatowania dla Highlighter
public class NoFormatter implements Formatter {

    //Metoda z interfejsu, co robic z termami
    @Override
    public String highlightTerm(String originalText, TokenGroup tokenGroup) {

        return originalText;
    }
}
