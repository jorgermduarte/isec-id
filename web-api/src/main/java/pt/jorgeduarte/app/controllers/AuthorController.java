package pt.jorgeduarte.app.controllers;

import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.AuthorBooks;
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.services.AuthorService;
import pt.jorgeduarte.domain.services.BookService;
import pt.jorgeduarte.domain.services.FileReaderTxtService;
import pt.jorgeduarte.domain.services.WikipediaRegexService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    WikipediaRegexService wikipediaRegexService;

    FileReaderTxtService fileReaderTxtService;

    AuthorService authorService;

    BookService bookService;

    public AuthorController(WikipediaRegexService wikipediaRegexService, AuthorService authorService, FileReaderTxtService fileReaderTxtService, BookService bookService) {
        this.wikipediaRegexService = wikipediaRegexService;
        this.authorService = authorService;
        this.fileReaderTxtService = fileReaderTxtService;
        this.bookService = bookService;
    }

    @RequestMapping
    public ResponseEntity<String> displayXmlFile() {
        try {
            // Read the contents of the XML file into a string
            String xmlContent = new String(Files.readAllBytes(Paths.get("output/escritores.xml")));

            // Return the XML content as a response entity with the correct content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            return new ResponseEntity<>(xmlContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error reading XML file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/filter/{filterType}")
    public ResponseEntity<List<?>> filterAuthors(@PathVariable String filterType, @RequestParam(required = false) String target) {
        List<?> authors;
        switch (filterType) {
            case "bornBefore":
                authors = authorService.xPathFindAuthorsBornBeforeDate(target);
                break;
            case "stillAlive":
                authors = authorService.xPathFindAuthorsStillAlive();
                break;
            case "passedAway":
                authors = authorService.xPathFindAuthorsPassedAway();
                break;
            case "bioContains":
                authors = authorService.xPathFindAuthorsByBiographyText(target);
                break;
            case "minimumBooks":
                authors = authorService.xQueryFindAuthorsWithMoreThanXBooks(Integer.parseInt(target));
                break;
            case "booksLanguage":
                authors = authorService.xQueryFindAuthorsWithBooksOfLanguage(target);
                break;
            case "nationality":
                authors = authorService.xQueryFindAuthorsWithNationality(target);
                break;
            case "occupation":
                authors = authorService.findAuthorsByOccupation(target);
                break;
            case "literaryGenre":
                authors = authorService.findAuthorsByLiteraryGenre(target);
                break;
            case "mostPrizes":
                authors = authorService.findTopXAuthorsWithMostPrizes(Integer.parseInt(target));
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @RequestMapping("/sync")
    public ResponseEntity<String> syncAllAuthorsInfo()  {
        // we will sync all authors based on the escritores.txt file
        var response = "";
        try{
            var lines = fileReaderTxtService.readFileByLines("escritores.txt");

            for (String line : lines) {
                Author author = wikipediaRegexService.fetchAuthorInfoWithRegex(line);
                if(author == null || author.getBirthDateString() == null){
                    response += "Author " + line + " not found! <br/>";
                }else{
                    //check if author exists
                    if(authorService.findAuthorByFullName(author.getFullName()).isPresent()){
                        response += "Author " + author.getFullName() + " already exists! <br/>";
                    }else{
                        authorService.saveAuthor(author);
                        response += "Author " + author.getFullName() + " synced with success! <br/>";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Finished syncing authors! <br/><br/>" + response + "<br/>");

    }

    @RequestMapping("{name}/sync")
    public ResponseEntity<String> syncAuthorInfo(@PathVariable String name){
        Author author = wikipediaRegexService.fetchAuthorInfoWithRegex(name);

        if(author == null || author.getBirthDateString() == null ){
            return ResponseEntity.ok("Author " + name + " not found!");
        }

        //check if author exists
        if(authorService.xPathFindAuthorByFullName(name).isPresent()){
            return ResponseEntity.ok("Author " + author.getFullName() + " already exists!");
        }else{
            authorService.saveAuthor(author);
            return ResponseEntity.ok("Author " + author.getFullName() + " synced with success!");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author author){
        Optional<Author> current = this.authorService.findAuthorById(id);

        if(current.isPresent()){
            this.authorService.updateAuthorById(id,author);
            return new ResponseEntity<>(this.authorService.findAuthorById(id).get(),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthorById(@PathVariable Long id){
        Optional<Author> current = this.authorService.findAuthorById(id);

        if(current.isPresent()){
            this.authorService.deleteAuthorById(id);
            return new ResponseEntity<>("Author deleted successfully", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("The author that you're trying to delete does not exist",HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("/{id}")
    public Author getAuthorInfo(@PathVariable Long id){
      return this.authorService.findAuthorById(id).orElseThrow(() -> new RuntimeException("Author not found!"));
    }
}
