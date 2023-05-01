package pt.jorgeduarte.domain.services;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class WikipediaJdomService {

    public String fetchAuthorInfoWithJdom2(String author) {
        try {
            String url = "https://pt.wikipedia.org/wiki/" + author.replace(" ", "_");
            Connection.Response response = Jsoup.connect(url).execute();
            String html = response.body();

            SAXBuilder saxBuilder = new SAXBuilder();
            InputStream stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
            Document document = saxBuilder.build(stream);

            Element bodyContent = document.getRootElement().getChild("body").getChild("div").getChild("div");
            List<Element> elements = bodyContent.getChildren("p");

            for (Element element : elements) {
                if (element.getText().contains("Nascimento")) {
                    return "Data de nascimento: " + element.getText().split("Nascimento:")[1].split("\\.")[0].trim();
                }
            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
        return null;
    }


}
