package pt.jorgeduarte.domain.entities;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Book {
    private Long id;

    private String title;

    private Long authorId;

    private String isbn;

    private String publicationDateString;

    private String publisher;

    private String language;

    private String description;

    private Double price;

    private Long pages;

    private String bertrandUrl;

    private String coverImageUrl;
}