package pt.jorgeduarte.domain.services;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BertrandJdomService {

    private final AuthorService authorService;

    public BertrandJdomService(AuthorService authorService) {
        this.authorService = authorService;
    }

    public List<Book> fetchBooksFromAuthor(long authorId) {

        Author author = authorService.findAuthorById(authorId).get();
        List<Book> books = new ArrayList<>();

        if (author != null) {
            String authorName = author.getFullName().replace(" ", "+");
            String url = "https://www.bertrand.pt/pesquisa/" + authorName;

            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .referrer("https://www.google.com/")
                        .get();
                List<String> bookUrls = extractBookUrls(document);

                for (String bookUrl : bookUrls) {
                    Book book = fetchBookDetails(bookUrl, authorId);
                    books.add(book);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return books;
    }

    private Book fetchBookDetails(String bookUrl, Long authorId) {
        Book book = new Book();
        book.setAuthorId(authorId);

        try {
            Document document = Jsoup.connect(bookUrl).get();

            // Extrair informações do livro
            Element titleElement = document.selectFirst("#productPageSectionDetails-collapseDetalhes-content-title");
            String title = titleElement.text();

            String[] urlParts = bookUrl.split("/");
            String bertrandBookId = urlParts[urlParts.length - 1];

            // ISBN do livro
            String isbn = document.selectFirst(".info-area span:contains(ISBN) .info").text();

            Element yearElement = document.selectFirst("#productPageSectionDetails-collapseDetalhes-content-year .info");
            String year = yearElement.text().trim();

            Element publisherElement = document.selectFirst("span[itemtype='https://schema.org/Organization'] .info");
            String publisher = publisherElement.text();

            Element languageElement = document.selectFirst("#productPageSectionDetails-collapseDetalhes-content-language .info");
            String language = languageElement.text();

            Element pagesElement = document.selectFirst("#productPageSectionDetails-collapseDetalhes-content-nrPages .info");
            Long pages = Long.parseLong(pagesElement.text());

            String publicationDateString = document.select("#productPageSectionDetails-collapseDetalhes-content-year .info").text();
            // Sinopse do livro
            String synopsis = document.select("#productPageSectionAboutBook-sinopse p").text();

            // Capa do livro (imagem)
            String coverUrl = document.select("#productPageLeftSectionTop-images picture img").attr("src");

            book.setDescription(synopsis);
            book.setPublicationDateString(publicationDateString);
            book.setTitle(title);
            book.setIsbn(isbn);
            book.setAuthorId(authorId);
            book.setPublisher(publisher);
            book.setLanguage(language);
            book.setPages(pages);
            book.setBertrandUrl(bookUrl);
            book.setCoverImageUrl(coverUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }
    private List<String> extractBookUrls(Document document) {
        List<String> bookUrls = new ArrayList<>();

        Elements bookElements = document.select(".title-lnk");

        for (Element bookElement : bookElements) {
            String bookUrl = bookElement.attr("abs:href");
            bookUrls.add(bookUrl);
        }

        return bookUrls;
    }
}
