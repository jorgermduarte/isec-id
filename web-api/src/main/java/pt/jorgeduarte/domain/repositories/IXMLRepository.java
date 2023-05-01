package pt.jorgeduarte.domain.repositories;

import java.util.List;
import java.util.Optional;

public interface IXMLRepository<T>{
    T save(T book);
    Optional<T> findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
}
