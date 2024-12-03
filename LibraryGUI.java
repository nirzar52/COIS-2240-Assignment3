import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class LibraryGUI extends Application {
	private static LibraryGUI instance;
    private Library library;
    private Transaction transaction;

    private TabPane tabPane;
    private TextField memberIdField, memberNameField, bookIdField, bookTitleField;
    private ListView<Member> memberListView;
    private ListView<Book> bookListView;
    private TextArea transactionHistoryArea;

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

    @Override
    public void start(Stage primaryStage) {
    	instance = this;
        primaryStage.setTitle("Library Management System");

        tabPane = new TabPane();
        tabPane.getTabs().addAll(
        		createMembersTab(),
                createBooksTab(),
                createBorrowTab(),
                createReturnTab(),
                createViewBorrowedBooksTab()
        );

        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Tab createMembersTab() {
        Tab membersTab = new Tab("Members");
        membersTab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");
        memberNameField = new TextField();
        memberNameField.setPromptText("Member Name");

        Button addMemberButton = new Button("Add Member");
        addMemberButton.setOnAction(e -> addMember());

        memberListView = new ListView<>();
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshMemberList());

        vbox.getChildren().addAll(memberIdField, memberNameField, addMemberButton, memberListView, refreshButton);
        membersTab.setContent(vbox);

        membersTab.setOnSelectionChanged(event -> refreshMembersTab(membersTab));
        return membersTab;
    }

    private Tab createBooksTab() {
        Tab booksTab = new Tab("Books");
        booksTab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        bookTitleField = new TextField();
        bookTitleField.setPromptText("Book Title");

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> addBook());

        bookListView = new ListView<>();
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshBookList());

        vbox.getChildren().addAll(bookIdField, bookTitleField, addBookButton, bookListView, refreshButton);
        booksTab.setContent(vbox);

        booksTab.setOnSelectionChanged(event -> refreshBooksTab(booksTab));
        return booksTab;
    }
    
    private Tab createBorrowTab() {
        Tab borrowTab = new Tab("Borrow");
        borrowTab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<Member> memberComboBox = new ComboBox<>();
        memberComboBox.setPromptText("-- Select a member --");
        
        ComboBox<Book> bookComboBox = new ComboBox<>();
        bookComboBox.setPromptText("-- Select a book --");

        Button borrowButton = new Button("Borrow Book");
        borrowButton.setOnAction(e -> borrowBook(memberComboBox.getValue(), bookComboBox.getValue()));

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            refreshMemberList();
            refreshBookList();
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            bookComboBox.setItems(FXCollections.observableArrayList(library.getBooks()));
        });

        vbox.getChildren().addAll(memberComboBox, bookComboBox, borrowButton, refreshButton);
        borrowTab.setContent(vbox);

        borrowTab.setOnSelectionChanged(event -> refreshBorrowTab(borrowTab));
        return borrowTab;
    }
    
    private Tab createReturnTab() {
        Tab returnTab = new Tab("Return");
        returnTab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<Member> memberComboBox = new ComboBox<>();
        memberComboBox.setPromptText("-- Select a member --");
        
        ComboBox<Book> bookComboBox = new ComboBox<>();
        bookComboBox.setPromptText("-- Select a book --");

        memberComboBox.setOnAction(e -> {
            Member selectedMember = memberComboBox.getValue();
            if (selectedMember != null) {
                bookComboBox.setItems(FXCollections.observableArrayList(selectedMember.getBorrowedBooks()));
            } else {
                bookComboBox.getItems().clear();
            }
        });

        Button returnButton = new Button("Return Book");
        returnButton.setOnAction(e -> returnBook(memberComboBox.getValue(), bookComboBox.getValue()));

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            bookComboBox.getItems().clear();
        });

        vbox.getChildren().addAll(memberComboBox, bookComboBox, returnButton, refreshButton);
        returnTab.setContent(vbox);

        returnTab.setOnSelectionChanged(event -> refreshReturnTab(returnTab));
        return returnTab;
    }
    
    private Tab createViewBorrowedBooksTab() {
        Tab borrowedBooksTab = new Tab("View Borrowed");
        borrowedBooksTab.setClosable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ComboBox<Member> memberComboBox = new ComboBox<>();
        memberComboBox.setPromptText("-- Select a member --");

        ListView<Book> borrowedBooksListView = new ListView<>();

        Button viewButton = new Button("View Borrowed Books");
        viewButton.setOnAction(e -> {
            Member selectedMember = memberComboBox.getValue();
            if (selectedMember != null) {
                ObservableList<Book> borrowedBooks = FXCollections.observableArrayList(selectedMember.getBorrowedBooks());
                borrowedBooksListView.setItems(borrowedBooks);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a member.");
            }
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            borrowedBooksListView.getItems().clear();
        });

        vbox.getChildren().addAll(memberComboBox, viewButton, borrowedBooksListView, refreshButton);
        borrowedBooksTab.setContent(vbox);

        borrowedBooksTab.setOnSelectionChanged(event -> refreshViewBorrowedBooksTab(borrowedBooksTab));
        return borrowedBooksTab;
    }
    
    private void addMember() {
    	try {
            int id = Integer.parseInt(memberIdField.getText());
            String name = memberNameField.getText();
            Member newMember = new Member(id, name);
            if (library.addMember(newMember)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully.");
                refreshMemberList();
                memberIdField.clear();
                memberNameField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add member. ID may already exist.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid member ID. Please enter a number.");
        }
    }

    private void addBook() {
    	try {
            int id = Integer.parseInt(bookIdField.getText());
            String title = bookTitleField.getText();
            Book newBook = new Book(id, title);
            if (library.addBook(newBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");
                refreshBookList();
                bookIdField.clear();
                bookTitleField.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book. ID may already exist.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid book ID. Please enter a number.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }
    
    private void borrowBook(Member member, Book book) {
        if (member != null && book != null) {
            if (transaction.borrowBook(book, member)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
                refreshBookList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to borrow book. Book may not be available.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a member and a book.");
        }
    }
    
    private void returnBook(Member member, Book book) {
        if (member != null && book != null) {
            if (transaction.returnBook(book, member)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
                refreshBookList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book. It may not have been borrowed by this member.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a member and a book.");
        }
    }
    
    private void refreshMemberList() {
        ObservableList<Member> members = FXCollections.observableArrayList(library.getMembers());
        memberListView.setItems(members);
    }

    private void refreshBookList() {
        ObservableList<Book> books = FXCollections.observableArrayList(library.getBooks());
        bookListView.setItems(books);
    }
    
    private void refreshMembersTab(Tab tab) {
        if (tab.isSelected()) {
            refreshMemberList();
        }
    }

    private void refreshBooksTab(Tab tab) {
        if (tab.isSelected()) {
            refreshBookList();
        }
    }
    
    private void refreshBorrowTab(Tab tab) {
        if (tab.isSelected()) {
            ComboBox<Member> memberComboBox = (ComboBox<Member>) ((VBox) tab.getContent()).getChildren().get(0);
            ComboBox<Book> bookComboBox = (ComboBox<Book>) ((VBox) tab.getContent()).getChildren().get(1);
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            bookComboBox.setItems(FXCollections.observableArrayList(
        		library.getBooks().stream().filter(Book::isAvailable).collect(Collectors.toList())
            ));
        }
    }
    
    private void refreshReturnTab(Tab tab) {
        if (tab.isSelected()) {
            ComboBox<Member> memberComboBox = (ComboBox<Member>) ((VBox) tab.getContent()).getChildren().get(0);
            ComboBox<Book> bookComboBox = (ComboBox<Book>) ((VBox) tab.getContent()).getChildren().get(1);
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            bookComboBox.getItems().clear();
        }
    }
    
    private void refreshViewBorrowedBooksTab(Tab tab) {
        if (tab.isSelected()) {
            ComboBox<Member> memberComboBox = (ComboBox<Member>) ((VBox) tab.getContent()).getChildren().get(0);
            ListView<Book> borrowedBooksListView = (ListView<Book>) ((VBox) tab.getContent()).getChildren().get(2);
            memberComboBox.setItems(FXCollections.observableArrayList(library.getMembers()));
            borrowedBooksListView.getItems().clear();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
