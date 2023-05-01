package pt.jorgeduarte.domain.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private String birthDateString;

    @Column(name = "death_date")
    @Nullable
    private String deathDateString;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "wikipedia_url")
    @Nullable
    private String wikipediaUrl;

    @Column(name = "biography", columnDefinition = "CLOB")
    @Lob
    @Nullable
    private String biography;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Book> books;

}
