package pt.jorgeduarte.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class AuthorBooks {

    private Long id;

    private String fullName;

    private String birthDateString;

    private String deathDateString;

    private String nationality;

    private String wikipediaUrl;

    private String biography;

    private String coverImageUrl;

    @XmlElementWrapper(name = "books")
    @XmlElement(name = "book")
    private List<Book> books;
}

