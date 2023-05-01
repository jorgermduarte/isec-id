package pt.jorgeduarte.domain.services;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class WookRegexService {
    public void fetchBookInfoWithRegex(String bookUrl) {
        try {
            Document doc = Jsoup.connect(bookUrl).get();

            String title = doc.select("h1.fn").text();

            String author = doc.select("div.author > a").text();

            System.out.println("Título: " + title);
            System.out.println("Autor: " + author);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
