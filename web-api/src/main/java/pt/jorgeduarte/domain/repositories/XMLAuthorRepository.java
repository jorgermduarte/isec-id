package pt.jorgeduarte.domain.repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.wrappers.AuthorListWrapper;

@Repository
public class XMLAuthorRepository implements IXMLRepository<Author> {
    private static final String XML_FILE = "output/escritores.xml";

    private List<Author> authors;

    public XMLAuthorRepository() {
        loadAuthors();
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null) {
            author.setId(generateId());
        }
        authors.add(author);
        saveAuthors();
        return author;
    }

    @Override
    public Optional<Author> findById(Long id) {
        return authors.stream().filter(author -> author.getId().equals(id)).findFirst();
    }

    @Override
    public List<Author> findAll() {
        return new ArrayList<>(authors);
    }

    @Override
    public void deleteById(Long id) {
        authors.removeIf(author -> author.getId().equals(id));
        saveAuthors();
    }

    private void loadAuthors() {
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthorListWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            FileInputStream fis = new FileInputStream(XML_FILE);
            AuthorListWrapper authorListWrapper = (AuthorListWrapper) unmarshaller.unmarshal(fis);
            authors = authorListWrapper.getAuthors();
            fis.close();
        } catch (JAXBException | IOException e) {
            authors = new ArrayList<>();
        }
    }
    public Optional<Author> findAuthorByFullName(String name){
        return authors.stream().filter(author -> author.getFullName().equals(name)).findFirst();
    }

    private void saveAuthors() {
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthorListWrapper.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            AuthorListWrapper authorListWrapper = new AuthorListWrapper();
            authorListWrapper.setAuthors(authors);

            FileOutputStream fos = new FileOutputStream(XML_FILE);
            marshaller.marshal(authorListWrapper, fos);
            fos.close();
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    private Long generateId() {
        return authors.stream().mapToLong(Author::getId).max().orElse(0) + 1;
    }

    public void validateXmlFile() {
        try {
            File xsdFile = new File("definitions/author.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(XML_FILE)));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
