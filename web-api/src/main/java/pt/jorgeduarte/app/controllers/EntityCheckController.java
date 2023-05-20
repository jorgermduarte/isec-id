package pt.jorgeduarte.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.services.AuthorService;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.net.URL;

@RestController
@RequestMapping("/entity/verify")
public class EntityCheckController {

    private final String xsdAuthorResourcePath = "/schemas/author_books.xsd";
    private final String xmlAuthorOutputPath = "output/escritores.xml";

    private final String xsdBookResourcePath = "/schemas/book.xsd";
    private final String xmlBookOutputPath = "output/obras.xml";


    private final String xsdAuthorBooksResourcePath = "/schemas/author_books.xsd";
    private final String xmlAuthorBooksOutputPath = "output/aggregation.xml";



    AuthorService authorService;

    public EntityCheckController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @RequestMapping("/author")
    public ResponseEntity<String> verifyAuthorEntity() {
        try {
            URL xsdResourceUrl = getClass().getResource(xsdAuthorResourcePath);
            if (xsdResourceUrl == null) {
                System.out.println("Não foi possível encontrar o arquivo XSD.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível encontrar o arquivo XSD.");
            }
            File xsdFile = new File(xsdResourceUrl.toURI());
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlAuthorOutputPath)));
            return ResponseEntity.ok("O arquivo XML é válido de acordo com o schema.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O arquivo XML é inválido de acordo com o schema.");
        }
    }

    @RequestMapping("/book")
    public ResponseEntity<String> verifyBookEntity() {
        try {
            URL xsdResourceUrl = getClass().getResource(xsdBookResourcePath);
            if (xsdResourceUrl == null) {
                System.out.println("Não foi possível encontrar o arquivo XSD.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível encontrar o arquivo XSD.");
            }
            File xsdFile = new File(xsdResourceUrl.toURI());
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlBookOutputPath)));
            return ResponseEntity.ok("O arquivo XML é válido de acordo com o schema.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O arquivo XML é inválido de acordo com o schema.");
        }
    }

    @RequestMapping("/aggregation")
    public ResponseEntity<String> verifyAggregationEntity() {
        try {
            URL xsdResourceUrl = getClass().getResource(xsdAuthorBooksResourcePath);
            if (xsdResourceUrl == null) {
                System.out.println("Não foi possível encontrar o arquivo XSD.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível encontrar o arquivo XSD.");
            }
            File xsdFile = new File(xsdResourceUrl.toURI());
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlAuthorBooksOutputPath)));
            return ResponseEntity.ok("O arquivo XML é válido de acordo com o schema.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O arquivo XML é inválido de acordo com o schema.");
        }
    }
}
