package com.course.bookmgmtbackend.controller;

import java.net.URI;
import java.net.URI;
import java.util.List;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.course.bookmgmtbackend.entity.Book;
import com.course.bookmgmtbackend.service.BookService;
 

 
@RestController
@RequestMapping("/api/books")
public class BookController {
 
	private final BookService bookService;
 
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}
 
	
	@GetMapping("/{id}")
	public ResponseEntity<Book> getBook(@PathVariable Long id) {
		return ResponseEntity.ok(bookService.findBookById(id));
	}
 
	
	@GetMapping
	public ResponseEntity<List<Book>> getBooks() {
		return ResponseEntity.ok(bookService.getAllBooks());
	}
 
	// 200 OK
	@GetMapping("/search")
	public ResponseEntity<List<Book>> searchBooks(
			@RequestParam(required = false) String query,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String language,
			@RequestParam(required = false) String publisher,
			@RequestParam(required = false) Boolean available) {
 
		return ResponseEntity.ok(
				bookService.searchBooksFromDb(query, category, language, publisher, available));
	}
 

	@PostMapping
	public ResponseEntity<Book> addBook(@RequestBody Book book) {
		Book saved = bookService.addBook(book);
 
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(saved.getId())
				.toUri();
 
		return ResponseEntity.created(location).body(saved);
	}
 
	
	@PostMapping("/addAll")
	public ResponseEntity<List<Book>> addAllBooks(@RequestBody List<Book> books) {
		return ResponseEntity.status(201).body(bookService.addAllBooks(books));
	}
 
	
	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
		return ResponseEntity.ok(bookService.updateBook(id, book));
	}
 

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
	}
}
 
