package com.andrewmagid;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame {
    // components
    private Button generateBtn;
    private Button downloadBtn;
    private Button selectAll;
    private Button deleteBtn;
    private Button searchBtn;
    private JTextField searchBar;
    private JRadioButton excelRadio;
    private JRadioButton csvRadio;
    private JCheckBox cutDatesCheckbox;
    private JPanel mainPanel;
    private JTable downloadTable;
    private DownloadTableModel dtm;

    // constants
    private int SCREEN_WIDTH = 750;
    private int SCREEN_HEIGHT = 500;


    public Window() {
        JFrame frame = new JFrame("Historical Price Downloader");
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
        searchBar.setColumns(40);
        searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchBtn = new Button("Search");

        downloadTable = new JTable();
        dtm = new DownloadTableModel();
        dtm.setDownloadTableParameters(downloadTable);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(downloadTable.getModel());
        downloadTable.setRowSorter(sorter);

        excelRadio = new JRadioButton("excel");
        excelRadio.setSelected(true);
        excelRadio.setActionCommand("excel");

        csvRadio = new JRadioButton("csv");
        csvRadio.setActionCommand("csv");

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(excelRadio);
        btnGroup.add(csvRadio);

        generateBtn = new Button("Generate");
        deleteBtn = new Button("Delete");
        downloadBtn = new Button("Download");

        cutDatesCheckbox = new JCheckBox("Don't Cut Dates");
        cutDatesCheckbox.setSelected(false);

        // add to panel
        mainPanel.add(searchBar, BorderLayout.PAGE_START);
        mainPanel.add(searchBtn, BorderLayout.PAGE_START);
        mainPanel.add(new JScrollPane(downloadTable), BorderLayout.CENTER);
        mainPanel.add(excelRadio, BorderLayout.PAGE_END);
        mainPanel.add(csvRadio, BorderLayout.PAGE_END);
        mainPanel.add(cutDatesCheckbox, BorderLayout.PAGE_END);
        mainPanel.add(generateBtn, BorderLayout.PAGE_END);
        mainPanel.add(deleteBtn, BorderLayout.PAGE_END);
        mainPanel.add(downloadBtn, BorderLayout.PAGE_END);
    }

    private void addListeners() {
        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SecurityUniverse.populateFilteredMutualFundUniverseBySearchBar(searchBar.getText());
                if (SecurityUniverse.fundList.size() == 0){
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    try {
                        dtm.fireTableRowsDeleted(0,0);
                    }
                    catch(IndexOutOfBoundsException ignored){
                        System.out.println("Fire Table Rows Deleted. Error: " + ignored);
                    }
                }
                else{
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
                if (SecurityUniverse.fundList.size() == 0){
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    try {
                        dtm.fireTableRowsDeleted(0,0);
                    }
                    catch(IndexOutOfBoundsException ignored){
                        System.out.println("Fire Table Rows Deleted. Error: " + ignored);
                    }
                }
                else{
                    dtm = (DownloadTableModel) downloadTable.getModel();
                    dtm.fireTableDataChanged();
                }
                downloadTable.repaint();
            }
        });

    }

}
