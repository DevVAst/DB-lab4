package com.ostremskiy;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by DevAs on 18.02.2016.
 */
public class DBConnection{
    public Connection conn;
    public Statement statmt;
    public Statement statmt2;
    public ResultSet resSet;
    public ResultSet resSet2;
    public ResultSet resSet3;
    private ResultSetMetaData meta;
    private boolean DB=false;
    DBConnection(String DBName) {
        ConnectDB(DBName);
        if(!DB) {
            DBInitialization dbInitialization = new DBInitialization(conn, statmt);
        }
        //ShowDB();
        //closeDB();
    }
    private void ConnectDB(String DBName){
        String url = "jdbc:sqlite:"+DBName;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            System.out.println("DataBase was opened!");
            statmt = conn.createStatement();
            statmt2 = conn.createStatement();
            try {
                statmt.execute("SELECT name FROM 'continents' WHERE  id=0");
                DB=true;
            }catch (SQLException e){
                DB=false;
            }
        } catch (SQLException e) {
            System.out.println("Cannon create connection!");
        } catch (ClassNotFoundException e) {
            System.out.println("Cannot find drivers!");
        }
    }
    private void closeDB(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public int getCitiesRowsCount(){
        int count=0;
        try {
            resSet = statmt.executeQuery("SELECT * FROM cities");
            while(resSet.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public int getCountriesRowsCount(){
        int count=0;
        try {
            resSet = statmt.executeQuery("SELECT * FROM countries");
            while(resSet.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public String getCityInf(int r, int c){
        try {
            resSet = statmt.executeQuery("SELECT * FROM cities WHERE id=" + (r + 1));
            if(resSet.next()) {
                meta = resSet.getMetaData();
                if(c==0||c==1) {
                    return resSet.getString(meta.getColumnName(c + 2));
                }else {
                    int countryId=resSet.getInt("id_country");
                    int continentId=0;
                    resSet2= conn.createStatement().executeQuery("SELECT * FROM countries WHERE id="+countryId);
                    if(resSet2.next()) {
                        continentId=resSet2.getInt("id_continent");
                        if(c==2) {
                            return resSet2.getString("name");
                        }
                    }
                    resSet3= conn.createStatement().executeQuery("SELECT * FROM continents WHERE id="+continentId);
                    if(resSet3.next()) {
                        return resSet3.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot select!");
        }

        return "";
    }
    public void deleteCity(int id){
        try {
            statmt.executeUpdate("DELETE FROM cities WHERE id=" + id);
            resSet = statmt.executeQuery("SELECT * FROM cities WHERE id > "+id);
            while(resSet.next()) {
                conn.createStatement().executeUpdate("UPDATE cities SET ID=" + (resSet.getInt("id") - 1) + " WHERE id=" + resSet.getInt("id"));
            }
            System.out.println("Deleted city with id="+id);
        } catch (SQLException e) {
            System.out.println("Cannot select!");
        }
    }
    public void updateValue(String value, int row, int col){
        try {
            statmt.executeUpdate("UPDATE cities SET name = '"+value+"' WHERE id="+(row+1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getCountries(){
        ArrayList<String> resultList = new ArrayList<>();
        try {
            resSet = statmt.executeQuery("SELECT name FROM countries");
            while(resSet.next()){
                resultList.add(resSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
    public ArrayList<String> getContinents(){
        ArrayList<String> resultList = new ArrayList<>();
        try {
            resSet = statmt.executeQuery("SELECT name FROM continents");
            while(resSet.next()){
                resultList.add(resSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
    public void addCity(String name, String country){
        try {
            int countryId=0;
            resSet=statmt.executeQuery("SELECT id FROM countries WHERE name ='"+country+"'");
            if(resSet.next())
                countryId=resSet.getInt("id");
            int count = getCitiesRowsCount()+1;
            statmt.executeUpdate("INSERT INTO cities ('id','name', 'id_country') VALUES (" + count + ",'" + name + "'," + countryId + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addCountry(String name, String continent){
        try {
            int continentId=0;
            resSet=statmt.executeQuery("SELECT id FROM continents WHERE name ='"+continent+"'");
            if(resSet.next())
                continentId=resSet.getInt("id");
            statmt.executeUpdate("INSERT INTO countries ('name', 'id_continent') VALUES ('" + name + "'," + continentId + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getContinentByCountryName(String countryName){
        String result="";
        try {
            int continentId=0;
            resSet = statmt.executeQuery("SELECT id_continent FROM countries WHERE name ='"+countryName+"'");
            if(resSet.next())
                continentId=resSet.getInt("id_continent");
            else
                System.err.println("Cannot find id_continent by country name="+countryName);
            resSet = statmt.executeQuery("SELECT name FROM continents WHERE id="+continentId);
            if(resSet.next()) {
                result = resSet.getString("name");
            }else{
                System.err.println("Cannot find continent by id="+continentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
