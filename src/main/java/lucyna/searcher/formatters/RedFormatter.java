package lucyna.searcher.formatters;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;


//Formatter formatujacy pasujace slowa na czerwono
public class RedFormatter implements Formatter {


    //Metoda wybiera pasujace slowa i zaznacza je na czerwono, z interfejsu
    @Override
    public String highlightTerm(String originalText, TokenGroup tokenGroup) {

        if (tokenGroup.getTotalScore() <= 0) {
            return originalText;
        }

        AttributedStringBuilder returnBuilder = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .append(originalText);

        return returnBuilder.toAnsi();
    }

}
