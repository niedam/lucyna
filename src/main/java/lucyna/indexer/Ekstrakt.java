package lucyna.indexer;

import lucyna.index.Constants;
import lucyna.index.Language;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


class Ekstrakt {

    private final String content;
    private final String fileName;
    private final String filePath;
    private final String contentType;


    //Przy uzyciu fasady Tika zdobywa zawartosc pliku o sciezce @path
    Ekstrakt(Path path) throws IOException, TikaException {

        Tika tika = new Tika();
        Metadata metadata = new Metadata();

        File f = new File(path.toString());

        if (!Files.isRegularFile(path)) {
            throw new IOException("Not regular file");
        }

        fileName = f.getName();
        filePath = path.toAbsolutePath().toString();

        InputStream stream = new FileInputStream(f);
        content = tika.parseToString(stream, metadata);

        contentType = metadata.get("Content-Type");
    }


    //Zwraca zawartosc pliku
    String getContent() {
        return content;
    }


    //Zwraca nazwe pliku
    String getName() {
        return fileName;
    }


    //Zwraca sciezke do pliku
    String getFilePath() {
        return filePath;
    }


    //Zwraca typ pliku
    String getContentType() {
        return contentType;
    }


    //Generuje dokument Lucene z ekstraktu wyciagnietego przez Tika
    Document toDocument() {

        Document result = new Document();

        result.add(new TextField(Constants.ABSPATH, getFilePath(), Field.Store.YES));

        result.add(new TextField(Constants.NAME, getName(), Field.Store.YES));
        result.add(new TextField(Constants.TERM, getContent(), Field.Store.YES));

        result.add(new TextField(Language.EN.getName(), getName(), Field.Store.NO));
        result.add(new TextField(Language.EN.getTerm(), getContent(), Field.Store.NO));

        result.add(new TextField(Language.PL.getName(), getName(), Field.Store.NO));
        result.add(new TextField(Language.PL.getTerm(), getContent(), Field.Store.NO));

        result.add(new TextField(Constants.ISINDDIR, Constants.NO, Field.Store.NO));

        return result;
    }

}
