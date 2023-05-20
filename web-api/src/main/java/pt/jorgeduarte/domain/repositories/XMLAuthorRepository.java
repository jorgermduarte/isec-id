package pt.jorgeduarte.domain.repositories;

import java.io.*;
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
import pt.jorgeduarte.domain.libs.XQueryVariable;
import pt.jorgeduarte.domain.wrappers.AuthorListWrapper;
import pt.jorgeduarte.domain.services.XQueryFileReaderService;

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

    private List<Author> findAuthorsByXPathSaxon(String expression) {
        List<Author> authors = new ArrayList<>();
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
                    Author author = new Author();
                    // Iterate over all child elements of the node
                    for (XdmItem child : node.children()) {
                        if (child instanceof XdmNode) {
                            XdmNode childNode = (XdmNode) child;
                            try{
                                String nodeName = childNode.getNodeName().getLocalName();
                                String nodeValue = childNode.getStringValue();
                            // Depending on the name of the node, set the appropriate field on the Author object
                            switch (nodeName) {
                                case "id":
                                    author.setId(Long.parseLong(nodeValue));
                                    break;
                                case "fullName":
                                    author.setFullName(nodeValue);
                                    break;
                                case "birthDateString":
                                    author.setBirthDateString(nodeValue);
                                    break;
                                case "deathDateString":
                                    author.setDeathDateString(nodeValue);
                                    break;
                                case "nationality":
                                    author.setNationality(nodeValue);
                                    break;
                                case "wikipediaUrl":
                                    author.setWikipediaUrl(nodeValue);
                                    break;
                                case "biography":
                                    author.setBiography(nodeValue);
                                    break;
                                case "books":
                                    List<Book> authorBooks = new ArrayList<>();
                                    for (XdmItem bookItem : childNode.children()) {
                                        if (bookItem instanceof XdmNode) {
                                            XdmNode bookNode = (XdmNode) bookItem;
                                            Book book = new Book();
                                            for (XdmItem bookChild : bookNode.children()) {
                                                if (bookChild instanceof XdmNode) {
                                                    XdmNode bookChildNode = (XdmNode) bookChild;
                                                    try{
                                                        String bookNodeName = bookChildNode.getNodeName().getLocalName();
                                                        String bookNodeValue = bookChildNode.getStringValue();
                                                        // Depending on the name of the node, set the appropriate field on the Book object
                                                        switch (bookNodeName) {
                                                            case "id":
                                                                book.setId(Long.parseLong(bookNodeValue));
                                                                break;
                                                            case "title":
                                                                book.setTitle(bookNodeValue);
                                                                break;
                                                            case "authorId":
                                                                book.setAuthorId((Long.parseLong(bookNodeValue)));
                                                                break;
                                                            case "isbn":
                                                                book.setIsbn(bookNodeValue);
                                                                break;
                                                            case "publicationDateString":
                                                                book.setPublicationDateString(bookNodeValue);
                                                                break;
                                                            case "publisher":
                                                                book.setPublisher(bookNodeValue);
                                                                break;
                                                            case "language":
                                                                book.setLanguage(bookNodeValue);
                                                                break;
                                                            case "description":
                                                                book.setDescription(bookNodeValue);
                                                                break;
                                                            case "pages":
                                                                book.setPages((Long.parseLong(bookNodeValue)));
                                                                break;
                                                            case "bertrandUrl":
                                                                book.setBertrandUrl(bookNodeValue);
                                                                break;
                                                            case "coverImageUrl":
                                                                book.setCoverImageUrl(bookNodeValue);
                                                                break;
                                                        }
                                                    }catch(Exception ex){}
                                                }
                                            }
                                            if(book.getAuthorId() != null){
                                                authorBooks.add(book);
                                            }
                                        }
                                    }
                                    author.setBooks(authorBooks);
                                    break;
                            }
                            }catch(Exception ex){}
                        }
                    }
                    authors.add(author);
                }
            }
        } catch (SaxonApiException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private List<Author> findAuthorsByXQuerySaxon(String xQueryString, List<XQueryVariable> variables ) {
        List<Author> authors = new ArrayList<>();
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            Processor processor = new Processor(false);
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode document = builder.build(new File(XML_FILE));

            XQueryCompiler compiler = processor.newXQueryCompiler();
            XQueryExecutable executable = compiler.compile(xQueryString);


            XQueryEvaluator evaluator = executable.load();

            // Set the external variables values
            variables.forEach( v -> {
                evaluator.setExternalVariable(new QName(v.getKey()), v.getValue());
            });

            evaluator.setContextItem(document);

            // Iterate over all matches
            for (XdmItem item : evaluator) {
                if (item instanceof XdmNode) {
                    XdmNode node = (XdmNode) item;
                    Author author = new Author();
                    // Iterate over all child elements of the node
                    for (XdmItem child : node.children()) {
                        if (child instanceof XdmNode) {
                            XdmNode childNode = (XdmNode) child;
                            try{
                                String nodeName = childNode.getNodeName().getLocalName();
                                String nodeValue = childNode.getStringValue();
                                // Depending on the name of the node, set the appropriate field on the Author object
                                switch (nodeName) {
                                    case "id":
                                        author.setId(Long.parseLong(nodeValue));
                                        break;
                                    case "fullName":
                                        author.setFullName(nodeValue);
                                        break;
                                    case "birthDateString":
                                        author.setBirthDateString(nodeValue);
                                        break;
                                    case "deathDateString":
                                        author.setDeathDateString(nodeValue);
                                        break;
                                    case "nationality":
                                        author.setNationality(nodeValue);
                                        break;
                                    case "wikipediaUrl":
                                        author.setWikipediaUrl(nodeValue);
                                        break;
                                    case "biography":
                                        author.setBiography(nodeValue);
                                        break;
                                    case "books":
                                        List<Book> authorBooks = new ArrayList<>();
                                        for (XdmItem bookItem : childNode.children()) {
                                            if (bookItem instanceof XdmNode) {
                                                XdmNode bookNode = (XdmNode) bookItem;
                                                Book book = new Book();
                                                for (XdmItem bookChild : bookNode.children()) {
                                                    if (bookChild instanceof XdmNode) {
                                                        XdmNode bookChildNode = (XdmNode) bookChild;
                                                        try{

                                                            String bookNodeName = bookChildNode.getNodeName().getLocalName();
                                                            String bookNodeValue = bookChildNode.getStringValue();
                                                            // Depending on the name of the node, set the appropriate field on the Book object
                                                            switch (bookNodeName) {
                                                                case "id":
                                                                    book.setId(Long.parseLong(bookNodeValue));
                                                                    break;
                                                                case "title":
                                                                    book.setTitle(bookNodeValue);
                                                                    break;
                                                                case "authorId":
                                                                    book.setAuthorId((Long.parseLong(bookNodeValue)));
                                                                    break;
                                                                case "isbn":
                                                                    book.setIsbn(bookNodeValue);
                                                                    break;
                                                                case "publicationDateString":
                                                                    book.setPublicationDateString(bookNodeValue);
                                                                    break;
                                                                case "publisher":
                                                                    book.setPublisher(bookNodeValue);
                                                                    break;
                                                                case "language":
                                                                    book.setLanguage(bookNodeValue);
                                                                    break;
                                                                case "description":
                                                                    book.setDescription(bookNodeValue);
                                                                    break;
                                                                case "pages":
                                                                    book.setPages((Long.parseLong(bookNodeValue)));
                                                                    break;
                                                                case "bertrandUrl":
                                                                    book.setBertrandUrl(bookNodeValue);
                                                                    break;
                                                                case "coverImageUrl":
                                                                    book.setCoverImageUrl(bookNodeValue);
                                                                    break;
                                                            }
                                                        }catch(Exception ex){}
                                                    }
                                                }
                                                if(book.getAuthorId() != null){
                                                    authorBooks.add(book);
                                                }
                                            }
                                        }
                                        author.setBooks(authorBooks);
                                        break;
                                }
                            }catch(Exception ex){}
                        }
                    }
                    authors.add(author);
                }
            }
        } catch (SaxonApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return authors;
    }

    public Optional<Author> xPathFindAuthorByFullName(String name){
        return findAuthorsByXPathSaxon("//author[fullName='"+ name +"']").stream().findFirst();
    }

    public List<Author> xPathFindAuthorsBornBeforeDate(String date){
        return findAuthorsByXPathSaxon("//author[translate(birthDateString, '-', '') < translate('" + date + "', '-', '')]");
    }

    public List<Author> xPathFindAuthorsPassedAway(){
        return findAuthorsByXPathSaxon("//author[deathDateString!='']");
    }

    public List<Author> xPathFindAuthorsByBiographyText(String text){
        return findAuthorsByXPathSaxon("//author[contains(biography, '" + text + "')]");
    }

    public List<Author> xPathFindAuthorsStillAlive(){
        return findAuthorsByXPathSaxon("//author[not(exists(deathDateString))]");
    }

    public Optional<Author> addBooksToAuthor(Long authorId, List<Book> books){
        this.authors.forEach( a -> {
            if(a.getId().equals(authorId)){

                //let's verify if the author does not have the book in the book list
                List<Book> currentAuthorBooks = a.getBooks();

                if(currentAuthorBooks == null)
                    currentAuthorBooks = new ArrayList<>();

                // for each new book, lets see if the author does not contain it already
                for (int i = 0; i < books.size(); i++) {
                    int finalI = i;
                    Optional<Book> bookExists = currentAuthorBooks.stream().filter(c -> c.getIsbn().equals(books.get(finalI).getIsbn())).findFirst();
                    if(bookExists.isEmpty()){
                        // add the book to the author
                        currentAuthorBooks.add(books.get(finalI));
                    }
                }

                a.setBooks(currentAuthorBooks);
            }
        });
        saveAuthors();
        return this.authors.stream().filter( a -> a.getId().equals(authorId)).findFirst();
    }

    public List<Author> xQueryFindAuthorsWithMoreThanXBooks(int minimumBooks){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsWithMinBooks.xq");

        XQueryVariable minBooks = new XQueryVariable();
        minBooks.setKey("minBooks");
        minBooks.setIntValue(minimumBooks);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(minBooks);

        return  findAuthorsByXQuerySaxon(xQuery, variables);
    }

    public List<Author> xQueryFindAuthorsWithNationality(String nationality){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsByNationality.xq");

        XQueryVariable variable = new XQueryVariable();
        variable.setKey("nationality");
        variable.setStringValue(nationality);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(variable);

        return findAuthorsByXQuerySaxon(xQuery, variables);
    }

    public List<Author> xQueryFindAuthorsWithBooksOfLanguage(String language){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsWithBooksOfOneLanguage.xq");

        XQueryVariable variable = new XQueryVariable();
        variable.setKey("language");
        variable.setStringValue(language);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(variable);

        return findAuthorsByXQuerySaxon(xQuery, variables);
    }


}
