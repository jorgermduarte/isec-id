package pt.jorgeduarte.domain.wrappers;

import pt.jorgeduarte.domain.entities.AuthorBooks;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "authors")
public class AuthorBooksListWrapper {
    private List<AuthorBooks> authors;

    @XmlElement(name = "author")
    public List<AuthorBooks> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorBooks> authors) {
        this.authors = authors;
    }
}