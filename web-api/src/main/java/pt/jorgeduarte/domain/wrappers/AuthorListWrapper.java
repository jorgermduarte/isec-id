package pt.jorgeduarte.domain.wrappers;

import pt.jorgeduarte.domain.entities.Author;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlRootElement(name = "authors")
public class AuthorListWrapper {

    private List<Author> authors;

    @XmlElement(name = "author")
    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}