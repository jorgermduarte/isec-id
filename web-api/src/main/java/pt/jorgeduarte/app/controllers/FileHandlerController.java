package pt.jorgeduarte.app.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.AuthorBooks;
import pt.jorgeduarte.domain.services.AuthorService;
import pt.jorgeduarte.domain.services.BookService;
import pt.jorgeduarte.domain.services.FileReaderTxtService;
import pt.jorgeduarte.domain.wrappers.AuthorBooksListWrapper;
import pt.jorgeduarte.domain.wrappers.AuthorListWrapper;
import pt.jorgeduarte.domain.wrappers.BookListWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/output")
public class FileHandlerController {
    FileReaderTxtService fileReaderService;

    AuthorService authorService;

    BookService bookService;


    public FileHandlerController(FileReaderTxtService fileReaderService, AuthorService authorService, BookService bookService) {
        this.fileReaderService = fileReaderService;
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @RequestMapping("/txt/{name}")
    public ResponseEntity<String> readFileData(@PathVariable String name) throws FileNotFoundException {
        return ResponseEntity.ok().body(fileReaderService.read(name));
    }
    @RequestMapping("/html/authors")
    public ResponseEntity<String> readAuthorsFileDataHtml(){
        try {
            // Load authors as XML
            String authorsXml = getAuthorsAsXmlString();

            // Load XSLT
            ClassPathResource xsltResource = new ClassPathResource("xslt/authors_to_html.xslt");
            InputStream xsltInputStream = xsltResource.getInputStream();
            StreamSource xsltSource = new StreamSource(xsltInputStream);

            // Transform XML with XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsltSource);
            StringWriter htmlOutput = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(authorsXml)), new StreamResult(htmlOutput));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(htmlOutput.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/html/books")
    public ResponseEntity<String> readBooksFileDataHtml(){
        try {
            // Load authors as XML
            String booksXml = getBooksAsXmlString();

            // Load XSLT
            ClassPathResource xsltResource = new ClassPathResource("xslt/books_to_html.xslt");
            InputStream xsltInputStream = xsltResource.getInputStream();
            StreamSource xsltSource = new StreamSource(xsltInputStream);

            // Transform XML with XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsltSource);
            StringWriter htmlOutput = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(booksXml)), new StreamResult(htmlOutput));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(htmlOutput.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/html/author/{authorName}")
    public ResponseEntity<String> readAuthorInfoDataHtml(@PathVariable String authorName){
        try {
            // Load authors as XML
            String authorBooksXml = getAuthorBooksAsXmlString(authorName);

            // Load XSLT
            ClassPathResource xsltResource = new ClassPathResource("xslt/author_books_to_html.xslt");
            InputStream xsltInputStream = xsltResource.getInputStream();
            StreamSource xsltSource = new StreamSource(xsltInputStream);

            // Transform XML with XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsltSource);
            StringWriter htmlOutput = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(authorBooksXml)), new StreamResult(htmlOutput));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<>(htmlOutput.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping("/txt/authors")
    public ResponseEntity<String> getAllAuthorsOnTxt(@RequestParam(required = false) boolean saveFile){
        final String[] result = {""};

        List<Author> authors = authorService.findAllAuthors();

        if(authors != null){
            authors.forEach( a -> {
                result[0] = result[0] + a.getFullName() + "<br/>";
            });
        }

        if(saveFile){
            String outputFile = result[0].replaceAll("<br/>","\n");
            fileReaderService.saveTextFile("current_authors.txt",outputFile);
        }

        return new ResponseEntity<>(result[0],HttpStatus.OK);
    }

    private String getAuthorsAsXmlString() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AuthorListWrapper.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        AuthorListWrapper authorListWrapper = new AuthorListWrapper();
        authorListWrapper.setAuthors(authorService.findAllAuthors());

        StringWriter xmlOutput = new StringWriter();
        marshaller.marshal(authorListWrapper, xmlOutput);
        return xmlOutput.toString();
    }

    private String getBooksAsXmlString() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(BookListWrapper.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        BookListWrapper bookListWrapper= new BookListWrapper();
        bookListWrapper.setBooks(bookService.findAllBooks());

        StringWriter xmlOutput = new StringWriter();
        marshaller.marshal(bookListWrapper, xmlOutput);
        return xmlOutput.toString();
    }

    private String getAuthorBooksAsXmlString(String name) throws  JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(AuthorBooksListWrapper.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        AuthorBooksListWrapper bookListWrapper= new AuthorBooksListWrapper();


        List<AuthorBooks> authorsDetected = authorService.findAuthorWithBooksByFullName(name);

        if(!authorsDetected.isEmpty()){
            bookListWrapper.setAuthors(authorsDetected);
        }

        StringWriter xmlOutput = new StringWriter();
        marshaller.marshal(bookListWrapper, xmlOutput);
        return xmlOutput.toString();
    }
}
