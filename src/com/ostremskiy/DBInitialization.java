package com.ostremskiy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by DevAs on 27.02.2016.
 */
public class DBInitialization {
    private Connection conn;
    private Statement statmt;

    public DBInitialization(Connection conn, Statement statmt) {
        this.conn = conn;
        this.statmt = statmt;
        CreateTables();
        WriteTables();
    }
    private void CreateTables(){
        try {
            statmt.execute("CREATE TABLE if not exists 'continents' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT, 'name' text)");
            statmt.execute("CREATE TABLE if not exists 'countries' ('id' INTEGER PRIMARY KEY  AUTOINCREMENT, 'name' text, 'id_continent' integer)");
            statmt.execute("CREATE TABLE if not exists 'cities' ('idA' INTEGER PRIMARY KEY  AUTOINCREMENT,'id' INTEGER, 'name' text, 'id_country' integer)");
            System.out.println("Tables are successfully created!");
        } catch (SQLException e) {
            System.out.println("Cannot create table!");
        }
    }
    private void WriteTables(){
        try {
            String[] insertContinents = new String[]{
                    "('Europe')",
                    "('Asia');",
                    "('Africa');",
                    "('North America');",
                    "('South America');",
                    "('Oceania');",
                    "('Australia');"
            };
            String[] insertCountries = new String[]{
                    "('Angola',3)","('Botswana',3)","('Cameroon',3)","('Afghanistan',2)","('Bahrain',2)","('Bangladesh',2)",
                    "('Albania',1)","('Ukraine',1)","('Poland',1)","('Bahamas',4)","('Grenada',4)","('Cuba',4)",
                    "('Bolivia',5)","('Brazil',5)","('Peru',5)","('Australia',7)",
                    "('Nauru',6)","('Micronesia',6)","('Tonga',6)"
            };
            String[] insertCities = new String[]{
                    "(1,'Luena',1)","(2,'Nata',2)","(3,'Garoua',3)","(4,'Kabul',4)","(5,'Manama',5)","(6,'Dhaka',6)",
                    "(7,'Tirane',7)","(8,'Nikolaev',8)","(9,'Krakow',9)","(10,'Nassau',10)","(11,'Tivoli',11)","(12,'Colon',12)",
                    "(13,'La Paz',13)","(14,'Brasilia',14)","(15,'Pucallpa',15)","(16,'Canberra',16)","(17,'Sydney',16)","(18,'Adelaide',16)",
                    "(19,'Nauru',17)","(20,'Palikir',18)","(21,'Nomuka',19)"
            };

            for(String tmp:insertContinents){
                statmt.execute("INSERT INTO 'continents' (name) VALUES "+tmp);
            }
            System.out.println("Continents are successfully added!");
            for(String tmp:insertCountries){
                statmt.execute("INSERT INTO 'countries' ('name', 'id_continent') VALUES "+tmp);
            }
            System.out.println("Countries are successfully added!");
            for(String tmp:insertCities){
                statmt.execute("INSERT INTO 'cities' ('id','name','id_country') VALUES "+tmp);
            }
            System.out.println("Cities are successfully added!");

        } catch (SQLException e) {
            System.out.println("Cannot write table!");
        }
    }
}
