package lucyna.searcher;

import lucyna.index.Constants;
import lucyna.index.IndexReading;
import lucyna.index.Language;

import lucyna.searcher.formatters.*;
import lucyna.searcher.queries.*;


import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;


//Interfejs komunikacji z programem, logika Wyszukiwarki
class Searcher {

    private static Language lang = Language.EN;
    private static boolean details = false;
    private static Formatter color = new NoFormatter();
    private static int limit = 0;
    private static InterfaceQuery queryGen = new TermQueries();

    private Terminal terminal;


    //Konstruktor klasy
    Searcher(Terminal terminal) {
        this.terminal = terminal;
    }


    //Metoda pobierajaca wejscie i wykonujaca polecania sterujace / wyszukiwania
    void execute(String command) {

        String[] args = command.split(" ");

        if (args.length == 2) {
            switch (args[0]) {

                case "%lang": { //Zmiana jezyka wyszukiwarki

                    if (args[1].equals("en")) {

                        lang = Language.EN;

                    } else if (args[1].equals("pl")) {

                        lang = Language.PL;

                    } else {

                        System.err.println("Wrong language");

                    }
                    return;
                }

                case "%details": { //Wlaczenie / wylaczanie kontekstu

                    if (args[1].equals("on")) {

                        details = true;

                    } else if (args[1].equals("off")) {

                        details = false;

                    } else {

                        System.err.println("Wrong option");
                    }

                    return;
                }

                case "%color": { // Wlaczenie / wylaczanie koloru

                    if (args[1].equals("on")) {

                        color = new RedFormatter();

                    } else if (args[1].equals("off")) {

                        color = new NoFormatter();

                    } else {

                        System.err.println("Wrong option");
                    }

                    return;

                }

                case "%limit": { // Ustawianie limitu wynikow wyszukiwan

                    limit = Integer.parseInt(args[1]);
                    return;

                }
            }

        } else if (args.length == 1) {

            switch (args[0]) {

                case "%term": { // Wlaczenie trybu term

                    queryGen = new TermQueries();
                    return;
                }
                case "%phrase": { // Wlaczenie trybu phrase

                    queryGen = new PhraQueries();

                    return;
                }
                case "%fuzzy": { // Wlaczenie trybu fuzzy

                    queryGen = new FuzzQueries();
                    return;
                }
            }
        }

        if (command.charAt(0) == '%') { // Zle polecenie sterujace

            System.err.println("Wrong command or parameter(s)");
            return;
        }

        //Fabryka queryGen wykonuje kwerende wlasciwego typu
        Query quer = queryGen.generator(lang, command);

        //Wyszukanie w indexie
        TopDocs res = IndexReading.searchIndex(quer, limit);

        //Wypisuje ilosc pasujacych dokumentow
        terminal.writer().print(new AttributedStringBuilder().append("File count: ")
                .style(AttributedStyle.DEFAULT.bold())
                .append(Long.toString(res.totalHits.value))
                .append('\n')
                .toAnsi());

        for (ScoreDoc i: res.scoreDocs) {

            Document doc = IndexReading.getDocById(i.doc);

            //Wypisuje sciezke do pliku
            terminal.writer().println(new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.bold())
                    .append(doc.get(Constants.ABSPATH))
                    .toAnsi());

            //Wypisywanie kontekstu
            if (details) {

                QueryScorer scorer = new QueryScorer(quer);

                Highlighter highlighter = new Highlighter(color, scorer);
                Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 50);
                highlighter.setTextFragmenter(fragmenter);


                String[] frags = new String[0];

                try {

                    frags = highlighter.getBestFragments(lang.getAnalyzer(), lang.getTerm(),
                                                            doc.get(Constants.TERM), 10);

                } catch (IOException | InvalidTokenOffsetsException e) {

                    e.printStackTrace();

                } catch (IllegalArgumentException e) {

                    System.err.println("Highlighter error: " + e.getMessage());
                }

                if (frags.length == 0) {

                    terminal.writer().println(new AttributedStringBuilder()
                            .style(AttributedStyle.DEFAULT.italic())
                            .append(" (no context) ")
                            .toAnsi());

                    continue;
                }

                terminal.writer().print(frags[0]);

                for (int j = 1; j < frags.length; j++) {

                    terminal.writer().print(" ... " +  frags[j]);

                }

                terminal.writer().print('\n');

            }

        }

    }

}
