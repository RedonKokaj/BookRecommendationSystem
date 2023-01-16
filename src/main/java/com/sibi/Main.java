package com.sibi;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
   static QueryDB connection = new QueryDB();

   public static void main(String[] args) {
      //showCategories();
      selectSuggestionMethod();
   }

   private static void selectSuggestionMethod()
   {
      final JFrame frame = new JFrame("Book Suggestion Menu");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(300, 200);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JButton suggestCategoryButton = new JButton("Suggest by Category");
      suggestCategoryButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showCategories();
         }
      });
      panel.add(suggestCategoryButton);
      panel.add(Box.createHorizontalGlue());

      JButton suggestBookButton = new JButton("Suggest by Book");
      suggestBookButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showBooks();
         }
      });
      panel.add(suggestBookButton);
      panel.add(Box.createHorizontalGlue());

      JButton exitButton = new JButton("Exit");
      exitButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            QueryDB.terminate();
         }
      });
      panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   //Suggestion selecting a category
   private static void showCategories() {
      final JFrame frame = new JFrame("Book Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      List < String > categories = collectCategories();
      DefaultListModel < String > categoriesListModel = new DefaultListModel < > ();
      for (String category: categories) {
         categoriesListModel.addElement(category);
      }
      final JList < String > categoriesList = new JList < > (categoriesListModel);
      JScrollPane scrollPane = new JScrollPane(categoriesList);
      panel.add(scrollPane);

      //Suggestion button
      JButton suggestButton = new JButton("Suggest");
      suggestButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String selectedCategory = categoriesList.getSelectedValue();
            final List < String > books = requestSuggestion(selectedCategory);
            frame.setTitle(selectedCategory + " Books");
            panel.removeAll();

            //create a new JList with the updated list of strings
            DefaultListModel < String > newStringListModel = new DefaultListModel < > ();
            for (String book: books) {
               newStringListModel.addElement(book);
            }
            final JList < String > suggestedBooks = new JList < > (newStringListModel);
            JScrollPane scrollPane = new JScrollPane(suggestedBooks);
            panel.add(scrollPane);

            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(1300, 25));
            
            panel.add(searchBar);
            searchBar.getDocument().addDocumentListener(new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  filterList();
               }
               public void removeUpdate(DocumentEvent e) {
                  filterList();
               }
               public void insertUpdate(DocumentEvent e) {
                  filterList();
               }

               public void filterList() {
                  String filterText = searchBar.getText();
                  List<String> filteredBooks = new ArrayList<>();
                  for (String book : books) {
                     if (book.toLowerCase().contains(filterText.toLowerCase())) {
                        filteredBooks.add(book);
                     }
                  }
                  DefaultListModel<String> newBooksListModel = new DefaultListModel<>();
                  for (String book : filteredBooks) {
                     newBooksListModel.addElement(book);
                  }
                  suggestedBooks.setModel(newBooksListModel);
                  panel.revalidate();
                  panel.repaint();
               }
            });

            JButton seeMoreButton = new JButton("See More");
            seeMoreButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  String selectedBook = suggestedBooks.getSelectedValue();
                  String descriptionRetrieved = retrieveDescription(selectedBook);

                  //frame.setVisible(false);
                  showBookDescription(descriptionRetrieved);
               }
            });
            panel.add(seeMoreButton);

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  frame.setVisible(false);
                  showCategories();
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  QueryDB.terminate();
               }
            });
            panel.add(exitButton);

            //refresh the JPanel
            panel.revalidate();
            panel.repaint();
         }
      });
      panel.add(suggestButton);
      panel.add(Box.createHorizontalGlue());

      //Back button
      JButton backButton = new JButton("Back");
      backButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            selectSuggestionMethod();
         }
      });
      panel.add(backButton);

      //Exit button
      JButton exitButton = new JButton("Exit");
      exitButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            QueryDB.terminate();

         }
      });
      panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   //Suggestion selecting a book
   private static void showBooks() {
      final JFrame frame = new JFrame("Book Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      final List < String > books = collectBooks();
      DefaultListModel < String > booksListModel = new DefaultListModel < > ();
      for (String book: books) {
         booksListModel.addElement(book);
      }
      final JList < String > booksList = new JList < > (booksListModel);
      JScrollPane scrollPane = new JScrollPane(booksList);
      panel.add(scrollPane);

      final JTextField searchBar = new JTextField();
      searchBar.setMaximumSize(new Dimension(1300, 25));
      
      panel.add(searchBar);
      searchBar.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent e) {
            filterList();
         }
         public void removeUpdate(DocumentEvent e) {
            filterList();
         }
         public void insertUpdate(DocumentEvent e) {
            filterList();
         }

         public void filterList() {
            String filterText = searchBar.getText();
            List<String> filteredBooks = new ArrayList<>();
            for (String book : books) {
               if (book.toLowerCase().contains(filterText.toLowerCase())) {
                  filteredBooks.add(book);
               }
            }
            DefaultListModel<String> newBooksListModel = new DefaultListModel<>();
            for (String book : filteredBooks) {
               newBooksListModel.addElement(book);
            }
            booksList.setModel(newBooksListModel);
            panel.revalidate();
            panel.repaint();
         }
      });

      //Suggestion button
      JButton suggestButton = new JButton("Suggest");
      suggestButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String selectedBook = booksList.getSelectedValue();
            String bookCategory = getBookCategory(selectedBook);
            final List < String > booksRetrieved = requestSuggestion(bookCategory);
            frame.setTitle(bookCategory + " Books");
            panel.removeAll();

            //create a new JList with the updated list of strings
            DefaultListModel < String > newStringListModel = new DefaultListModel < > ();
            for (String book: booksRetrieved) {
               newStringListModel.addElement(book);
            }
            final JList < String > suggestedBooks = new JList < > (newStringListModel);
            JScrollPane scrollPane = new JScrollPane(suggestedBooks);
            panel.add(scrollPane);

            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(1300, 25));

            panel.add(searchBar);
            searchBar.getDocument().addDocumentListener(new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  filterList();
               }
               public void removeUpdate(DocumentEvent e) {
                  filterList();
               }
               public void insertUpdate(DocumentEvent e) {
                  filterList();
               }

               public void filterList() {
                  String filterText = searchBar.getText();
                  List<String> filteredBooks = new ArrayList<>();
                  for (String book : booksRetrieved) {
                     if (book.toLowerCase().contains(filterText.toLowerCase())) {
                        filteredBooks.add(book);
                     }
                  }
                  DefaultListModel<String> newBooksListModel = new DefaultListModel<>();
                  for (String book : filteredBooks) {
                     newBooksListModel.addElement(book);
                  }
                  suggestedBooks.setModel(newBooksListModel);
                  panel.revalidate();
                  panel.repaint();
               }
            });

            JButton seeMoreButton = new JButton("See More");
            seeMoreButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  String selectedBook = booksList.getSelectedValue();
                  String descriptionRetrieved = retrieveDescription(selectedBook);

                  //frame.setVisible(false);
                  showBookDescription(descriptionRetrieved);
               }
            });
            panel.add(seeMoreButton);

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  
                  frame.setVisible(false);
                  showBooks();
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  QueryDB.terminate();
               }
            });
            panel.add(exitButton);

            //refresh the JPanel
            panel.revalidate();
            panel.repaint();
         }
      });
      panel.add(suggestButton);
      panel.add(Box.createHorizontalGlue());

      //Back button
      JButton backButton = new JButton("Back");
      backButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            selectSuggestionMethod();
         }
      });
      panel.add(backButton);

      //Exit button
      JButton exitButton = new JButton("Exit");
      exitButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            QueryDB.terminate();
         }
      });
      panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   private static void showBookDescription(String descriptionRetrieved) {
      final JFrame frame = new JFrame("Book Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JLabel label = new JLabel(descriptionRetrieved);
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(descriptionRetrieved + "<br>");
      label.setText("<html>" + strBuilder.toString() + "</html>");
      panel.add(label);

      JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  
                  frame.setVisible(false);
                  //showBooks();
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  QueryDB.terminate();
               }
            });
            panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   private static String retrieveDescription(String selectedBook)
   {
      return connection.retrieveDescription(selectedBook);
   }

   private static List < String > collectCategories() {
      return connection.retrieveCategories();
   }

   private static List < String > collectBooks() {
      return connection.retrieveBooks();
   }

   private static String getBookCategory(String bookTitle) {
      return connection.getBookCategory(bookTitle);
   }

   private static List < String > requestSuggestion(String category) {
      return connection.suggestBook(category);
   }
}