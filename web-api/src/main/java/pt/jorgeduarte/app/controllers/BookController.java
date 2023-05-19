package pt.jorgeduarte.app.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.services.BertrandJdomService;
import pt.jorgeduarte.domain.services.BookService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    BertrandJdomService bertrandJsonService;
    BookService bookService;

    public BookController(BookService bookService, BertrandJdomService bertrandJsonService) {
        this.bookService = bookService;
        this.bertrandJsonService = bertrandJsonService;
    }

    @RequestMapping("/{id}")
    public Book getBookByID(@PathVariable Long id){
        return bookService.findBookById(id).orElseThrow(() -> new RuntimeException("Book not found!"));
    }
    @RequestMapping("/filter/{filterType}")
    public ResponseEntity<List<Book>> filterAuthors(@PathVariable String filterType, @RequestParam(required = false) String target) {
        List<Book> books;
        switch (filterType) {
            case "title":
                books = bookService.xPathFindBooksByTitle(target);
                break;
            case "language":
                books = bookService.xPathFindBooksByLanguage(target);
                break;
            case "publicationDateString":
                books = bookService.xPathFindBooksByPublicationDate(target);
                break;
            case "isbn":
                books = bookService.xPathFindBooksByIsbn(target);
                break;
            case "pages":
                books = bookService.xPathFindBooksByNumberOfPages(Long.valueOf(target));
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (books.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @RequestMapping
    public ResponseEntity<String> displayXmlFile() {
        try {
            // Read the contents of the XML file into a string
            String xmlContent = new String(Files.readAllBytes(Paths.get("output/obras.xml")));

            // Return the XML content as a response entity with the correct content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(xmlContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error reading XML file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/author/{authorId}/sync")
    public String syncAuthorBooks(@PathVariable Long authorId) {
        List<Book> books = bertrandJsonService.fetchBooksFromAuthor(authorId);
        bookService.saveAll(books);
        return "Synced " + books.size() + " books from author id: " + authorId;
    }
}
