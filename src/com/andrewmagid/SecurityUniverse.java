package com.andrewmagid;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SecurityUniverse {
    private File fundCsv = new File("assets/funds.csv");
    private File nasdaqCsv = new File("assets/nasdaqlisted.csv");
    private File otherCsv = new File("assets/otherlisted.csv");
    private String DELIMITER = "\\|";

    private static ArrayList<Security> masterFundList;
    public static ArrayList<Security> fundList;


    public static class Security implements Comparable<Security> {
        private String ticker; private String name; private boolean selected;
        public Security(String ticker, String name) {this.selected = false; this.ticker = ticker; this.name = name;}
        public String getTicker() {
            return ticker;
        }
        public String getName() {
            return name;
        }
        public boolean getSelected() {
            return selected;
        }

        public void toggleSelected() {
            selected = !selected;
        }

        @Override
        public String toString() {
            return ticker + " : " + name;
        }

        @Override
        public int compareTo(Security o) {
            return ticker.compareTo(o.ticker);
        }
    }

    public SecurityUniverse() throws FileNotFoundException{
        masterFundList = new ArrayList<>();
        populateFromCsv(fundCsv);
        populateFromCsv(nasdaqCsv);
        populateFromCsv(otherCsv);
        Collections.sort(masterFundList);
        populateFilteredMutualFundUniverseBySearchBar("");
    }

    public static void populateFilteredMutualFundUniverseBySearchBar(String searchBarText){
        searchBarText = searchBarText.toLowerCase();

        if (searchBarText.equals(""))
            fundList = (ArrayList<Security>) masterFundList.clone();
        else{
            fundList.clear();
            for (Security security : masterFundList){
                if (security.getName().toLowerCase().contains(searchBarText))
                    fundList.add(security);
                else if (security.getTicker().toLowerCase().contains(searchBarText))
                    fundList.add(security);
            }
        }
    }

    public ArrayList<Security> getFundList() {
        return masterFundList;
    }

    private void populateFromCsv(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        sc.useDelimiter(DELIMITER);
        while (sc.hasNextLine()) {
            masterFundList.add(new Security(sc.next(), sc.next()));
            sc.nextLine();  // continue reading till the end of the line
        }
    }

    public static void main(String[] args) {
        try {
            SecurityUniverse su = new SecurityUniverse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

