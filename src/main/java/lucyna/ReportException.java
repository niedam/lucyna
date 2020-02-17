package lucyna;

public class ReportException {

    //Funkcja przechwytujaca wyjatki
    public static void report(Exception e, String message) {

        System.err.println(message);
        System.err.println(e.getLocalizedMessage());

    }

}
