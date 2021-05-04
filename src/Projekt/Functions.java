package Projekt;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class Functions {

    String db = "jdbc:sqlite:gasstation.db";
    Connection con = null;

    public Functions() throws SQLException {
        con = DriverManager.getConnection(db);
    }

    void newDB (){
        try {
             Statement stmt = con.createStatement();
            ArrayList<String> sqlStmts = new ArrayList<>();

            if (! con.getMetaData().getTables(null,null,"WORKER",null).next()){
                sqlStmts.add("CREATE TABLE WORKER(workerID INTEGER PRIMARY KEY AUTOINCREMENT, firstname TEXT,"
                        +"lastname TEXT)");
            }
            if (! con.getMetaData().getTables(null,null,"GAS_PUMP",null).next()){
                sqlStmts.add("CREATE TABLE GAS_PUMP(gas_pumpID INTEGER PRIMARY KEY AUTOINCREMENT, gas_tank INT, in_use INT," +
                        "FOREIGN KEY (gas_tank) REFERENCES GAS_TANK(gas_tankID))");
            }

            if (! con.getMetaData().getTables(null,null,"PRODUCT_CATEGORY",null).next()){
                sqlStmts.add("CREATE TABLE PRODUCT_CATEGORY(product_categoryID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
            }

            if (! con.getMetaData().getTables(null,null,"PRODUCT",null).next()){
                sqlStmts.add("CREATE TABLE PRODUCT(productID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,"
                        +"product_categoryID INTEGER, price INTEGER, tax INTEGER, amount INTEGER,"+
                        "FOREIGN KEY (product_categoryID) REFERENCES PRODUCT_CATEGORY(product_categoryID) )");
            }
            if (! con.getMetaData().getTables(null,null,"BILL",null).next()){
                sqlStmts.add("CREATE TABLE BILL(billID INTEGER PRIMARY KEY AUTOINCREMENT, gas_pumpID INTEGER,"
                        +"total_price INTEGER, date DATE, payment TEXT," +
                        "FOREIGN KEY (gas_pumpID) REFERENCES GAS_PUMP(gas_pumpID) )");
            }

            if (! con.getMetaData().getTables(null,null,"BILL_ELEMENT",null).next()){
                sqlStmts.add("CREATE TABLE BILL_ELEMENT(billID INTEGER, productID INTEGER, amount INTEGER, " +
                        "price INTEGER, bill_elementID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "FOREIGN KEY (billID) REFERENCES BILL(billID), FOREIGN KEY (productID) REFERENCES PRODUCT(productID) )");
            }

            if (! con.getMetaData().getTables(null,null,"GAS_TANK",null).next()){
                sqlStmts.add("CREATE TABLE GAS_TANK(gas_tankID INTEGER PRIMARY KEY AUTOINCREMENT, productID INT,"
                        +" max_amount INTEGER, "
                        +" FOREIGN KEY (productID) REFERENCES PRODUCT(productID))");
            }

            if (! con.getMetaData().getTables(null,null,"LOGIN",null).next()){
                sqlStmts.add("CREATE TABLE LOGIN(loginID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " workerID INTEGER,  password PASSWORD NOT NULL, code INTEGER,"
                        +" FOREIGN KEY (workerID) REFERENCES WORKER(workerID))");
            }

            // Mockups

            sqlStmts.add("INSERT INTO WORKER(firstname, lastname) " +
                    "VALUES ('Flavio','Hermanny')");
            sqlStmts.add("INSERT INTO WORKER(firstname, lastname) " +
                    "VALUES ('Michail','Manthey')");
            sqlStmts.add("INSERT INTO WORKER(firstname, lastname) " +
                    "VALUES ('Flavio','Hermanny')");
            sqlStmts.add("INSERT INTO LOGIN(workerID, password, code) " +
                    "VALUES (1,'3627909a29c31381a071ec27f7c9ca97726182aed29a7ddd2e54353322cfb30abb9e3a6df2ac2c20fe23436311d678564d0c8d305930575f60e2d3d048184d79',2)");
            sqlStmts.add("INSERT INTO LOGIN(workerID, password, code) " +
                    "VALUES (2,'3627909a29c31381a071ec27f7c9ca97726182aed29a7ddd2e54353322cfb30abb9e3a6df2ac2c20fe23436311d678564d0c8d305930575f60e2d3d048184d79',2)");
            sqlStmts.add("INSERT INTO LOGIN(workerID, password, code) " +
                    "VALUES (0,'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec',1)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(1,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(1,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(2,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(2,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(3,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(3,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(1,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(2,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(2,0)");
            sqlStmts.add("INSERT INTO GAS_PUMP(gas_tank,in_use) VALUES(3,0)");
            sqlStmts.add("INSERT INTO GAS_TANK(productID,amount) VALUES(1,10000)");
            sqlStmts.add("INSERT INTO GAS_TANK(productID,amount) VALUES(2,7500)");
            sqlStmts.add("INSERT INTO GAS_TANK(productID,amount) VALUES(3,12000)");
            sqlStmts.add("INSERT INTO PRODUCT_CATEGORY(name) VALUES('Gas')");
            sqlStmts.add("INSERT INTO PRODUCT_CATEGORY(name) VALUES('Candy')");
            sqlStmts.add("INSERT INTO PRODUCT_CATEGORY(name) VALUES('Cleaning')");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Premium Shell',1,15,10,10000)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Regular Shell',1,11,10,7500)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Diesel',1,7,10,12000)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('MoonBar',2,3,16,50)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Twix',2,2,16,50)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('KitKat',2,5,16,50)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('LeonBar',2,3,16,50)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Glass cleaner',3,6,13,100)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Fabric cleaner',3,4,13,100)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Hyper cleaner',3,10,13,100)");
            sqlStmts.add("INSERT INTO PRODUCT(name,product_categoryID,price,tax,amount) VALUES('Wipers',3,5,13,4)");



            for(String sql : sqlStmts){
                stmt.executeUpdate(sql);
            }
            System.out.println("Tables applied.");

        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }

    boolean verifyQuery(String str, String... reference) throws SQLException {

        boolean exist = false;

        try {
            PreparedStatement statement = con.prepareStatement(str);
            int x = 1;
            for (String str2 : reference) {
                statement.setString(x++, str2);
            }

            try (ResultSet rs = statement.executeQuery()) {
                exist = rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }

    int executeQuery(String str, String... reference) throws SQLException {
        try {
            PreparedStatement statement = con.prepareStatement(str);

            int x = 1;
            for(String str2 : reference) {
                statement.setString(x++, str2);
            }
            statement.execute();
            ResultSet rs1 = statement.getGeneratedKeys();
            if(rs1.next()){
                return rs1.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    ResultSet getQuery(String str, String... reference) throws SQLException {

        ResultSet rs = null;

        PreparedStatement statement = con.prepareStatement(str);

        int x = 1;
        for(String str2 : reference){
            statement.setString(x++, str2);
        }

        rs = statement.executeQuery();
        return rs;

    }

    public String get_SHA_512_SecurePassword(String passwordToHash){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
