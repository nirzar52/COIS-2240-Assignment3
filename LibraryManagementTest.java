import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LibraryManagementTest {

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
}
