import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryManagementTest {
	
	private Transaction transaction;
    private Book book;
    private Member member;
    
    @BeforeEach
    void setUp() throws Exception {
        book = new Book(100, "Test Book");
        member = new Member(1, "John Doe");
        transaction = Transaction.getTransaction();
    }

    @Test
    void testBorrowReturn() {
        // Ensure the book is initially available
        assertTrue(book.isAvailable());

        // Test successful borrowing
        assertTrue(transaction.borrowBook(book, member));
        assertFalse(book.isAvailable());

        // Test failed borrowing (book already borrowed)
        assertFalse(transaction.borrowBook(book, member));

        // Test successful returning
        assertTrue(transaction.returnBook(book, member));
        assertTrue(book.isAvailable());

        // Test failed returning (book already returned)
        assertFalse(transaction.returnBook(book, member));
    }
    
    @Test
    void testBookId() {
        try {
            // Test valid boundary cases
            Book book1 = new Book(100, "Book 100");
            assertEquals(100, book1.getId());

            Book book2 = new Book(999, "Book 999");
            assertEquals(999, book2.getId());

            // Test invalid cases
            assertThrows(Exception.class, () -> new Book(99, "Invalid Book"));
            assertThrows(Exception.class, () -> new Book(1000, "Invalid Book"));

            // Test exception message
            Exception exception = assertThrows(Exception.class, () -> new Book(50, "Invalid Book"));
            assertEquals("Invalid book ID. ID must be between 100 and 999.", exception.getMessage());

            exception = assertThrows(Exception.class, () -> new Book(1001, "Invalid Book"));
            assertEquals("Invalid book ID. ID must be between 100 and 999.", exception.getMessage());

        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }
    
    @Test
    void testSingletonTransaction() throws Exception {
        Constructor<Transaction> constructor = Transaction.class.getDeclaredConstructor();
        int modifiers = constructor.getModifiers();
        assertTrue(Modifier.isPrivate(modifiers));
        
        // Additional test to ensure only one instance is created
        Transaction instance1 = Transaction.getTransaction();
        Transaction instance2 = Transaction.getTransaction();
        assertSame(instance1, instance2);
    }
}
