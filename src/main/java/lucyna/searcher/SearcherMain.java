package lucyna.searcher;

import lucyna.ReportException;
import lucyna.index.IndexReading;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;


//Glowna klasa Wyszukiwarki
public class SearcherMain {


    public static void main(String[] args) {

        //Zamyka index w przypadku, gdy wcisnieto Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                IndexReading.closeReader();

            }
        });

        try (Terminal terminal = TerminalBuilder.builder()
                .jna(false)
                .jansi(true)
                .build()) {

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            Searcher searcher = new Searcher(terminal);

            while (true) {

                String line = null;

                try {

                    line = lineReader.readLine("> ");
                    searcher.execute(line);

                } catch (UserInterruptException | EndOfFileException e) {

                    ReportException.report(e, "Error: Terminal reader interruption");
                }
            }

        } catch (IOException e) {

            ReportException.report(e, "Error: Terminal can't work");
        }

    }
}

