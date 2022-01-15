package com.andrewmagid;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class DownloadTableModel extends AbstractTableModel {
    String[] columnNames = {" ", "Ticker", "Name"};
    private static final int BOOLEAN_COLUMN = 0;

    @Override
    public int getRowCount() {
        return SecurityUniverse.fundList.size();
    }

    @Override
    //bool, ticker, name, weight
    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return SecurityUniverse.fundList.get(rowIndex).getSelected();
            case 1:
                return SecurityUniverse.fundList.get(rowIndex).getTicker();
            case 2:
                return SecurityUniverse.fundList.get(rowIndex).getName();
            default:
                return null;
        }
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //true only if bool in 1st column or weight in 2nd column
        return columnIndex == BOOLEAN_COLUMN;
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        //fireTableCellUpdated(rowIndex, columnIndex);
        if (columnIndex == BOOLEAN_COLUMN) {
            SecurityUniverse.fundList.get(rowIndex).toggleSelected();
        }
    }

    public void setDownloadTableParameters(JTable downloadTable){
        int defaultInclusionPortfolioWidth = 20;
        int defaultTickerWidth = 65;
        int defaultNameWidth = 450;

        DownloadTableModel downloadTableModel = new DownloadTableModel();
        downloadTable.setModel(downloadTableModel);
        downloadTable.getColumnModel().getColumn(0).setMaxWidth(defaultInclusionPortfolioWidth);
        downloadTable.getColumnModel().getColumn(0).setMinWidth(defaultInclusionPortfolioWidth);
        downloadTable.getColumnModel().getColumn(1).setMinWidth(defaultTickerWidth);
        downloadTable.getColumnModel().getColumn(1).setMaxWidth(defaultTickerWidth);
        downloadTable.getColumnModel().getColumn(2).setPreferredWidth(defaultNameWidth);
        downloadTable.getColumnModel().getColumn(2).setMinWidth(defaultNameWidth);
    }
}
