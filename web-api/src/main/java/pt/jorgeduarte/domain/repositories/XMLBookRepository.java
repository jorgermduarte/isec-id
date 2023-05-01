package pt.jorgeduarte.domain.repositories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.wrappers.BookListWrapper;

@Repository
public class XMLBookRepository implements IXMLRepository<Book> {
    private static final String XML_FILE = "data/obras.xml";

    private List<Book> books;

    public XMLBookRepository() {
        loadBooks();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(generateId());
        }
        books.add(book);
        saveBooks();
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst();
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    @Override
    public void deleteById(Long id) {
        books.removeIf(book -> book.getId().equals(id));
        saveBooks();
    }

    private void loadBooks() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BookListWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            FileInputStream fis = new FileInputStream(XML_FILE);
            BookListWrapper bookListWrapper = (BookListWrapper) unmarshaller.unmarshal(fis);
            books = bookListWrapper.getBooks();
            fis.close();
        } catch (JAXBException | IOException e) {
            books = new ArrayList<>();
        }
    }

    private void saveBooks() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BookListWrapper.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            BookListWrapper bookListWrapper = new BookListWrapper();
            bookListWrapper.setBooks(books);
            FileOutputStream fos = new FileOutputStream(XML_FILE);
            marshaller.marshal(bookListWrapper, fos);
            fos.close();
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    private Long generateId() {
        return books.stream().mapToLong(Book::getId).max().orElse(0) + 1;
    }

    public void validateXmlFile() {
        try {
            File xsdFile = new File("definitions/book.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(XML_FILE)));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}




