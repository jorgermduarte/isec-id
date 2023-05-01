package pt.jorgeduarte.app.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.jorgeduarte.domain.services.AuthorService;
import pt.jorgeduarte.domain.services.FileReaderTxtService;
import pt.jorgeduarte.domain.wrappers.AuthorListWrapper;

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

@RestController
@RequestMapping("/output")
public class FileHandlerController {
    FileReaderTxtService fileReaderService;

    AuthorService authorService;

    public FileHandlerController(FileReaderTxtService fileReaderService, AuthorService authorService) {
        this.fileReaderService = fileReaderService;
        this.authorService = authorService;
    }

    @RequestMapping("/txt/{name}")
    public ResponseEntity<String> readFileData(@PathVariable String name) throws FileNotFoundException {
        return ResponseEntity.ok().body(fileReaderService.read(name));
    }
    @RequestMapping("/html/authors")
    public ResponseEntity<String> readFileDataHtml(){
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
}
