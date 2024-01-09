import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.text.*;
import java.io.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JOptionPane;
import javax.swing.undo.*;
import java.awt.print.*;


public class NotePad extends JFrame {
    private static ArrayList<NotePad> openDocuments = new ArrayList<>();

    private JTextArea textArea;
    private UndoManager undoManager;
    private int fontSize = 12;
    private JCheckBoxMenuItem statusBarMenuItem;
    private JPanel statusBar;
    private JLabel statusLabel;

    public NotePad() {
        setTitle("Notepad");
        setSize(900, 600);
        textArea = new JTextArea();
        undoManager = new UndoManager();
        add(new JScrollPane(textArea));
        createMenus();
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undoManager.addEdit(evt.getEdit());
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                dispose();
                openDocuments.remove(NotePad.this);
            }
        });
        openDocuments.add(this);
    }
    private void createMenus() {
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newTabItem = new JMenuItem("New Tab");
        JMenuItem newWindowItem = new JMenuItem("New Window");
        JMenuItem closeTabItem = new JMenuItem("Close Tab");
        JMenuItem closeWindowItem = new JMenuItem("Close Window");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem saveAllItem = new JMenuItem("Save All");
        JMenuItem pageSetUpItem = new JMenuItem("Page Setup");
        JMenuItem printItem = new JMenuItem("Print");
        JMenuItem exitItem = new JMenuItem("Exit");

        newTabItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        newWindowItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NotePad newWindow = new NotePad();
                newWindow.setVisible(true);
            }
        });
        closeTabItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        closeWindowItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(NotePad.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        textArea.setText(content.toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(NotePad.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try (PrintWriter writer = new PrintWriter(selectedFile)) {
                        writer.write(textArea.getText());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(NotePad.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try (PrintWriter writer = new PrintWriter(selectedFile)) {
                        writer.write(textArea.getText());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        saveAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAllDocuments();
            }
        });

        pageSetUpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPageSetupDialog();
            }
        });

        printItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printDocument();
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        fileMenu.add(newTabItem);
        fileMenu.add(newWindowItem);
        fileMenu.add(closeTabItem);
        fileMenu.add(closeWindowItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(saveAllItem);
        fileMenu.add(pageSetUpItem);
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem pastItem = new JMenuItem("Past");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem findNextItem = new JMenuItem("Find Next");
        JMenuItem findPreviousItem = new JMenuItem("Find Previous");
        JMenuItem replaceItem = new JMenuItem("Replace");
        JMenuItem goToItem = new JMenuItem("Go To");

        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performUndo();
            }
        });

        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });

        cutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });

        pastItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });

        selectAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.selectAll();
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
            }
        });

        findItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFindDialog();
            }
        });

        findNextItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = JOptionPane.showInputDialog(NotePad.this, "Enter text to find:");
                if (searchText != null && !searchText.isEmpty()) {
                    String text = textArea.getText();
                    int startIndex = textArea.getSelectionEnd();
                    int index = text.indexOf(searchText, startIndex);

                    if (index != -1) {
                        textArea.setSelectionStart(index);
                        textArea.setSelectionEnd(index + searchText.length());
                    } else {
                        JOptionPane.showMessageDialog(NotePad.this, "Text not found: " + searchText);
                    }
                }
            }
        });

        findPreviousItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = JOptionPane.showInputDialog(NotePad.this, "Enter text to find:");
                if (searchText != null && !searchText.isEmpty()) {
                    String text = textArea.getText();
                    int startIndex = textArea.getSelectionStart() - 1;
                    int index = text.lastIndexOf(searchText, startIndex);

                    if (index != -1) {
                        textArea.setSelectionStart(index);
                        textArea.setSelectionEnd(index + searchText.length());
                    } else {
                        JOptionPane.showMessageDialog(NotePad.this, "Text not found: " + searchText);
                    }
                }
            }
        });

        replaceItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog replaceDialog = new JDialog(NotePad.this, "Replace");
                replaceDialog.setLayout(new GridLayout(3, 2));

                JLabel findLabel = new JLabel("Find:");
                JTextField findField = new JTextField();
                JLabel replaceLabel = new JLabel("Replace:");
                JTextField replaceField = new JTextField();

                JButton replaceButton = new JButton("Replace");
                JButton replaceAllButton = new JButton("Replace All");

                replaceButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String searchText = findField.getText();
                        String replaceText = replaceField.getText();

                        if (searchText != null && !searchText.isEmpty()) {
                            String text = textArea.getText();
                            int startIndex = textArea.getSelectionEnd();
                            int index = text.indexOf(searchText, startIndex);

                            if (index != -1) {
                                textArea.replaceRange(replaceText, index, index + searchText.length());
                                textArea.setSelectionStart(index);
                                textArea.setSelectionEnd(index + replaceText.length());
                            } else {
                                JOptionPane.showMessageDialog(replaceDialog, "Text not found: " + searchText);
                            }
                        }

                        replaceDialog.dispose();
                    }
                });

                replaceAllButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String searchText = findField.getText();
                        String replaceText = replaceField.getText();

                        if (searchText != null && !searchText.isEmpty()) {
                            String text = textArea.getText();
                            text = text.replaceAll(searchText, replaceText);
                            textArea.setText(text);
                        }
                        replaceDialog.dispose();
                    }
                });

                replaceDialog.add(findLabel);
                replaceDialog.add(findField);
                replaceDialog.add(replaceLabel);
                replaceDialog.add(replaceField);
                replaceDialog.add(replaceButton);
                replaceDialog.add(replaceAllButton);

                replaceDialog.setSize(300, 150);
                replaceDialog.setLocationRelativeTo(NotePad.this);
                replaceDialog.setVisible(true);
            }
        });

        goToItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lineNumberStr = JOptionPane.showInputDialog(NotePad.this, "Enter line number:");
                if (lineNumberStr != null && !lineNumberStr.isEmpty()) {
                    try {
                        int lineNumber = Integer.parseInt(lineNumberStr);
                        int startOffset = textArea.getLineStartOffset(lineNumber - 1);
                        int endOffset = textArea.getLineEndOffset(lineNumber - 1);
                        textArea.setSelectionStart(startOffset);
                        textArea.setSelectionEnd(endOffset);
                    } catch (NumberFormatException | BadLocationException ex) {
                        JOptionPane.showMessageDialog(NotePad.this, "Invalid line number");
                    }
                }
            }
        });


        editMenu.add(undoItem);
        editMenu.addSeparator();
        editMenu.add(copyItem);
        editMenu.add(selectAllItem);
        editMenu.add(cutItem);
        editMenu.add(pastItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(findNextItem);
        editMenu.add(findPreviousItem);
        editMenu.addSeparator();
        editMenu.add(replaceItem);
        editMenu.add(goToItem);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontSizeItem = new JMenuItem("Font Size");
        JMenuItem fontStyleItem = new JMenuItem("Font Style");
        JMenuItem fontColorItem = new JMenuItem("Font Color");
        JMenuItem bgColorItem = new JMenuItem("Background Color");
        JMenuItem themeItem = new JMenuItem("Theme");
        JMenuItem dateItem = new JMenuItem("Date");
        JMenuItem timeItem = new JMenuItem("Time");

        fontSizeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFontSizeDialog();
            }
        });

        fontStyleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFontStyleDialog();
            }
        });

        fontColorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFontColorDialog();
            }
        });

        bgColorItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBgColorDialog();
            }
        });

        themeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showThemeDialog();
            }
        });

        timeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String currentTime = sdf.format(new Date());
                textArea.append(currentTime + "\n");
            }
        });
        dateItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = sdf.format(new Date());
                textArea.append(currentDate + "\n");
            }
        });

        formatMenu.add(fontSizeItem);
        formatMenu.add(fontStyleItem);
        formatMenu.add(fontColorItem);
        formatMenu.add(bgColorItem);
        formatMenu.add(themeItem);
        formatMenu.addSeparator();
        formatMenu.add(dateItem);
        formatMenu.add(timeItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        JMenuItem restoreDefaultZoomItem = new JMenuItem("Restore Default Zoom");
        statusBarMenuItem = new JCheckBoxMenuItem("Status Bar", true);
        JMenuItem wordWrapItem = new JMenuItem("Word Wrap");
        zoomInItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontSize += 2;
                updateTextAreaFont();
            }
        });
        zoomOutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fontSize > 2) {
                    fontSize -= 2;
                    updateTextAreaFont();
                }
            }
        });
        restoreDefaultZoomItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontSize = 12;
                updateTextAreaFont();
            }
        });
        statusBarMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleStatusBar();
            }
        });

        wordWrapItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleWordWrap();
            }
        });
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(restoreDefaultZoomItem);
        viewMenu.addSeparator();
        viewMenu.add(statusBarMenuItem);
        viewMenu.add(wordWrapItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem fileChooseItem = new JMenuItem("File Choose");
        JMenuItem shortcutKeyItem = new JMenuItem("Shortcut Keys");

        fileChooseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFileChooseDialog();
            }
        });
        shortcutKeyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showShortcutKeysDialog();
            }
        });
        helpMenu.add(fileChooseItem);
        helpMenu.add(shortcutKeyItem);

        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(formatMenu);
        menubar.add(viewMenu);
        menubar.add(helpMenu);
        setJMenuBar(menubar);
        createStatusBar();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Exit")) {
            dispose();
        }
    }

    private void showFileChooseDialog() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    private void showShortcutKeysDialog() {
        String shortcutInfo = "Ctrl + N: New Tab\n" +
                "Ctrl + W: Close Tab\n" +
                "Ctrl + O: Open\n" +
                "Ctrl + S: Save\n" +
                "Ctrl + P: Print\n" +
                "Ctrl + Z: Undo\n" +
                "Ctrl + C: Copy\n" +
                "Ctrl + X: Cut\n" +
                "Ctrl + V: Paste\n" +
                "Ctrl + F: Find\n" +
                "Ctrl + H: Replace\n" +
                "Ctrl + G: Go To\n" +
                "Ctrl + A: Select All\n";

        JTextArea shortcutArea = new JTextArea(shortcutInfo);
        shortcutArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(shortcutArea);

        JOptionPane.showMessageDialog(this, scrollPane, "Shortcut Keys", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Row: 1   Column: 1   Width: UTF-8   ");
        statusBar.add(statusLabel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        statusBar.setVisible(statusBarMenuItem.isSelected());

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStatus();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStatus();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStatus();
            }
        });
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatus();
            }
        });
    }
    private void updateStatus() {
        try {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPosition) + 1;
            int columnNumber = caretPosition - textArea.getLineStartOffset(lineNumber - 1) + 1;
            int textWidth = textArea.getLineEndOffset(lineNumber - 1) - textArea.getLineStartOffset(lineNumber - 1);
            String encoding = "UTF-8";
            statusLabel.setText(String.format("Row: %d   Column: %d   Width: %d   Encoding: %s   ",
                    lineNumber, columnNumber, textWidth, encoding));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void toggleStatusBar() {
    boolean isVisible = statusBarMenuItem.isSelected();

    if (isVisible) {
        statusBar.setVisible(true);
    } else {
        statusBar.setVisible(false);
    }
}

    private void toggleWordWrap() {
        textArea.setLineWrap(!textArea.getLineWrap());
        textArea.setWrapStyleWord(true);
    }

    private void showFontSizeDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Font Size:");
        try {
            fontSize = Integer.parseInt(input);
            updateTextAreaFont();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Font Size");
        }
    }

    private void showFontStyleDialog() {
        String[] fontStyleOptions = {"Plain", "Bold", "Italic", "Bold Italic"};
        int selectedOption = JOptionPane.showOptionDialog(
                this,
                "Select Font Style:",
                "Font Style",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                fontStyleOptions,
                fontStyleOptions[0]
        );
        int style = Font.PLAIN;
        switch (selectedOption) {
            case 1:
                style = Font.BOLD;
                break;
            case 2:
                style = Font.ITALIC;
                break;
            case 3:
                style = Font.BOLD | Font.ITALIC;
                break;
        }
        Font currentFont = textArea.getFont();
        Font newFont = new Font(currentFont.getName(), style, currentFont.getSize());
        textArea.setFont(newFont);
    }


    private void showFontColorDialog() {
        Color color = JColorChooser.showDialog(this, "Choose Font Color", textArea.getForeground());
        if (color != null) {
            textArea.setForeground(color);
        }
    }

    private void showBgColorDialog() {
        Color color = JColorChooser.showDialog(this, "Choose Background Color", textArea.getBackground());
        if (color != null) {
            textArea.setBackground(color);
        }
    }

    private void showThemeDialog() {
        UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();

        String[] themeNames = new String[lookAndFeels.length];
        for (int i = 0; i < lookAndFeels.length; i++) {
            themeNames[i] = lookAndFeels[i].getName();
        }

        String selectedTheme = (String) JOptionPane.showInputDialog(
                this,
                "Select a theme:",
                "Theme Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                themeNames,
                UIManager.getLookAndFeel().getName());

        if (selectedTheme != null) {
            for (LookAndFeelInfo info : lookAndFeels) {
                if (selectedTheme.equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        SwingUtilities.updateComponentTreeUI(NotePad.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void showPageSetupDialog() {
        PrinterJob pageSetup = PrinterJob.getPrinterJob();
        pageSetup.pageDialog(pageSetup.defaultPage());
    }

    private void printDocument() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            try {
                printJob.setPrintable(new Printable() {
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                            throws PrinterException {
                        if (pageIndex > 0) {
                            return Printable.NO_SUCH_PAGE;
                        }

                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                        g2d.drawString(textArea.getText(), 100, 100); // You might need a more sophisticated rendering logic

                        return Printable.PAGE_EXISTS;
                    }
                });

                printJob.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void saveAllDocuments() {
        for (NotePad document : openDocuments) {
            document.saveDocument();
        }
    }
    private void saveDocument() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                writer.write(textArea.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showFindDialog() {
        String searchText = JOptionPane.showInputDialog(this, "Find:");
        if (searchText != null && !searchText.isEmpty()) {
            int startIndex = textArea.getText().indexOf(searchText, textArea.getCaretPosition());
            if (startIndex != -1) {
                textArea.setSelectionStart(startIndex);
                textArea.setSelectionEnd(startIndex + searchText.length());
            }
        }
    }

    private void performUndo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    private void updateTextAreaFont() {
        Font currentFont = textArea.getFont();
        Font newFont = currentFont.deriveFont((float) fontSize);
        textArea.setFont(newFont);
    }
    public static void main(String[] args) {
        NotePad app = new NotePad();
        app.setVisible(true);
    }
}