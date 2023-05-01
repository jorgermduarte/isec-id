package pt.jorgeduarte.domain.services;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.Book;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WookJdomService {
    private final AuthorService authorService;

    public WookJdomService(AuthorService authorService) {
        this.authorService = authorService;
    }
    public List<Book> fetchBooksFromAuthor(Long authorId) {
        Author author = authorService.findAuthorById(authorId).get();
        List<Book> books = new ArrayList<>();

        if (author != null) {
            String authorName = author.getFullName().replace(" ", "+");
            String url = "https://www.wook.pt/pesquisa/" + authorName;

            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                        .referrer("http://www.google.com")
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
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

    private List<String> extractBookUrls(Document document) {
        List<String> bookUrls = new ArrayList<>();
        Elements productDivs = document.select(".col-xs-12 .product");

        for (Element productDiv : productDivs) {
            Element bookLink = productDiv.selectFirst(".product-img a");
            if (bookLink != null) {
                String bookUrl = bookLink.attr("href");
                if (!bookUrl.isEmpty()) {
                    bookUrls.add("https://www.wook.pt" + bookUrl);
                }
            }
        }


        return bookUrls;
    }

    private Book fetchBookDetails(String bookUrl, Long authorId) {
        Book book = new Book();
        book.setAuthorId(authorId);
        // TODO: Extrair informações do livro
        return book;
    }
}
