import javafx.application.Application;
import javafx.stage.Stage;

public class LibraryGUI extends Application {
    private static LibraryGUI instance;
    private Library library;
    private Transaction transaction;

    public LibraryGUI() {
        library = new Library();
        transaction = Transaction.getTransaction();
    }

    public static LibraryGUI getInstance() {
        if (instance == null) {
            instance = new LibraryGUI();
        }
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");
    }
}
