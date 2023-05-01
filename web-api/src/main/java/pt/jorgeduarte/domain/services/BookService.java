package pt.jorgeduarte.domain.services;

import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.repositories.XMLBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final XMLBookRepository bookRepository;

    @Autowired
    public BookService(XMLBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public void transformBooksToHtml() {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            StreamSource xsltSource = new StreamSource(new File("xslt/books_to_html.xslt"));
            Transformer transformer = factory.newTransformer(xsltSource);

            StreamSource xmlSource = new StreamSource(new File("data/obras.xml"));
            FileOutputStream htmlOutput = new FileOutputStream("output/obras.html");
            StreamResult result = new StreamResult(htmlOutput);

            transformer.transform(xmlSource, result);
            htmlOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}