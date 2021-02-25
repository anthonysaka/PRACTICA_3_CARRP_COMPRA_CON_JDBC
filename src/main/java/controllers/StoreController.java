package controllers;

import encapsulations.InvoiceProduct;
import encapsulations.Product;
import encapsulations.ShoppingCart;
import encapsulations.User;
import services.DataBaseH2Services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreController {

    private ArrayList<Product> listProduct;
    private ArrayList<User> listUser;
    private ArrayList<InvoiceProduct> listSaleProduct;

    private static StoreController storeController;


    /* SINGLETON */
    public static StoreController getInstance() {
        if(storeController == null) {
            storeController = new StoreController();
        }
        return storeController;
    }

    private StoreController() {
        this.listProduct =  new ArrayList<Product>();
        this.listUser = new ArrayList<User>();
        this.listSaleProduct = new ArrayList<InvoiceProduct>();
    }

    /* Gets and Sets */

    public ArrayList<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    public ArrayList<User> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<User> listUser) {
        this.listUser = listUser;
    }

    public ArrayList<InvoiceProduct> getListSaleProduct() {
        return listSaleProduct;
    }

    public void setListSaleProduct(ArrayList<InvoiceProduct> listSaleProduct) {
        this.listSaleProduct = listSaleProduct;
    }

    public static StoreController getStoreController() {
        return storeController;
    }

    public static void setStoreController(StoreController storeController) {
        StoreController.storeController = storeController;
    }

    public void addProduct(Product p) throws SQLException {
        String sqlQuery = "INSERT INTO PRODUCT(NAME, PRICE, DESCRIP) values(?,?,?);";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        st.setString(1,p.getName());
        st.setDouble(2, p.getPrice());
        st.setString(3, p.getDescription());

        st.execute();

        st.close();
        conn.close();

    }

    public User searchUser(String username, String password) {
        User auxUser = null;

        for (User user: listUser) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equalsIgnoreCase(password)){
                auxUser = user;
                break;
            }
        }
        return  auxUser;
    }

    public Product searchProduct(Integer id) {
        Product auxProd = null;

        for (Product p: listProduct) {
            if (p.getId().equals(id)){
               auxProd = p;
                break;
            }
        }
        return  auxProd;
    }

    public void editProduct(Integer id, String name, Double price, String description) throws SQLException {
        Product p = searchProduct(id);
        String sqlQuery = "UPDATE PRODUCT SET NAME=?, PRICE=?, DESCRIP=? WHERE ID=?;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        st.setString(1,p.getName());
        st.setDouble(2, p.getPrice());
        st.setString(3, p.getDescription());
        st.setInt(4,p.getId());

        st.executeUpdate();

        st.close();
        conn.close();
    }

    public void addUser(User u){
        listUser.add(u);
    }

    public void addInvoice(InvoiceProduct i) throws SQLException {
        String sqlQuery = "INSERT INTO INVOICE(CLIENTNAME, CREATE_DATE, TOTAL_PRICE) values(?,?,?);";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        st.setString(1,i.getClientName());
        st.setDate(2, (Date) i.getDate());
        st.setDouble(3, i.getTotalPrice());

        st.execute();

        st.close();
        conn.close();
    }

    public void addProductToInvoice(int idInvoice, List<ShoppingCart> p) throws SQLException {
        String sqlQuery = "INSERT INTO INVOICEPRODUCT(ID_INVOICE, ID_PRODUCT, CANT) values(?,?,?);";
        Connection conn = DataBaseH2Services.getDataBaseConnection();

        for (ShoppingCart aux: p) {
            PreparedStatement st = conn.prepareStatement(sqlQuery);
            st.setInt(1, idInvoice);
            st.setInt(2, aux.getProduct().getId());
            st.setInt(3, aux.getCant());

            st.execute();
        }

        conn.close();
    }

    public Integer searchIdLastInvoice() throws SQLException {
        String sqlQuery = "SELECT ID FROM INVOICE ORDER BY ID DESC LIMIT 1;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        ResultSet rs = st.executeQuery();
        int id = 0;

        while (rs.next()){
            id = rs.getInt("ID");
        }
        conn.close();

        return id;
    }

    public void deleteProduct(Integer id) throws SQLException {
        String sqlQuery = "DELETE FROM PRODUCT WHERE ID=?;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        st.setInt(1,id);

        st.execute();

        st.close();
        conn.close();

    }

    public void loadProduct() throws SQLException {
        ArrayList<Product> auxListProduct = new ArrayList<>();
        String sqlQuery = "SELECT * FROM PRODUCT;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        ResultSet rs = st.executeQuery();

        while (rs.next()){
            Integer id = rs.getInt("ID");
            String name = rs.getString("NAME");
            Double price = rs.getDouble("PRICE");
            String descrip = rs.getString("DESCRIP");

            Product auxProduct = new Product(id,name,price,descrip);
            this.listProduct.add(auxProduct);
        }

        st.close();
        conn.close();
    }

    public void loadUser() throws SQLException {
        ArrayList<User> auxListuser = new ArrayList<>();
        String sqlQuery = "SELECT * FROM USER;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        ResultSet rs = st.executeQuery();

        while (rs.next()){
            String fullname = rs.getString("FULLNAME");
            String username = rs.getString("USERNAME");
            String password = rs.getString("PASSWORD");

            User auxUser = new User(fullname,username,password);
            this.listUser.add(auxUser);
        }

        st.close();
        conn.close();
    }

    public void loadSalesHistory() throws SQLException {
        ArrayList<ShoppingCart> auxList = new ArrayList<>();

        String sqlQuery = "SELECT * FROM INVOICE;";
        String sqlQuery1 = "SELECT * FROM INVOICEPRODUCT WHERE ID_INVOICE = ?;";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        PreparedStatement st = conn.prepareStatement(sqlQuery);
        ResultSet rs = st.executeQuery();

        while (rs.next()){
            int id = rs.getInt("ID");
            String clname = rs.getString("CLIENTNAME");
            Date idate = rs.getDate("CREATED_DATE");
            Float itotalp = rs.getFloat("TOTAL_PRICE");

            InvoiceProduct invp = new InvoiceProduct(clname,idate);
            invp.setId(id);
            invp.setTotalPrice(itotalp);

            PreparedStatement st1 = conn.prepareStatement(sqlQuery1);
            st1.setInt(1,invp.getId());
            ResultSet rs1 = st1.executeQuery();

            while (rs1.next()) {
                ShoppingCart p = new ShoppingCart( searchProduct(rs1.getInt("ID_PRODUCT")),rs1.getInt("CANT"));
                auxList.add(p);
            }

            invp.addProduct(auxList);
            this.listSaleProduct.add(invp);
            st1.close();
        }
        st.close();
        conn.close();
    }






}

