package mate.academy;

import java.math.BigDecimal;
import java.util.Optional;
import mate.academy.dao.BookDao;
import mate.academy.lib.Injector;
import mate.academy.model.Book;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.academy");

    public static void main(String[] args) {
        final BookDao bookDao = (BookDao) injector.getInstance(BookDao.class);
        // initialize field values using setters or constructor
        System.out.println("--- Creating new book ---");
        Book book = new Book();
        book.setTitle("The Lord og the Rings");
        book.setPrice(new BigDecimal("550.50"));
        Book createdBook = bookDao.create(book);
        System.out.println("Created book: " + createdBook);
        System.out.println();
        // test other methods from BookDao
        System.out.println("--- Find by id ---");
        Long bookId = book.getId();
        Optional<Book> findBookOptional = bookDao.findById(bookId);
        if (findBookOptional.isPresent()) {
            System.out.println("Found book: " + findBookOptional.get());
        } else {
            System.out.println("Book not found");
        }
        System.out.println();
        System.out.println("--- Find all ---");
        bookDao.findAll().forEach(System.out::println);
        System.out.println();
        System.out.println("--- Update book ---");
        Book bookToUpdate = book;
        bookToUpdate.setPrice(new BigDecimal("700.00"));
        bookDao.update(bookToUpdate);
        Optional<Book> updatedBook = bookDao.findById(bookId);
        if (updatedBook.isPresent()) {
            System.out.println("Updated book: " + updatedBook.get());
        } else {
            System.out.println("Book not found");
        }
        System.out.println();
        System.out.println("--- Delete book ---");
        bookDao.deleteById(bookId);
        Optional<Book> deletedBook = bookDao.findById(bookId);
        if (deletedBook.isEmpty()) {
            System.out.println("Deleted book");
        } else {
            System.out.println("Book not found");
        }
        System.out.println();
    }
}
