package com.andrewmagid;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Window extends JFrame {
    // components
    private Button generateBtn;
    //    private Button downloadBtn;
    private Button selectAll;
    //    private Button deleteBtn;
    private Button searchBtn;
    private JTextField searchBar;
    private JRadioButton excelRadio;
    private JRadioButton csvRadio;
    private JPanel mainPanel;
    private JTable downloadTable;
    private DownloadTableModel dtm;

    // constants
    private int SCREEN_WIDTH = 650;
    private int SCREEN_HEIGHT = 550;


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

        excelRadio = new JRadioButton("xlsx");
        excelRadio.setSelected(true);

        csvRadio = new JRadioButton("csv");

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(excelRadio);
        btnGroup.add(csvRadio);

        generateBtn = new Button("Generate");
//        downloadBtn = new Button("Download");
//        deleteBtn = new Button("Delete");


        JPanel southUiPanel = new JPanel(new FlowLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());

        // add to panels
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(downloadTable), BorderLayout.CENTER);
        mainPanel.add(southUiPanel, BorderLayout.SOUTH);

        southUiPanel.add(excelRadio);
        southUiPanel.add(csvRadio);
        southUiPanel.add(generateBtn);
//        southUiPanel.add(deleteBtn);
//        southUiPanel.add(downloadBtn);

        searchPanel.add(searchBar);
        searchPanel.add(searchBtn);
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
                    JOptionPane.showMessageDialog(null, "No Selection Made", "", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                ArrayList<String> cmds = new ArrayList<>(Arrays.asList("python3", System.getProperty("user.dir")+"/src/run.py"));
                if (excelRadio.isSelected()) {
                    cmds.add("- e");
                } else {
                    cmds.add("- c");
                }
                for (SecurityUniverse.Security sec : SecurityUniverse.fundList) {
                    if (sec.getSelected())
                        cmds.add(" " + sec.getTicker());
                }
                ProcessBuilder pb = new ProcessBuilder(cmds).inheritIO();
                try {
                    Process p = pb.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
