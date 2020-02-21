# "Lucyna" - indekser i wyszukiwarka

Projekt zaliczeniowy na przedmiot "Programowanie obiektowe" na studiach informatycznych na Uniwersytecie Warszawskim.
Zawiera implementacje dwóch aplikacji konsolowych:
* **indekser** - monitorujący wszelkie zmiany w dodanych do indeksu katalogach i ektraktujący znajdujące się w nich dokumenty w formatach TXT, PDF, RTF, ODF, OOXML
* **wyszukiwarka** - wyszukująca termy i frazy w indeksie

**Narzędzia:**
Java 11, Apache Lucene, Apache Maven, Apache Tika

### Indekser - tryby pracy

Indekser rozpoczyna pracę w domyślnym trybie, jeśli przy jego uruchamianiu nie podano żadnych argumentów w linii poleceń.

Domyślnym trybem pracy indeksera jest monitorowanie katalogów, których zawartość została dodana do indeksu i aktualizacja tego indeksu. Program działa w nieskończonej pętli czekając na informacje o zmianach w systemie pliku. Wciśnięcie kombinacji klawiszy `Ctrl+C` powinno powodować zakończenie pracy indeksera.

#### Możliwe argumenty indeksera:

* `--purge` - usuwa wszystkie dokumenty z indeksu i kończy pracę indeksera
* `--add <dir>` - dodaje katalog, którego ścieżka jest podana w argumencie `<dir>` do zbioru indeksowanych i monitorowanych katalogów. poddrzewa katalogów dodawanych do indeksu muszą być rozłączne (zgodnie z wymaganiami funkcjonalnymi), następnie kończy pracę indeksera
* `--rm <dir>` - powoduje usunięcie katalogu podanego w argumencie `<dir>` ze zbioru indeksowanych katalogów oraz usunięcie dokumentów w indeksie odpowiadających plikom znajdującym się w poddrzewie katalogu, następnie kończy pracę indeksera
* `--reindex` - powoduje usunięcie wszystkich dokumentów odpowiadających plikom z indeksu i wykonanie ponownego indeksowania zawartości wszystkich katalogów dodanych wcześniej do indeksu, następnie kończy pracę indeksera
* `--list` - powoduje wypisanie listy katalogów znajdujących się w indeksie (dodanych za pomocą `--add`), następnie kończy pracę indeksera

Indekser może być uruchamiany z maksymalnie jednym z powyższych argumentów. 

### Wyszukiwarka

Wyszukiwarka działa w trybie interaktywnym, w nieskończonej pętli wykonuje następujące kroki: wczytuje polecenie od użytkownika, wykonuje polecenie i wypisuje wyniki. 

Aby zakończyć pracę programu należy użyć kombinacji klawiszy `Ctrl+C`.

#### Polecenia wyszukiwarki

Polecenia dzielimy na dwie kategorie: polecenia sterujące zaczynające się od znaku `%` oraz zapytania.

Wyróżniamy następujące polecenia sterujące:
* `%lang en/pl` - powoduje ustawienie wybranego języka używanego przy zapytaniach
* `%details on/off` - powoduje włączenie lub wyłączenie wyświetlania kontekstu wystąpienia wyszukiwanego termu lub frazy w tekście
* `%limit <n>` - powoduje ograniczenie liczby wyników wyszukiwania do co najwyżej `n` dokumentów, jeżeli `n == 0` to przeglądane są wszystkie dokumenty
* `%color on/off` - powoduje włączenie lub wyłączenie wyróżniania ("podświetlania") znalezionych termów/fraz w wyświetlanym kontekście
* `%term` - powoduje włączenie trybu wyszukiwania pojedyńczego termu
* `%phrase` - powoduje wybranie trybu wyszukiwania frazy, czyli ciągu termów
* `%fuzzy` - powoduje wybranie tryby wyszukiwania rozmytego
Trybom wyszukiwania odpowiadają klasy z biblioteki Lucene: `TermQuery`, `PhraseQuery`, `FuzzyQuery`

