package pt.jorgeduarte.domain.entities;

import javax.xml.bind.annotation.*;

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

    @XmlElementWrapper(name = "occupations")
    @XmlElement(name = "occupation")
    private List<String> occupations;

    @XmlElementWrapper(name = "prizes")
    @XmlElement(name = "prize")
    private List<String> prizes;

    private String literaryGenre;

    private String biography;

    private String coverImageUrl;
}
