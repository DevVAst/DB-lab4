package com.ostremskiy;

import javax.swing.table.AbstractTableModel;

/**
 * Created by DevAs on 18.02.2016.
 */
public class TableModel extends AbstractTableModel {
    private  MainFrame mainFrame;
    private DBConnection db;
    TableModel(DBConnection db, MainFrame mainFrame) {
        super();
        this.db = db;
        this.mainFrame = mainFrame;
    }
    @Override
    public int getRowCount() {
        return db.getCitiesRowsCount();
    }
    @Override
    public int getColumnCount() {
        return 4;
    }
    @Override
    public Object getValueAt(int r, int c) {
        return db.getCityInf(r,c);
    }
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
        mainFrame.updateTable(false);
        String value1=(String)value;
        //удаляем пробел вначале строки
        value1 = value1.trim();
        db.updateValue(value1, rowIndex, columnIndex);
    }
    @Override
    public String getColumnName(int c) {
        switch (c){
            case 0:
                return "ID";
            case 1:
                return "Назва міста";
            case 2:
                return "Назва країни";
            case 3:
                return "Назва континенту";
        }
        return "";
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex==1)
            return true;
        else
            return false;
    }


}
