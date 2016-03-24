package com.ostremskiy;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by DevAs on 18.02.2016.
 */
public class MainFrame extends JFrame {
    private DefaultTableCellRenderer renderer;
    private DefaultTableCellRenderer rendererDef;
    private String CountryFiltr;
    private String ContinentFiltr;
    private Dimension comboDimension = new Dimension(110,25);
    private JTable table;
    private TableModel tableModel;
    private DBConnection db;
    private JToggleButton btnF;
    private JComboBox<String> continentsCombo;
    private JComboBox<String> countriesCombo;
    private JComboBox<String> countryAddCity;
    private JComboBox<String> continentAddCountry;
    private  Iterator<String> it;
    private boolean isFiltr=false;
    private HashMap<String,String> defFilterValues;
    MainFrame(){
        db = new DBConnection("myDB.db");
        setTitle("База міст");
        make();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private void make() {
        renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean countryEq = false;
                boolean continentEq = false;
                String tCountry = table.getValueAt(row, 2).toString();
                String tContinent = table.getValueAt(row, 3).toString();
                if (tCountry.equals(CountryFiltr) || CountryFiltr.equals(defFilterValues.get("country")))
                    countryEq = true;
                if (tContinent.equals(ContinentFiltr) || ContinentFiltr.equals(defFilterValues.get("continent")))
                    continentEq = true;

                if (countryEq && continentEq) {
                    cell.setBackground(Color.YELLOW);
                } else {
                    cell.setBackground(Color.WHITE);
                }
                return cell;
            }
        };
        rendererDef = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBackground(Color.WHITE);
                return cell;
            }
        };
        JPanel mainPanel = new JPanel();

        JPanel txtPanel = new JPanel();
        tableModel = new TableModel(db, this);
        table = new JTable(tableModel);
        //table.setMaximumSize(new Dimension(350, 300));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(500, 450));
        scroll.setMinimumSize(new Dimension(10, 10));
        txtPanel.add(scroll);



        JLabel countryFilterL = new JLabel("Країна");
        JLabel continentFilterL = new JLabel("Континент");
        JPanel filterBtnPanel = new JPanel();
        btnF = new JToggleButton("Фільтр");
        filterBtnPanel.add(btnF);
        btnF.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                isFiltr = true;
            else
                isFiltr = false;
            updateTable(true);
        });
        defFilterValues = new HashMap<>();
        defFilterValues.put("country", "Будь-яка");
        defFilterValues.put("continent", "Будь-який");
        continentsCombo = new JComboBox<>();
        continentsCombo.setPreferredSize(comboDimension);
        continentsCombo.addItem(defFilterValues.get("continent"));
        continentsCombo.addActionListener(e -> {
            String continentName = (String) continentsCombo.getSelectedItem();
            String countryName = (String) countriesCombo.getSelectedItem();
            if (!countryName.equals(defFilterValues.get("country"))) {
                if (!continentName.equals(db.getContinentByCountryName((String) countriesCombo.getSelectedItem()))) {
                    countriesCombo.setSelectedItem(defFilterValues.get("country"));
                }
            }
            updateTable(false);
        });
        it = db.getContinents().iterator();
        while (it.hasNext()) {
            continentsCombo.addItem(it.next());
        }
        countriesCombo = new JComboBox<>();
        countriesCombo.setPreferredSize(comboDimension);
        countriesCombo.addItem(defFilterValues.get("country"));
        countriesCombo.addActionListener(e -> {
            String selectedCountry = (String) countriesCombo.getSelectedItem();
            if (!selectedCountry.equals(defFilterValues.get("country"))) {
                continentsCombo.setSelectedItem(db.getContinentByCountryName(selectedCountry));
            }
            updateTable(false);
        });
        it = db.getCountries().iterator();
        ArrayList<String> countriesList = new ArrayList<>();
        while (it.hasNext()) {
            countriesList.add(it.next());
        }
        Collections.sort(countriesList);
        countriesList.forEach(countriesCombo::addItem);
        //show filter comps
        JPanel filterPanel = new JPanel(new BorderLayout());
        JPanel filterNPanel = new JPanel(new GridLayout(0, 2, 0, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Фільтр"));
        filterNPanel.add(countryFilterL);
        filterNPanel.add(countriesCombo);
        filterNPanel.add(continentFilterL);
        filterNPanel.add(continentsCombo);
        filterPanel.add(filterNPanel, BorderLayout.NORTH);
        filterPanel.add(filterBtnPanel, BorderLayout.CENTER);

        JTextField txtField = new JTextField();
        txtField.setColumns(3);
        JButton btnDel = new JButton("Видалити");
        btnDel.setEnabled(false);
        btnDel.addActionListener(e -> delete(txtField.getText()));
        txtField.addCaretListener(e -> {
            if (!txtField.getText().equals(""))
                btnDel.setEnabled(true);
            else
                btnDel.setEnabled(false);
        });

        //show delete comps
        JPanel deletePanel = new JPanel();
        deletePanel.setBorder(BorderFactory.createTitledBorder("Видалення"));
        deletePanel.add(txtField);
        deletePanel.add(btnDel);
        //-------
        //show insert city panel
        JPanel addCityPanel;
        addCityPanel = new JPanel(new BorderLayout(0, 3));
        JButton okCityBtn = new JButton("Додати");
        addCityPanel.setBorder(BorderFactory.createTitledBorder("Додати місто"));
        JPanel gridCityPanel = new JPanel(new GridLayout(0, 2, 0, 5));
        JLabel cityNameAddCityL = new JLabel("Назва");
        JLabel countryNameAddCityL = new JLabel("Країна");
        JTextField cityName = new JTextField(10);
        cityName.addCaretListener(e -> {
            if (!cityName.getText().equals(""))
                okCityBtn.setEnabled(true);
            else
                okCityBtn.setEnabled(false);
        });
        countryAddCity = new JComboBox<>();
        countryAddCity.setPreferredSize(comboDimension);

        it = db.getCountries().iterator();
        ArrayList<String> countriesListC = new ArrayList<>();
        while (it.hasNext()){
            countriesListC.add(it.next());
        }
        Collections.sort(countriesListC);
        countriesListC.forEach(countryAddCity::addItem);

        JPanel okCityPanel = new JPanel();
        okCityBtn.setEnabled(false);
        okCityBtn.addActionListener(e -> {

            String country = (String)countryAddCity.getSelectedItem();
            String cityNameS = cityName.getText();
            db.addCity(cityNameS, country);
            tableModel.fireTableDataChanged();
        });
        okCityPanel.add(okCityBtn);
        gridCityPanel.add(cityNameAddCityL);
        gridCityPanel.add(cityName);
        gridCityPanel.add(countryNameAddCityL);
        gridCityPanel.add(countryAddCity);
        addCityPanel.add(gridCityPanel, BorderLayout.NORTH);
        addCityPanel.add(okCityPanel, BorderLayout.CENTER);
        //------
        //show insert country panel
        JPanel addCountryPanel;
        addCountryPanel = new JPanel(new BorderLayout(0, 3));
        JButton okCountryBtn = new JButton("Додати");
        addCountryPanel.setBorder(BorderFactory.createTitledBorder("Додати країну"));
        JPanel gridCountryPanel = new JPanel(new GridLayout(0, 2, 0, 5));
        JLabel countryNameAddCountryL = new JLabel("Назва");
        JLabel continentNameAddCountryL = new JLabel("Континент");
        JTextField countryName = new JTextField(10);
        countryName.addCaretListener(e->{
            if(!cityName.getText().equals(""))
                okCountryBtn.setEnabled(true);
            else
                okCountryBtn.setEnabled(false);
        });
        continentAddCountry = new JComboBox<>();
        continentAddCountry.setPreferredSize(comboDimension);
        it = db.getContinents().iterator();
        while (it.hasNext()) {
            continentAddCountry.addItem(it.next());
        }
        JPanel okCountryPanel = new JPanel();
        okCountryBtn.setEnabled(false);
        okCountryBtn.addActionListener(e -> {
            String country = (String)continentAddCountry.getSelectedItem();
            String countryNameS = countryName.getText();
            db.addCountry(countryNameS, country);
            remakeCombos();
        });
        okCountryPanel.add(okCountryBtn);
        gridCountryPanel.add(countryNameAddCountryL);
        gridCountryPanel.add(countryName);
        gridCountryPanel.add(continentNameAddCountryL);
        gridCountryPanel.add(continentAddCountry);
        addCountryPanel.add(gridCountryPanel, BorderLayout.NORTH);
        addCountryPanel.add(okCountryPanel, BorderLayout.CENTER);
        //------
        JPanel panel1 = new JPanel(new GridBagLayout());
        panel1.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                //g.drawRect(x,y,width-1,height-1);
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(0,0,0,10);
            }
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx=1;
        panel1.add(filterPanel,c);
        panel1.add(deletePanel,c);
        panel1.add(addCityPanel,c);
        panel1.add(addCountryPanel,c);
        mainPanel.add(txtPanel);
        add(mainPanel, BorderLayout.WEST);
        add(panel1, BorderLayout.EAST);
    }
    private void delete(String e){
        if(e.equals(""))
            JOptionPane.showMessageDialog(this, "Введіть номер поля!","Попередження",JOptionPane.WARNING_MESSAGE);
        else {
            try {
                int tmp = Integer.parseInt(e);
                db.deleteCity(tmp);
            } catch (IndexOutOfBoundsException ex) {
                JOptionPane.showMessageDialog(this, "Такого поля не існує!","Попередження",JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Номер має бути в числовому форматі!","Попередження",JOptionPane.WARNING_MESSAGE);
            }
            updateTable(false);
        }
    }
    public void updateTable(boolean fromBtn){
        if(isFiltr) {
            try {
                CountryFiltr = (String)countriesCombo.getSelectedItem();
                ContinentFiltr = (String)continentsCombo.getSelectedItem();
                for (int i = 0; i < table.getColumnCount(); i++)
                    table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }catch (NumberFormatException ex){
                if(fromBtn) {
                    JOptionPane.showMessageDialog(this, "Введіть дані для фільтру!", "Попередження", JOptionPane.WARNING_MESSAGE);
                }
            }
        }else {
            for(int i=0; i<table.getColumnCount(); i++)
                table.getColumnModel().getColumn(i).setCellRenderer(rendererDef);
        }
        tableModel.fireTableDataChanged();
    }
    private void remakeCombos(){
        int i = countriesCombo.getItemCount();
        int k=1;
        while(k<i) {
            System.out.println("ssss");
            countriesCombo.removeItemAt(1);
            k++;
        }
        it = db.getCountries().iterator();
        ArrayList<String> countriesList = new ArrayList<>();
        while (it.hasNext()){
            countriesList.add(it.next());
        }
        Collections.sort(countriesList);
        countriesList.forEach(countriesCombo::addItem);

        //remake addCity CountriesComboBox
        countryAddCity.removeAllItems();
        it = db.getCountries().iterator();
        while (it.hasNext()) {
            countryAddCity.addItem(it.next());
        }
        //---
    }
}

