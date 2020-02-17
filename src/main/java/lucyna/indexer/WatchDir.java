package lucyna.indexer;

import java.nio.file.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

import lucyna.ReportException;
import lucyna.index.Constants;
import lucyna.index.IndexWriting;
import org.apache.lucene.index.Term;
import org.apache.tika.exception.TikaException;


//Klasa do sledzenia katalogow
class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private static IndexWriting writer;


    //Konstruktor klasy sledzacej
    WatchDir() throws IOException {

            watcher = FileSystems.getDefault().newWatchService();
            writer = IndexWriting.getInstance();
            keys = new HashMap<WatchKey, Path>();
    }


    //Zmiana typu
    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {

        return (WatchEvent<T>) event;
    }


    //Rejestruje katalog do sledzenia o sciezce @path
    private void register(Path dir) {

        try {

            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            keys.put(key, dir);

        } catch (IOException e) {

            ReportException.report(e, "Direction " + dir + " hasn't been added to index.");
        }
    }


    //Rejestruje do sledzenia katalog o sciezce @path i jego podkatalogi
    void registerAll(Path start) {

        try {

            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {

            ReportException.report(e, "Error during walk file tree");
        }
    }


    //Rozpoczyna sledzenie katalogu
    void processEvents() {

        for (;;) {

            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name).toAbsolutePath();


                try {
                    if (kind == ENTRY_CREATE) {

                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        } else {

                            try {
                                Ekstrakt e = new Ekstrakt(child);
                                writer.addDoc(e.toDocument());
                            } catch (TikaException e) {

                                ReportException.report(e, "File " + child + " hasn't been added to index.");
                                IndexWriting.getInstance().removeDoc(
                                        new Term(Constants.ABSPATH, child.toAbsolutePath().toString()));

                            }
                        }

                    } else if (kind == ENTRY_DELETE) {

                        if (!Files.isDirectory(child, NOFOLLOW_LINKS)) {

                            writer.removeDoc(new Term(Constants.ABSPATH, child.toString()));
                        }

                    } else if (kind == ENTRY_MODIFY) {

                        if (!Files.isDirectory(child, NOFOLLOW_LINKS)) {

                            try {

                                Ekstrakt e = new Ekstrakt(child);
                                writer.updateDoc(new Term(Constants.ABSPATH, child.toString()), e.toDocument());

                            } catch (TikaException e) {
                                ReportException.report(e, "File " + child + " hasn't been updated.");
                            }
                        }
                    }

                } catch (IOException e) {

                    ReportException.report(e, "Action with file " + child + " hasn't been registered");
                }

            }

            boolean valid = key.reset();

            if (!valid) {
                keys.remove(key);

                if (keys.isEmpty()) {
                    break;
                }
            }

        }
    }
}
