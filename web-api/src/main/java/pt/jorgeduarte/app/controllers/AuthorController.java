package pt.jorgeduarte.app.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.services.AuthorService;
import pt.jorgeduarte.domain.services.WikipediaRegexService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    WikipediaRegexService wikipediaRegexService;

    AuthorService authorService;

    public AuthorController(WikipediaRegexService wikipediaRegexService, AuthorService authorService) {
        this.wikipediaRegexService = wikipediaRegexService;
        this.authorService = authorService;
    }

    @RequestMapping("/{id}")
    public Author getAuthorInfo(@PathVariable Long id){
        return authorService.findAuthorById(id).orElseThrow(() -> new RuntimeException("Author not found!"));
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

    @RequestMapping("{name}/sync")
    public ResponseEntity<String> syncAuthorInfo(@PathVariable String name){
        Author author = wikipediaRegexService.fetchAuthorInfoWithRegex(name);

        //check if author exists
        if(authorService.findAuthorByFullName(name).isPresent()){
            return ResponseEntity.ok("Author " + author.getFullName() + " already exists!");
        }else{
            authorService.saveAuthor(author);
            return ResponseEntity.ok("Author " + author.getFullName() + " synced with success!");
        }
    }
}
