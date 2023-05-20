package pt.jorgeduarte.domain.repositories;


import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Repository;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.AuthorBooks;
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.libs.XQueryVariable;
import pt.jorgeduarte.domain.services.XQueryFileReaderService;
import pt.jorgeduarte.domain.wrappers.AuthorBooksListWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class XMLAggregationRepository implements IXMLRepository<AuthorBooks> {
    private static final String XML_FILE = "output/aggregation.xml";

    private List<AuthorBooks> authorsBooks;

    public XMLAggregationRepository() {
        loadAuthorsBooks();
    }

    @Override
    public AuthorBooks save(AuthorBooks author) {
        authorsBooks.add(author);
        saveAuthorsBooks();
        return author;
    }

    public boolean updateAuthorById(long authorId, Author updated){
        AuthorBooks updatedAuthor = this.mapAuthorToAuthorBooks(updated);

        for (AuthorBooks author : this.authorsBooks) {
            if(author.getId().equals(authorId)){
                author.setFullName(updatedAuthor.getFullName());
                author.setBirthDateString(updatedAuthor.getBirthDateString());
                author.setDeathDateString(updatedAuthor.getDeathDateString());
                author.setNationality(updatedAuthor.getNationality());
                author.setWikipediaUrl(updatedAuthor.getWikipediaUrl());
                author.setBiography(updatedAuthor.getBiography());
                author.setCoverImageUrl(updatedAuthor.getCoverImageUrl());
                saveAuthorsBooks();
                return true;
            }
        }
        return false;  // return false if no author was found with the given ID
    }

    public List<AuthorBooks> findAuthorsWithBooksByFullName(String name){
        return this.authorsBooks.stream().filter( a -> a.getFullName().toLowerCase().contains(name.toLowerCase())).toList();
    }

    public AuthorBooks mapAuthorToAuthorBooks(Author author){
       AuthorBooks result = new AuthorBooks();
       result.setId(author.getId());
       result.setBiography(author.getBiography());
       result.setNationality(author.getNationality());
       result.setCoverImageUrl(author.getCoverImageUrl());
       result.setFullName(author.getFullName());
       result.setBirthDateString(author.getBirthDateString());
       result.setWikipediaUrl(author.getWikipediaUrl());
       result.setDeathDateString(author.getDeathDateString());
       return result;
    }

    @Override
    public Optional<AuthorBooks> findById(Long id) {
        return authorsBooks.stream().filter(author -> author.getId().equals(id)).findFirst();
    }

    @Override
    public List<AuthorBooks> findAll() {
        return new ArrayList<>(authorsBooks);
    }

    @Override
    public void deleteById(Long id) {
        authorsBooks.removeIf(author -> author.getId().equals(id));
        saveAuthorsBooks();
    }

    private void loadAuthorsBooks() {
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthorBooksListWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            FileInputStream fis = new FileInputStream(XML_FILE);
            AuthorBooksListWrapper authorListWrapper = (AuthorBooksListWrapper) unmarshaller.unmarshal(fis);
            authorsBooks = authorListWrapper.getAuthors();
            fis.close();
        } catch (JAXBException | IOException e) {
            authorsBooks = new ArrayList<>();
        }
    }

    private void saveAuthorsBooks() {
        try {
            // Create the output directory if it does not exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(AuthorBooksListWrapper.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            AuthorBooksListWrapper authorListWrapper = new AuthorBooksListWrapper();
            authorListWrapper.setAuthors(authorsBooks);

            FileOutputStream fos = new FileOutputStream(XML_FILE);
            marshaller.marshal(authorListWrapper, fos);
            fos.close();
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }
    private List<AuthorBooks> findAuthorsByXQuerySaxon(String xQueryString, List<XQueryVariable> variables ) {
        List<AuthorBooks> authors = new ArrayList<>();
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
                    AuthorBooks author = new AuthorBooks();
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
                                                                case "price":
                                                                    book.setPrice(Double.parseDouble(nodeValue));
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

    public Optional<AuthorBooks> addBooksToAuthor(Author author, List<Book> books){
        this.authorsBooks.forEach( a -> {
            if(a.getId().equals(author.getId())){

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
        saveAuthorsBooks();
        return this.authorsBooks.stream().filter( a -> a.getId().equals(author.getId())).findFirst();
    }

    public List<AuthorBooks> xQueryFindAuthorsWithMoreThanXBooks(int minimumBooks){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsWithMinBooks.xq");

        XQueryVariable minBooks = new XQueryVariable();
        minBooks.setKey("minBooks");
        minBooks.setIntValue(minimumBooks);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(minBooks);

        return  findAuthorsByXQuerySaxon(xQuery, variables);
    }

    public List<AuthorBooks> xQueryFindAuthorsWithNationality(String nationality){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsByNationality.xq");

        XQueryVariable variable = new XQueryVariable();
        variable.setKey("nationality");
        variable.setStringValue(nationality);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(variable);

        return findAuthorsByXQuerySaxon(xQuery, variables);
    }

    public List<AuthorBooks> xQueryFindAuthorsWithBooksOfLanguage(String language){
        String xQuery = XQueryFileReaderService.getXQueryFromFile("/xqueries/authorsWithBooksOfOneLanguage.xq");

        XQueryVariable variable = new XQueryVariable();
        variable.setKey("language");
        variable.setStringValue(language);

        ArrayList<XQueryVariable> variables = new ArrayList<>();
        variables.add(variable);

        return findAuthorsByXQuerySaxon(xQuery, variables);
    }


}
