package pt.jorgeduarte.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.jorgeduarte.domain.entities.Author;
import pt.jorgeduarte.domain.entities.AuthorBooks;
import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.repositories.XMLAggregationRepository;
import pt.jorgeduarte.domain.repositories.XMLAuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final XMLAuthorRepository xmlAuthorRepository;

    private final XMLAggregationRepository xmlAggregationRepository;

    @Autowired
    public AuthorService(XMLAuthorRepository xmlAuthorRepository, XMLAggregationRepository xmlAggregationRepository) {
        this.xmlAuthorRepository = xmlAuthorRepository;
        this.xmlAggregationRepository = xmlAggregationRepository;
    }

    public Author saveAuthor(Author author) {
        return xmlAuthorRepository.save(author);
    }

    public Optional<Author> findAuthorById(Long id) {
        return xmlAuthorRepository.findById(id);
    }

    public Optional<Author> findAuthorByFullName(String name) {
        return xmlAuthorRepository.findAuthorByFullName(name);
    }

    public List<Author> findAllAuthors() {
        return xmlAuthorRepository.findAll();
    }

    public void deleteAuthorById(Long id) {
        xmlAuthorRepository.deleteById(id);
    }

    public Optional<Author> xPathFindAuthorByFullName(String name){
        return xmlAuthorRepository.xPathFindAuthorByFullName(name);
    }

    public List<Author> xPathFindAuthorsBornBeforeDate(String date){
        return xmlAuthorRepository.xPathFindAuthorsBornBeforeDate(date);
    }

    public List<Author> xPathFindAuthorsPassedAway(){
        return xmlAuthorRepository.xPathFindAuthorsPassedAway();
    }

    public List<Author> xPathFindAuthorsByBiographyText(String text){
        return xmlAuthorRepository.xPathFindAuthorsByBiographyText(text);
    }

    public List<Author> xPathFindAuthorsStillAlive(){
        return xmlAuthorRepository.xPathFindAuthorsStillAlive();
    }

    public Optional<AuthorBooks> addBooksToAuthor(Author author, List<Book> books){
        return xmlAggregationRepository.addBooksToAuthor(author,books);
    }

    public List<AuthorBooks> xQueryFindAuthorsWithMoreThanXBooks(int minimumBooks){
        return xmlAggregationRepository.xQueryFindAuthorsWithMoreThanXBooks(minimumBooks);
    }

    public List<AuthorBooks> xQueryFindAuthorsWithNationality(String nationality){
        return xmlAggregationRepository.xQueryFindAuthorsWithNationality(nationality);
    }

    public List<AuthorBooks> xQueryFindAuthorsWithBooksOfLanguage(String language){
        return xmlAggregationRepository.xQueryFindAuthorsWithBooksOfLanguage(language);
    }

}
