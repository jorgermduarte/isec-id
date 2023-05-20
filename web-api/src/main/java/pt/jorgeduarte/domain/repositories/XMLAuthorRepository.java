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

    public boolean updateAuthorById(Long authorId, Author updatedAuthor){
        for (Author author : this.authors) {
            if(author.getId().equals(authorId)){
                author.setFullName(updatedAuthor.getFullName());
                author.setBirthDateString(updatedAuthor.getBirthDateString());
                author.setDeathDateString(updatedAuthor.getDeathDateString());
                author.setNationality(updatedAuthor.getNationality());
                author.setWikipediaUrl(updatedAuthor.getWikipediaUrl());
                author.setBiography(updatedAuthor.getBiography());
                author.setCoverImageUrl(updatedAuthor.getCoverImageUrl());
                author.setOccupations(updatedAuthor.getOccupations());
                author.setLiteraryGenre(updatedAuthor.getLiteraryGenre());
                author.setPrizes(updatedAuthor.getPrizes());
                saveAuthors();
                return true;
            }
        }
        return false;  // return false if no author was found with the given ID
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
                                case "literaryGenre":
                                    author.setLiteraryGenre(nodeValue);
                                    break;
                                case "prizes":
                                    List<String> prizes = new ArrayList<>();
                                    for (XdmItem prizeItem : childNode.children()) {
                                        if (prizeItem instanceof XdmNode) {
                                            XdmNode prizeNode = (XdmNode) prizeItem;
                                            String prize = prizeNode.getStringValue();
                                            if(!prize.startsWith("\n")) {
                                                prizes.add(prize);
                                            }
                                        }
                                    }
                                    author.setPrizes(prizes);
                                    break;
                                case "occupations":
                                    List<String> occupations = new ArrayList<>();
                                    for (XdmItem occupationItem : childNode.children()) {
                                        if (occupationItem instanceof XdmNode) {
                                            XdmNode occupationNode = (XdmNode) occupationItem;
                                            String occupation = occupationNode.getStringValue();
                                            if(!occupation.startsWith("\n")){
                                                occupations.add(occupation);
                                            }
                                        }
                                    }
                                    author.setOccupations(occupations);
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

}
