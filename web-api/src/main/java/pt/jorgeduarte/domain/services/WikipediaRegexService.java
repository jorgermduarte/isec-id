package pt.jorgeduarte.domain.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import pt.jorgeduarte.domain.entities.Author;

import java.io.IOException;
import org.jsoup.select.Elements;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WikipediaRegexService {
    public Author fetchAuthorInfoWithRegex(String author)  {
        try {
            String url = "https://pt.wikipedia.org/wiki/" + author.replace(" ", "_");
            Document doc = Jsoup.connect(url).get();

            Author authorObj = new Author();
            authorObj.setFullName(author);
            authorObj.setWikipediaUrl(url);

            String contentInfo = doc.select(".infobox").text();

            // Birthdate
            Pattern birthDatePattern = Pattern.compile("(?<=Nascimento\\s)(\\d{1,2}\\sde\\s[a-zA-Z]+\\sde\\s\\d{4})");
            Matcher birthDateMatcher = birthDatePattern.matcher(contentInfo);
            if (birthDateMatcher.find()) {
                String birthDateStr = birthDateMatcher.group();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "PT"));
                    LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
                    authorObj.setBirthDateString(birthDate.toString());
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }

            // Death date
            Pattern deathDatePattern = Pattern.compile("(?<=Morte\\s)(\\d{1,2}\\sde\\s[a-zA-Z]+\\sde\\s\\d{4})");
            Matcher deathDateMatcher = deathDatePattern.matcher(contentInfo);
            if (deathDateMatcher.find()) {
                String deathDateStr = deathDateMatcher.group();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "PT"));
                    LocalDate deathDate = LocalDate.parse(deathDateStr, formatter);
                    authorObj.setDeathDateString(deathDate.toString());
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }

            // Nationality
            Pattern nationalityPattern = Pattern.compile("Nacionalidade\\s*([\\p{L}-]+)");
            Matcher nationalityMatcher = nationalityPattern.matcher(contentInfo);
            if (nationalityMatcher.find()) {
                authorObj.setNationality(nationalityMatcher.group(1).trim());
            }

            // Biography
            Elements biographyElements = doc.select("#mw-content-text > div.mw-parser-output > p");
            String biography = "";
            for (int i = 0; i < biographyElements.size(); i++) {
                String paragraph = biographyElements.get(i).text();
                if (!paragraph.isEmpty()) {
                    biography += paragraph + "\n";
                } else {
                    break;
                }
            }

            biography = biography.replaceAll("\\[\\d+]", "").trim();
            authorObj.setBiography(biography);

            // Image url
            Elements imageElements = doc.select(".infobox img");
            List<Element> filteredElements = imageElements.stream().filter(element -> element.absUrl("src").toLowerCase().contains("jpg")).toList();
            if (!filteredElements.isEmpty()) {
                String imageUrl = filteredElements.get(0).absUrl("src");
                authorObj.setCoverImageUrl(imageUrl);
            }

            Element infoBox = doc.select(".infobox_v2").first();

            if(infoBox != null){
                Elements rows = infoBox.select("tr");
                for (Element row : rows) {
                    Elements headerElements = row.select("td[scope=row]");
                    if (!headerElements.isEmpty()) {
                        String headerText = headerElements.first().text().trim();

                        if (headerText.contains("Ocupação")) {
                            Elements occupationElements = row.select("td").get(1).select("a");
                            List<String> occupations = occupationElements.eachText();
                            authorObj.setOccupations(occupations);
                        }
                        else if (headerText.contains("Prémios")) {
                            String prizesText = row.select("td").get(1).ownText();
                            Elements prizeLinkElements = row.select("td").get(1).select("a");

                            List<String> prizes = new ArrayList<>();
                            String[] prizesParts = prizesText.split(",");
                            for (String prizePart : prizesParts) {
                                String trimmedPrizePart = prizePart.trim();
                                if (!trimmedPrizePart.isEmpty() && !trimmedPrizePart.startsWith("[") && !trimmedPrizePart.startsWith("(") && !trimmedPrizePart.isEmpty()) {
                                    prizes.add(trimmedPrizePart);
                                }
                            }

                            for (Element prizeElement : prizeLinkElements) {
                                String trimmedText = prizeElement.text().trim();
                                if(!trimmedText.startsWith("[") && !trimmedText.startsWith("(") && !trimmedText.isEmpty()){
                                    prizes.add(trimmedText);
                                }
                            }

                            authorObj.setPrizes(prizes);
                        } else if (headerText.contains("Movimento literário")) {
                            Elements genreElements = row.select("td").get(1).select("a");
                            List<String> genre = genreElements.eachText();
                            genre.removeIf(g -> g.isEmpty());

                            authorObj.setLiteraryGenre(String.join(", ", genre));
                        } else if (headerText.contains("Gênero literário")) {
                            Elements genreElements = row.select("td").get(1).select("a");
                            List<String> genres = genreElements.eachText();
                            genres.removeIf(g -> g.isEmpty());
                            authorObj.setLiteraryGenre(String.join(", ", genres));
                        }
                    }
                }
            }

            return authorObj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
