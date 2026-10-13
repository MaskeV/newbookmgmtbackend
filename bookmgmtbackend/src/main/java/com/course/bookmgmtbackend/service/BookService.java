package com.course.bookmgmtbackend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.course.bookmgmtbackend.entity.Book;
import com.course.bookmgmtbackend.exception.BookNotFoundException;
import com.course.bookmgmtbackend.exception.BookServiceException;
import com.course.bookmgmtbackend.repository.BookRepository;

@Service
public class BookService {

	private static final Logger log = LoggerFactory.getLogger(BookService.class);

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Transactional(readOnly = true)
	public Book findBookById(Long id) {
		requireId(id);
		try {
			// Optional is never null - use orElseThrow instead of "== null"
			return bookRepository.findById(id)
					.orElseThrow(() -> new BookNotFoundException(id));
		} catch (DataAccessException e) {
			log.error("Database error while fetching book id={}", id, e);
			throw new BookServiceException("Could not fetch book with id " + id, e);
		}
	}

	@Transactional
	public Book addBook(Book book) {
		validateBook(book);
		try {
			book.setCreatedAt(LocalDateTime.now());
			Book saved = bookRepository.save(book);
			log.info("Book created with id={}", saved.getId());
			return saved;
		} catch (DataAccessException e) {
			log.error("Database error while adding book '{}'", book.getTitle(), e);
			throw new BookServiceException("Could not add book", e);
		}
	}

	@Transactional
	public void deleteBook(Long id) {
		requireId(id);
		try {
			if (!bookRepository.existsById(id)) {
				throw new BookNotFoundException(id);
			}
			bookRepository.deleteById(id);
			log.info("Book deleted id={}", id);
		} catch (DataAccessException e) {
			log.error("Database error while deleting book id={}", id, e);
			throw new BookServiceException("Could not delete book with id " + id, e);
		}
	}

	@Transactional
	public Book updateBook(Long id, Book updatedBook) {
		requireId(id);
		validateBook(updatedBook);

		Book book = findBookById(id); // throws BookNotFoundException if missing

		try {
			book.setTitle(updatedBook.getTitle().trim());
			book.setAuthor(updatedBook.getAuthor());
			book.setDescription(updatedBook.getDescription());
			book.setCategory(updatedBook.getCategory());
			book.setPublisher(updatedBook.getPublisher());
			book.setPublicationYear(updatedBook.getPublicationYear());
			book.setLanguage(updatedBook.getLanguage());
			book.setQuantity(updatedBook.getQuantity());
			book.setAvailable(updatedBook.getAvailable());
			book.setUpdatedAt(LocalDateTime.now());

			Book saved = bookRepository.save(book);
			log.info("Book updated id={}", id);
			return saved;
		} catch (DataAccessException e) {
			log.error("Database error while updating book id={}", id, e);
			throw new BookServiceException("Could not update book with id " + id, e);
		}
	}

	@Transactional(readOnly = true)
	public List<Book> getAllBooks() {
		try {
			
			return bookRepository.findAll();
		} catch (DataAccessException e) {
			log.error("Database error while fetching all books", e);
			throw new BookServiceException("Could not fetch books", e);
		}
	}

	@Transactional
	public List<Book> addAllBooks(List<Book> books) {
		// TODO Auto-generated method stub
	
		for(Book book:books) {
			addBook(book);
		}
		
		return books;
		
	}
	
	@Transactional(readOnly = true)
	public List<Book> searchBooksFromDb(String query, String category, String language,
			String publisher, Boolean available) {
		try {
			return bookRepository.searchBooks(
					blankToNull(query),
					blankToNull(category),
					blankToNull(language),
					blankToNull(publisher),
					available);
		} catch (DataAccessException e) {
			log.error("Database error while searching books (query='{}')", query, e);
			throw new BookServiceException("Could not search books", e);
		}
	}



	private void requireId(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("Book id must be a positive number");
		}
	}

	private void validateBook(Book book) {
		if (book == null) {
			throw new IllegalArgumentException("Book must not be null");
		}
		if (isBlank(book.getTitle())) {
			throw new IllegalArgumentException("Book title is required");
		}
		if (isBlank(book.getAuthor())) {
			throw new IllegalArgumentException("Book author is required");
		}
		if (book.getQuantity() != null && book.getQuantity() < 0) {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}
	}

	private String blankToNull(String value) {
		return isBlank(value) ? null : value.trim();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}