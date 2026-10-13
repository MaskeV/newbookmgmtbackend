package com.course.bookmgmtbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.course.bookmgmtbackend.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	
	 @Query("""
		        SELECT b FROM Book b
		        WHERE (:category  IS NULL OR b.category  = :category)
		          AND (:language  IS NULL OR b.language  = :language)
		          AND (:publisher IS NULL OR b.publisher = :publisher)
		          AND (:available IS NULL OR b.available = :available)
		        """)
		    List<Book> findByFilters(@Param("category") String category,
		                             @Param("language") String language,
		                             @Param("publisher") String publisher,
		                             @Param("available") Boolean available);
	 
	 @Query("""
			    SELECT b FROM Book b
			    WHERE (:category  IS NULL OR LOWER(b.category)  = LOWER(:category))
			      AND (:language  IS NULL OR LOWER(b.language)  = LOWER(:language))
			      AND (:publisher IS NULL OR LOWER(b.publisher) = LOWER(:publisher))
			      AND (:available IS NULL OR b.available = :available)
			      AND (:query IS NULL
			           OR LOWER(b.title)       LIKE LOWER(CONCAT('%', :query, '%'))
			           OR LOWER(b.author)      LIKE LOWER(CONCAT('%', :query, '%'))
			           OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')))
			    """)
			List<Book> searchBooks(@Param("query") String query,
			                       @Param("category") String category,
			                       @Param("language") String language,
			                       @Param("publisher") String publisher,
			                       @Param("available") Boolean available);

}
