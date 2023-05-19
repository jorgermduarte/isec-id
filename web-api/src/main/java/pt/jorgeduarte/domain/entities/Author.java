package pt.jorgeduarte.domain.entities;

import javax.xml.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;
import pt.jorgeduarte.domain.wrappers.BookListWrapper;

import java.util.List;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Author {

     private Long id;

    private String fullName;

    private String birthDateString;

    private String deathDateString;

    private String nationality;

    private String wikipediaUrl;

    private String biography;

    @XmlElementWrapper(name = "books")
    @XmlElement(name = "book")
    private List<Book> books;

    private String coverImageUrl;
}
