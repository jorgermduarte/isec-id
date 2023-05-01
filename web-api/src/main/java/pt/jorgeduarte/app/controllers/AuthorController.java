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
import pt.jorgeduarte.domain.services.FileReaderTxtService;
import pt.jorgeduarte.domain.services.WikipediaRegexService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    WikipediaRegexService wikipediaRegexService;

    FileReaderTxtService fileReaderTxtService;

    AuthorService authorService;

    public AuthorController(WikipediaRegexService wikipediaRegexService, AuthorService authorService, FileReaderTxtService fileReaderTxtService) {
        this.wikipediaRegexService = wikipediaRegexService;
        this.authorService = authorService;
        this.fileReaderTxtService = fileReaderTxtService;
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

    @RequestMapping("/{id}")
    public Author getAuthorInfo(@PathVariable Long id){
        return authorService.findAuthorById(id).orElseThrow(() -> new RuntimeException("Author not found!"));
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
                    response += "Author " + author.getFullName() + " not found! <br/>";
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
            return ResponseEntity.ok("Author " + author.getFullName() + " not found!");
        }

        //check if author exists
        if(authorService.findAuthorByFullName(name).isPresent()){
            return ResponseEntity.ok("Author " + author.getFullName() + " already exists!");
        }else{
            authorService.saveAuthor(author);
            return ResponseEntity.ok("Author " + author.getFullName() + " synced with success!");
        }
    }
}
