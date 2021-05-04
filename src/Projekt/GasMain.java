package Projekt;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class GasMain extends Connection {

    public static JFrame frame = new JFrame("Gas Main");

    private static int workerID;
    private static String workerFullName;

    private JPanel gasMain;
    private JPanel mainButtons;
    private JPanel mainCards;
    private JButton buttonPumps;
    private JButton buttonProducts;
    private JLabel workerName;
    private JButton logoutButton;
    private JPanel mainPumps;
    private JPanel mainProducts;
    private JList<String> listProducts;
    private JComboBox<String> activePumpComboBox;
    private JComboBox<String> productCategoriesComboBox;
    private JTextField paramenterTextField;
    private JButton addButton;
    private JList billElementsList;
    private JButton removeButton1;


    //image
    private static BufferedImage bi;

    // search products
    private static SearchProducts[] searchProducts;

    // active Bills
    private static HashMap<String,String> activeBills = new HashMap<>();

    // Bill elements
    private static String totalBill;
    private static ArrayList<BillElements> billElements = new ArrayList<>();
    private String billnr;

    public GasMain(){
        setWorkerName();
        workerName.setText(workerFullName);
        listProducts.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        billElementsList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        listProducts.setSize(100,400);
        billElementsList.setSize(30,400);
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font(Font.MONOSPACED, Font.PLAIN, 12)));

        try {
            bi = ImageIO.read(new File("pump.png"));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        buttonPumps.addActionListener(e ->{
            mainCards.removeAll();
            mainPumps.removeAll();
            getPumps();
            mainCards.add(mainPumps);
            mainCards.repaint();
            mainCards.revalidate();

        });

        buttonProducts.addActionListener(e -> {

            mainCards.removeAll();
            mainCards.add(mainProducts);
            mainCards.repaint();
            mainCards.revalidate();


            activePumpComboBox.setModel(new DefaultComboBoxModel<>(getActivePumps()));
            productCategoriesComboBox.setModel(new DefaultComboBoxModel<>(getProductsCategories()));
            listProducts.setListData(getProducts());

            if(!activePumpComboBox.getSelectedItem().equals("")){
                    billnr = activeBills.get(activePumpComboBox.getSelectedItem());
                    billElementsList.setListData(getBillElements(billnr));
            }


        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            Login.openLogin();
        });

        productCategoriesComboBox.addActionListener(e -> {
            listProducts.setListData(getProducts());
        });

        paramenterTextField.addActionListener(e -> {
            listProducts.setListData(getProducts());
        });

        addButton.addActionListener(e -> {
            addProducts();
            billnr = activeBills.get(activePumpComboBox.getSelectedItem());
            billElementsList.setListData(getBillElements(billnr));

        });

        activePumpComboBox.addActionListener(e->{
            listProducts.setListData(getProducts());
            billnr = activeBills.get(activePumpComboBox.getSelectedItem());
            billElementsList.setListData(getBillElements(billnr));
        });

        removeButton1.addActionListener(ne ->{
            removeProduct();
            String billnr = activeBills.get(activePumpComboBox.getSelectedItem());
            billElementsList.setListData(getBillElements(billnr));
        });
    }

    public static void openGasMain(int id){

        workerID = id;
        frame.setContentPane(new GasMain().gasMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void getPumps() {

        ArrayList<PumpButton> pumps = new ArrayList<>();

        try {
            String query = "SELECT gp.gas_pumpID, gp.in_use FROM GAS_PUMP AS gp";
            ResultSet rs = functions.getQuery(query);
            while (rs.next()) {
                pumps.add(new PumpButton(rs.getString(1)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }

        for (PumpButton pump : pumps) {
            pump.addActionListener(e->{
                ResultSet rs;
                try {
                    String query = "SELECT gas_pumpID, in_use FROM GAS_PUMP WHERE gas_pumpID = ?";
                    rs = functions.getQuery(query, pump.getText());
                    while (rs.next()) {
                        if (rs.getString(2).equals("0")) {

                            // start new bill

                            query = "INSERT INTO BILL(gas_pumpID,date) VALUES(?,?)";
                            String date = LocalDate.now().toString();
                            int key = functions.executeQuery(query,pump.getText(),date);

                            // get new bill id

                            activeBills.put(pump.getText(),String.valueOf(key));

                            // set gas_pump in use

                            query = "UPDATE GAS_PUMP SET in_use = 1 WHERE gas_pumpID = ?";

                            this.setForeground(Color.RED);
                            repaint();

                        } else {
                            int option = JOptionPane.showConfirmDialog(null, "Do you want to close this Pump?",
                                    "Close", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                            if(option == JOptionPane.YES_OPTION){
                                Object[] possibilities = {"Card", "Cash", "Empty"};
                                String s = (String)JOptionPane.showInputDialog(frame,
                                        getBillElements(activeBills.get(pump.getText())),
                                        "Close",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        possibilities,
                                        "Card");

                                if ((s != null) && (s.length() > 0)){
                                    query = "UPDATE BILL SET payment = ?, total_price = ? WHERE billID = ?";
                                    functions.executeQuery(query,s,totalBill,activeBills.get(pump.getText()));

                                    for (BillElements elements : billElements){
                                        String amount;
                                        int newamount = 0;
                                        query = "SELECT amount FROM PRODUCT WHERE productID = ?";
                                        rs = functions.getQuery(query,elements.productID);
                                        while (rs.next()){
                                            amount = rs.getString(1);
                                            newamount = Integer.parseInt(amount) - Integer.parseInt(elements.amount);
                                            query = "UPDATE PRODUCT SET amount = ? WHERE productID = ?";
                                            functions.executeQuery(query,String.valueOf(newamount),elements.productID);
                                        }
                                    }

                                    query = "UPDATE GAS_PUMP SET in_use = 0 WHERE gas_pumpID = ?";
                                    activeBills.remove(pump.getText());
                                    this.setForeground(Color.GREEN);
                                    repaint();
                                }
                            }
                        }
                        functions.executeQuery(query, pump.getText());
                    }
                } catch (SQLException er) {
                    JOptionPane.showMessageDialog(null, er.getMessage());
                }
            });
            mainPumps.add(pump);
        }
    }

    public String[] getActivePumps(){

        ArrayList<String> activePumps = new ArrayList<>();

        try {
            String query = "SELECT gas_pumpID FROM GAS_PUMP WHERE in_use = ?";

            ResultSet rs = functions.getQuery(query,"1");
            while (rs.next()){
                activePumps.add(rs.getString(1));
            }

        }catch (SQLException e){
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }

        return activePumps.toArray(new String[0]);
    }

    public String[] getProductsCategories(){

        ArrayList<String> categories = new ArrayList<>();

        try {
            String query = "SELECT name FROM PRODUCT_CATEGORY";
            ResultSet rs = functions.getQuery(query);
            while (rs.next()){
                categories.add(rs.getString(1));
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(frame,e.getMessage() );
        }

        return categories.toArray(new String[0]);
    }

    public String[] getProducts(){

        ArrayList<String> product = new ArrayList<>();
        ArrayList<SearchProducts> products = new ArrayList<>();

        try {
            String query;
            ResultSet rs;

            if (productCategoriesComboBox.getSelectedItem().equals("Gas")){
                query = "SELECT * FROM PRODUCT " +
                        "WHERE productID = (SELECT productID FROM GAS_TANK " +
                        "WHERE gas_tankID = (SELECT gas_tank FROM GAS_PUMP WHERE gas_pumpID = ?))";
                rs = functions.getQuery(query,activePumpComboBox.getSelectedItem().toString());

            }else{
                if (paramenterTextField.getText().equals("")) {
                    query = "SELECT * FROM PRODUCT AS p " +
                            "WHERE p.product_categoryID = (SELECT product_categoryID FROM PRODUCT_CATEGORY WHERE name = ?)  ORDER BY p.name";
                    rs = functions.getQuery(query, productCategoriesComboBox.getSelectedItem().toString());
                }
                else {
                    query = "SELECT * FROM PRODUCT AS p " +
                            "WHERE p.product_categoryID = (SELECT product_categoryID FROM PRODUCT_CATEGORY WHERE name = ?) AND p.name LIKE ? ORDER BY p.name";
                    rs = functions.getQuery(query, productCategoriesComboBox.getSelectedItem().toString(),
                            "%"+paramenterTextField.getText()+"%");
                }
            }

            while (rs.next()){

                String name = rs.getString(2);
                String tax = " tax: %" + rs.getString(5);
                String price = "EU:" + rs.getString(4);

                products.add(new SearchProducts(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5)));

                product.add(String.format("%-30s%-15s%-15s", name, tax, price));
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(frame,e.getMessage() );
        }
        searchProducts = products.toArray(new SearchProducts[0]);
        return product.toArray(new String[0]);
    }

    public String[] getBillElements(String billnr){
        ArrayList<String> elements = new ArrayList<>();
        String query;
        ResultSet rs;
        int total = 0;

        elements.add("Bill nr. " + billnr + "\nGasStation Macron\nEmployee:"+ workerID +"\n");

        try {
            query = "SELECT p.name, bl.amount, bl.price, p.tax FROM BILL_ELEMENT AS bl, PRODUCT AS p WHERE bl.billID = ? AND bl.productID = p.productID";
            rs = functions.getQuery(query,billnr);
            while(rs.next()){
                String name = rs.getString(1);
                String amount = "x:" + rs.getString(2);
                String tax = "tax:" + rs.getString(4);
                String price = "EU:" + rs.getString(3);
                elements.add(String.format("%-25s%-7s%-7s%-7s", name, amount,tax, price));
                total += Integer.parseInt(rs.getString(3));
            }
            query = "SELECT * FROM BILL_ELEMENT WHERE billID = ?";
            rs = functions.getQuery(query,billnr);

            while (rs.next()){
                String productID = rs.getString(2);
                String amount = rs.getString(3);
                String price = rs.getString(4);
                String elementID = rs.getString(5);

                billElements.add(new BillElements(billnr,productID,amount,price,elementID));
            }

        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }

        totalBill = ""+total;
        elements.add(String.format("%-35s%-10s", "\nTOTAL", "EU:"+totalBill));

        return elements.toArray(new String[0]);
    }

    public void addProducts(){
        int selection = listProducts.getSelectedIndex();
        SearchProducts selected = searchProducts[selection];
        String amount;
        String query;
        ResultSet rs;

        do {
            amount = JOptionPane.showInputDialog(frame,"How many - " + selected.name +
                    " - do you want to add?","1");
        } while(amount.equals("") || !functions.isNumeric(amount));

        int totalPrice = Integer.parseInt(amount) * Integer.parseInt(selected.price);

        try {
            query = "SELECT amount FROM PRODUCT WHERE productID = ?";
            rs = functions.getQuery(query,selected.productID);
            while (rs.next()){
                if(Integer.parseInt(rs.getString(1)) > Integer.parseInt(amount)){
                    query = "INSERT INTO BILL_ELEMENT(billID,productID,amount,price) VALUES(?,?,?,?)";
                    functions.executeQuery(query,activeBills.get(activePumpComboBox.getSelectedItem()),
                            selected.productID,amount,Integer.toString(totalPrice));
                }
                else {
                    JOptionPane.showMessageDialog(null,"There is not enough product in stock.\n " +
                            "The current stock is "+ rs.getString(1) + " unites",null,JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    };

    private void removeProduct(){
        int selection = billElementsList.getAnchorSelectionIndex();
        System.out.println(selection);
        int count = 1;
        String query;
        ResultSet rs;
        try {

            query = "SELECT * FROM BILL_ELEMENT WHERE billID = ?";
            rs = functions.getQuery(query,activeBills.get(activePumpComboBox.getSelectedItem()));
            while (rs.next()){
                if (count == selection){
                    String bill_elementId = rs.getString(5);
                    query = " DELETE FROM BILL_ELEMENT WHERE bill_elementID = ?";
                    functions.executeQuery(query,bill_elementId);
                }
                count++;
            }

        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static class PumpButton extends JButton {

        public PumpButton(String pumpnr){
            super(pumpnr);
            try {
                String query = "SELECT gas_pumpID, in_use FROM GAS_PUMP WHERE gas_pumpID = ?";
                ResultSet rs = functions.getQuery(query, pumpnr);
                while (rs.next()) {
                    if (rs.getString(2).equals("0")) {
                        this.setForeground(Color.BLUE);
                    } else {
                        this.setForeground(Color.RED);
                    }
                }
            } catch (SQLException er) {
                JOptionPane.showMessageDialog(null, er.getMessage());
            }

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e){
                    repaint();
                }
            });
        }

        public void paint(Graphics g){
            super.paint(g);
            g.drawImage(bi,16,2,22,22,null );
            this.setSize(48,30);
            try {
                String query = "SELECT in_use FROM GAS_PUMP WHERE gas_pumpID = ?";
                ResultSet rs = functions.getQuery(query, this.getText());
                while (rs.next()) {
                    if(rs.getString(1).equals("1")){
                        this.setForeground(Color.RED);
                    }
                    else {
                        this.setForeground(Color.BLUE);
                    }
                }

            } catch (SQLException er) {
                JOptionPane.showMessageDialog(null, er.getMessage());
            }





        }

    }

    public static class SearchProducts {

        public String productID;
        public String name;
        public String product_categoryID;
        public String price;
        public String tax;

        public SearchProducts(String productID, String name, String product_categoryID, String price, String tax) {
            this.productID = productID;
            this.name = name;
            this.product_categoryID = product_categoryID;
            this.price = price;
            this.tax = tax;
        }
    }

    public static class BillElements {

        public String elementID;
        public String productID;
        public String amount;
        public String price;
        public String billID;

        public BillElements(String elementID, String productID, String amount, String price, String billID) {
            this.elementID = elementID;
            this.productID = productID;
            this.amount = amount;
            this.price = price;
            this.billID = billID;
        }
    }

    public void setWorkerName() {
        try {
            String query = "SELECT firstname, lastname FROM WORKER WHERE workerID = ?";
            ResultSet rs = functions.getQuery(query,String.valueOf(workerID));
            while (rs.next()) {
                workerFullName = (rs.getString(1) + ' ' + rs.getString(2));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
