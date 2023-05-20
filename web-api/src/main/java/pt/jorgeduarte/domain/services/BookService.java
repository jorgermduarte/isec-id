package pt.jorgeduarte.domain.services;

import pt.jorgeduarte.domain.entities.Book;
import pt.jorgeduarte.domain.repositories.XMLBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final XMLBookRepository bookRepository;

    @Autowired
    public BookService(XMLBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> saveAll(List<Book> books) {
        for (Book book : books) {
            bookRepository.save(book);
        }
        return books;
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAuthorBooks(Long authorId) {
        return bookRepository.getAuthorBooks(authorId);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> xPathFindBooksByTitle(String title) {
        return bookRepository.xPathFindBooksByTitle(title);
    }
    public List<Book> xPathFindBooksByLanguage(String language){
        return bookRepository.xPathFindBooksByLanguage(language);
    }

    public List<Book> xPathFindBooksByNumberOfPages(Long pages){
        return bookRepository.xPathFindBooksByNumberOfPages(pages);
    }

    public List<Book> xPathFindBooksByPublicationDate(String publicationDateString){
        return bookRepository.xPathFindBooksByPublicationDate(publicationDateString);
    }

    public List<Book> xPathFindBooksByIsbn(String isbn){
        return bookRepository.xPathFindBooksByIsbn(isbn);
    }

    public List<Book> xQueryFindBooksByPublisher(String publisher){
        return bookRepository.xQueryFindBooksByPublisher(publisher);
    }

    public List<Book> xQueryFindBooksWithMaxPrice(Double maxPrice){
        return bookRepository.xQueryFindBooksWithMaxPrice(maxPrice);
    }

    public List<Book> xQueryGetXMostExpensiveBooks(int numberOfBooks){
        return bookRepository.xQueryGetXMostExpensiveBooks(numberOfBooks);
    }

}