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

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}