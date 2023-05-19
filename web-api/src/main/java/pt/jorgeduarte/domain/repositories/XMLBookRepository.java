package pt.jorgeduarte.domain.repositories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Repository;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.wrappers.BookListWrapper;

@Repository
public class XMLBookRepository implements IXMLRepository<Book> {
    private static final String XML_FILE = "output/obras.xml";

    private List<Book> books;

    public XMLBookRepository() {
        loadBooks();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(generateId());
        }

        // verify if we already have the book or not
        // this way we will only save distinct books
        Optional<Book> alreadyExists = this.books.stream().filter( a -> a.getIsbn().equals(book.getIsbn())).findFirst();
        if(!alreadyExists.isPresent()){
            books.add(book);
            saveBooks();
        }
        return book;
    }

    public List<Book> getAuthorBooks(Long authorId) {
        List<Book> authorBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthorId().equals(authorId)) {
                authorBooks.add(book);
            }
        }
        return authorBooks;
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
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

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
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

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

    private List<Book> findBooksByXPathSaxon(String expression) {
        List<Book> books = new ArrayList<>();
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            Processor processor = new Processor(false);
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode document = builder.build(new File(XML_FILE));

            XPathCompiler compiler = processor.newXPathCompiler();
            XPathSelector selector = compiler.compile(expression).load();
            selector.setContextItem(document);

            // Iterate over all matches
            for (XdmItem item : selector) {
                if (item instanceof XdmNode) {
                    XdmNode node = (XdmNode) item;
                    Book book = new Book();
                    // Iterate over all child elements of the node
                    for (XdmItem child : node.children()) {
                        if (child instanceof XdmNode) {
                            XdmNode childNode = (XdmNode) child;
                            try{
                                String nodeName = childNode.getNodeName().getLocalName();
                                String nodeValue = childNode.getStringValue();
                                // Depending on the name of the node, set the appropriate field on the Book object
                                switch (nodeName) {
                                    case "id":
                                        book.setId(Long.parseLong(nodeValue));
                                        break;
                                    case "title":
                                        book.setTitle(nodeValue);
                                        break;
                                    case "authorId":
                                        book.setAuthorId((Long.parseLong(nodeValue)));
                                        break;
                                    case "isbn":
                                        book.setIsbn(nodeValue);
                                        break;
                                    case "publicationDateString":
                                        book.setPublicationDateString(nodeValue);
                                        break;
                                    case "publisher":
                                        book.setPublisher(nodeValue);
                                        break;
                                    case "language":
                                       book.setLanguage(nodeValue);
                                        break;
                                    case "description":
                                        book.setDescription(nodeValue);
                                        break;
                                    case "pages":
                                        book.setPages((Long.parseLong(nodeValue)));
                                        break;
                                    case "bertrandUrl":
                                        book.setBertrandUrl(nodeValue);
                                        break;
                                    case "coverImageUrl":
                                        book.setCoverImageUrl(nodeValue);
                                        break;
                                }
                            }catch(Exception ex){}
                        }
                    }
                    books.add(book);
                }
            }
        } catch (SaxonApiException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> xPathFindBooksByTitle(String title) {
        return findBooksByXPathSaxon("//book[title='"+ title +"']");
    }

    public List<Book> xPathFindBooksByLanguage(String language) {
        return findBooksByXPathSaxon("//book[language='"+ language +"']");
    }

    public List<Book> xPathFindBooksByNumberOfPages(Long pages) {
        return findBooksByXPathSaxon("//book[pages='"+ pages +"']");
    }

    public List<Book> xPathFindBooksByPublicationDate(String publicationDateString) {
        return findBooksByXPathSaxon("//book[publicationDateString='"+ publicationDateString +"']");
    }

    public List<Book> xPathFindBooksByIsbn(String isbn) {
        return findBooksByXPathSaxon("//book[isbn='"+ isbn +"']");
    }
}




