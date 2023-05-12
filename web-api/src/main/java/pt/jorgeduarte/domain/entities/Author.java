package pt.jorgeduarte.domain.entities;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import lombok.Getter;
import lombok.Setter;

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

    private List<Book> books;
}
