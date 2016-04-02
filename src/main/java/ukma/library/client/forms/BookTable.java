package ukma.library.client.forms;

import ukma.library.client.LibraryClient;
import ukma.library.client.forms.tables.BooksTable;
import ukma.library.client.forms.tables.ReadersTable;
import ukma.library.server.entity.Book;
import ukma.library.server.entity.User;
import ukma.library.server.service.LibraryService;

import java.awt.*;
import java.awt.event.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.*;

public class BookTable extends JFrame{
    private JLabel headerLabel;
    private JLabel errorLabel;

    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel editionLabel;
    private JLabel yearLabel;
    private JLabel keyWordsLabel;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField keyWordsField;
    private JTextField editionField;
    private JSpinner yearSpinner;


    private JPanel controlPanel;
    private JPanel mainPanel;
    private LibrarianPage librarianForm;
    public BookTable(LibrarianPage librarian){
        super("Add book");
        this.librarianForm = librarian;
        prepareGUI();
    }

    private void prepareGUI(){
        this.setSize(400, 500);
        this.setLayout(new GridLayout(3, 1));

        headerLabel = new JLabel("Додати нову книгу", JLabel.CENTER );
        errorLabel = new JLabel("", JLabel.CENTER );
        errorLabel.setVisible(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(headerLabel);
        textPanel.add(errorLabel);
        textPanel.setSize(500,150);
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1));

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        this.add(textPanel);
        this.add(mainPanel);

        this.add(controlPanel);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    void showEventDemo(){
        titleLabel = new JLabel("Заголовок", JLabel.RIGHT );
        authorLabel = new JLabel("Автор", JLabel.RIGHT );
        editionLabel = new JLabel("Видання", JLabel.RIGHT );
        yearLabel = new JLabel("Рік видання", JLabel.RIGHT );
        keyWordsLabel = new JLabel("Ключові слова", JLabel.RIGHT );

        titleField = new JTextField(70);
        authorField = new JTextField(70);
        editionField = new JTextField(50);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        SpinnerModel yearModel = new SpinnerNumberModel(currentYear, //initial value
                currentYear - 600, //min
                currentYear, //max
                1);                //step
        yearSpinner = new JSpinner(yearModel);


        keyWordsField = new JTextField(120);

        JButton addButton = new JButton("Додати");
        JButton backButton = new JButton("Повернутися назад");

        addButton.addActionListener(new AddButtonClickListener());
        backButton.addActionListener(new BackButtonClickListener());

        mainPanel.add(titleLabel);
        mainPanel.add(titleField);
        mainPanel.add(authorLabel);
        mainPanel.add(authorField);
        mainPanel.add(editionLabel);
        mainPanel.add(editionField);
        mainPanel.add(yearLabel);
        mainPanel.add(yearSpinner);
        mainPanel.add(keyWordsLabel);
        mainPanel.add(keyWordsField);

        controlPanel.add(addButton);
        controlPanel.add(backButton);

        this.setVisible(true);
    }


    private void back(){
        setVisible(false); //you can't see me!
        dispose(); //Destroy the JFrame object
    }
    private boolean validateTextField(String field, int minLength, String error) {
        if(field.length() >= minLength) {
            return true;
        } else {
            errorLabel.setVisible(true);
            errorLabel.setText(error);
            return false;
        }
    }
    private class AddButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            errorLabel.setVisible(false);
            if(!validateTextField(titleField.getText(), 5, "Довжина титулки має бути більша, ніж 5 символів")) return;
            if(!validateTextField(authorField.getText(), 5, "Довжина автора має бути більшою, ніж 5 символів")) return;
            if(!validateTextField(editionField.getText(), 1, "Довжина видання має бути не порожньою")) return;
            if(!validateTextField(keyWordsField.getText(), 20, "Довжина ключових слів має бути більшою, ніж 20 символів")) return;

            Book b = new Book();
            b.setTitle(titleField.getText());
            b.setAuthor(authorField.getText());
            b.setEdition(editionField.getText());
            b.setYear(Integer.parseInt(yearSpinner.getValue().toString()));


            try {
                // Adding new book
                LibraryClient.library.addBook(b);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

            try {
                // Reinitialization of book list
                ArrayList<Book> books = (ArrayList<Book>) LibraryClient.library.getAllBooks();
                librarianForm.getAllBooksTable().setModel(new BooksTable(books));
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

            back();
        }
    }

    private class BackButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            back();
        }
    }
}
