package lucyna.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import lucyna.ReportException;
import lucyna.index.Constants;
import lucyna.index.IndexReading;
import lucyna.index.IndexWriting;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import org.apache.lucene.index.Term;

import org.apache.lucene.search.TermQuery;
import org.apache.tika.exception.TikaException;


public class IndexerMain {

    //Dodaje pliki z katalogu do indexu
    private static void addDirToInd(Path path) {

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs) {

                    try {

                        Ekstrakt eks = new Ekstrakt(dir);
                        IndexWriting.getInstance().addDoc(eks.toDocument());

                    } catch (TikaException | IOException e) {

                        try {

                            IndexWriting.getInstance().removeDoc(
                                    new Term(Constants.ABSPATH, path.toAbsolutePath().toString()));

                        } catch (IOException f) {
                            //Trying to back effect of previous action
                        }

                        ReportException.report(e, "Error: file " + dir + " hasn't beed add to index.");
                    }

                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (IOException e) {

            ReportException.report(e, "Error: Files haven't been added to index properly");
        }
    }


    //Usuwa pliki z katalogu z indexu
    private static void rmDirToInd(Path path) {

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs) {

                    try {

                        IndexWriting.getInstance().removeDoc(new Term(Constants.ABSPATH, dir.toAbsolutePath().toString()));

                    } catch (IOException e) {

                        ReportException.report(e, "Error: file " + dir + " hasn't beed removed from index.");
                    }

                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (IOException e) {

            ReportException.report(e, "Error: Files haven't been removed from index properly");
        }
    }



    //Zwraca liste sciezek (String) do monitorowanych katalogow
    private static List<String>  dirIndexed() {

        List<String> result = IndexReading.searchIndex(
                new TermQuery(new Term(Constants.ISINDDIR, Constants.YES)), Constants.INDEXDIR, 0);

        IndexReading.closeReader();

        return result;
    }


    public static void main(String[] args) {

        if (args.length == 0) {

            List<String> dirs = dirIndexed();

            //Pilnuje czy nie nastapilo Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {

                    try {

                        IndexWriting.getInstance().closeWriter();

                    } catch (IOException e) {

                        ReportException.report(e, "Error: Changes in index couldn't be saved properly");
                    }
                }
            });

            try {

                WatchDir w = new WatchDir();

                for (String i: dirs) {

                    w.registerAll(Paths.get(i));
                }

                //Rozpoczyna sledzenie
                w.processEvents();

            } catch (IOException e) {

                ReportException.report(e, "Error: Watching directions hasn't run properly");
            }


            return;

        } else if (args.length == 1) {

            switch (args[0]) {
                case "--purge": { //Czysci index

                    try {

                        IndexWriting writer = IndexWriting.getInstance();
                        writer.clearIndex();
                        writer.closeWriter();

                    } catch (IOException e) {

                        ReportException.report(e, "Error: Problems with index purging");
                    }

                    return;
                }
                case "--reindex": { //Ponowne indeksowanie

                    List<String> dirs = dirIndexed();

                    try {

                        IndexWriting writer = IndexWriting.getInstance();
                        writer.removeDoc(new Term(Constants.ISINDDIR, Constants.NO));

                        for (String i : dirs) {

                            addDirToInd(Paths.get(i));

                        }

                        writer.closeWriter();

                    } catch (IOException e) {

                        ReportException.report(e, "Error: Reindexing failed");
                    }

                    return;

                }
                case "--list": { //Wypisuje monitorowane katalogi

                    List<String> dirs = dirIndexed();

                    for (String i : dirs) {
                        System.out.println(i);
                    }

                    return;

                }
            }


        } else if (args.length == 2) {

            Path path = Paths.get(args[1]).toAbsolutePath();
            File tempFile = new File(path.toString());

            if (!tempFile.exists() || !tempFile.isDirectory()) {

                ReportException.report(new IOException(path.toString()), "Direction doesn't exist");
                return;
            }

            switch (args[0]) {
                case "--add": { //Dodaje to monitorowanych katalogow

                    Document newIndDir = new Document();
                    newIndDir.add(new TextField(Constants.INDEXDIR, path.toString(), Field.Store.YES));
                    newIndDir.add(new TextField(Constants.ISINDDIR, Constants.YES, Field.Store.NO));

                    try {

                        addDirToInd(path);

                        IndexWriting writer = IndexWriting.getInstance();
                        writer.addDoc(newIndDir);


                        writer.closeWriter();
                    } catch (IOException e) {

                        ReportException.report(e, "Error: Adding directory to index failed");
                    }

                    return;
                }

                case "--rm": { //Usuwa z monitorowanych katalogow

                    try {

                        IndexWriting writer = IndexWriting.getInstance();
                        long rem = writer.removeDoc(new Term(Constants.INDEXDIR, path.toString()));

                        if (rem < 1) {

                            System.err.println("! Direction hasn't been in index !");

                        } else {

                            rmDirToInd(path);

                        }


                        writer.closeWriter();
                        return;

                    } catch (IOException e) {

                        ReportException.report(e, "Error: Removing directory failed");
                    }
                }
            }
        }

        System.err.println("Wrong input");
    }
}
