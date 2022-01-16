package com.andrewmagid;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Window extends JFrame {
    private final String DEFAULT_DIR_LBL = "Select Destination...";
    // components
    private Button generateBtn;
    private Button selectDirBtn;
    private Button selectAllBtn;
    private Button deselectAllBtn;
    //    private Button deleteBtn;
    private Button searchBtn;
    private JTextField searchBar;
    private JRadioButton excelRadio;
    private JRadioButton csvRadio;
    private JPanel mainPanel;
    private JTable downloadTable;
    private DownloadTableModel dtm;
    private JFrame frame;
    private File dir;
    private JLabel dirNameLbl;
    private JTextField filenameField;
    // constants
    private int SCREEN_WIDTH = 650;
    private int SCREEN_HEIGHT = 550;


    public Window() {
        frame = new JFrame("Historical Price Downloader");
        mainPanel = new JPanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        addUI();
        addListeners();

        frame.pack();
        frame.setLocationRelativeTo(null);  // center window
        frame.setVisible(true);
    }

    private void addUI() {
        // initialize components + set alignment
        searchBar = new HintTextField("Search:");
        // TODO: figure out if there is a better way than this 40 magic number
        searchBar.setColumns(20);
        searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchBtn = new Button("Search");

        downloadTable = new JTable();
        dtm = new DownloadTableModel();
        dtm.setDownloadTableParameters(downloadTable);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(downloadTable.getModel());
        downloadTable.setRowSorter(sorter);

        excelRadio = new JRadioButton("xlsx");
        excelRadio.setSelected(true);

        csvRadio = new JRadioButton("csv");

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(excelRadio);
        btnGroup.add(csvRadio);

        generateBtn = new Button("Generate");
        selectDirBtn = new Button("...");
        selectDirBtn.setPreferredSize(new Dimension(30,20));

        selectAllBtn = new Button("Select All");
        deselectAllBtn = new Button("Deselect All");

        JPanel southUiPanel = new JPanel(new FlowLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());

        Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
        dirNameLbl = new JLabel(DEFAULT_DIR_LBL);
        dirNameLbl.setBorder(border);

        filenameField = new HintTextField("filename");
        filenameField.setColumns(7);

        // add to panels
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(downloadTable), BorderLayout.CENTER);
        mainPanel.add(southUiPanel, BorderLayout.SOUTH);

        southUiPanel.add(excelRadio);
        southUiPanel.add(csvRadio);
        southUiPanel.add(dirNameLbl);
        southUiPanel.add(selectDirBtn);
        southUiPanel.add(filenameField);
        southUiPanel.add(generateBtn);

        searchPanel.add(searchBar);
        searchPanel.add(searchBtn);
        searchPanel.add(selectAllBtn);
        searchPanel.add(deselectAllBtn);
    }

    private void addListeners() {
        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SecurityUniverse.populateFilteredMutualFundUniverseBySearchBar(searchBar.getText());
                if (SecurityUniverse.fundList.size() == 0) {
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    try {
                        dtm.fireTableRowsDeleted(0, 0);
                    } catch (IndexOutOfBoundsException ignored) {
                        System.out.println("Fire Table Rows Deleted. Error: " + ignored);
                    }
                } else {
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    dtm.fireTableDataChanged();
                }
                downloadTable.repaint();
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SecurityUniverse.populateFilteredMutualFundUniverseBySearchBar(searchBar.getText());
                if (SecurityUniverse.fundList.size() == 0) {
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    try {
                        dtm.fireTableRowsDeleted(0, 0);
                    } catch (IndexOutOfBoundsException ignored) {
                        System.out.println("Fire Table Rows Deleted. Error: " + ignored);
                    }
                } else {
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    dtm.fireTableDataChanged();
                }
                downloadTable.repaint();
            }
        });

        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SecurityUniverse.selectedCount == 0) {
                    JOptionPane.showMessageDialog(null, "No Selection Made.", "Selection Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (dirNameLbl.getText().equals(DEFAULT_DIR_LBL)) {
                    JOptionPane.showMessageDialog(null, "Select a destination folder.", "Destination Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (filenameField.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "Declare output filename.", "Filename Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ArrayList<String> cmds = new ArrayList<>(Arrays.asList(System.getProperty("user.dir") + "/venv/bin/python3", System.getProperty("user.dir") + "/src/run.py"));
                if (excelRadio.isSelected())
                    cmds.add("-e");
                else
                    cmds.add("-c");
                cmds.add(dir.getAbsolutePath().toString()+"/"+filenameField.getText());
                for (SecurityUniverse.Security sec : SecurityUniverse.fundList) {
                    if (sec.getSelected())
                        cmds.add(sec.getTicker());
                }
                ProcessBuilder pb = new ProcessBuilder(cmds).inheritIO();
                try {
                    JFrame progressFrame = new JFrame("...Processing...");
                    progressFrame.setLayout(new BorderLayout());
                    progressFrame.setPreferredSize(new Dimension(150,75));
                    progressFrame.pack();
                    progressFrame.setLocationRelativeTo(null);
                    progressFrame.setVisible(true);
                    Process p = pb.start();
                    p.waitFor();
                    progressFrame.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        selectDirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    dir = fileChooser.getSelectedFile();
                    dirNameLbl.setText(dir.getAbsolutePath());
                }
            }
        });

        selectAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (SecurityUniverse.Security sec : SecurityUniverse.fundList)
                    sec.setSelected();
                downloadTable.repaint();
            }
        });

        deselectAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (SecurityUniverse.Security sec : SecurityUniverse.fundList)
                    sec.setDeselected();
                downloadTable.repaint();
            }
        });


    }
}
