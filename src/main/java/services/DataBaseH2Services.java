package services;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseH2Services {

    private final static String JDBC_DRIVER = "org.h2.Driver";
    private  final static String DB_URL = "jdbc:h2:tcp://localhost/~/practica3";
    private final static String user = "";
    private final static String password = "";
    private static Connection dbConnection = null;

    private static Server server;


    public static Connection getDataBaseConnection(){

        try {
            Class.forName(JDBC_DRIVER);
            dbConnection = DriverManager.getConnection(DB_URL, user, password);
            System.out.println("[success] Connected to the database");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("[failed] An error occurred. Database Connection Failed!");
            e.printStackTrace();
        }
        return  dbConnection;
    }

    public static void startDb() throws SQLException {
        server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers", "-ifNotExists").start();
        System.out.println("[-] START DATABASE OK!");
    }


    public static void stopDb(){
        server.shutdown();
    }

    public static void initAdminUser() throws SQLException{
        String sql = "MERGE INTO USER(USERNAME, FULLNAME, PASSWORD) VALUES ('admin','ANTHONY SAKAMOTO','admin')";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        Statement statement = conn.createStatement();
        statement.execute(sql);
        statement.close();
        conn.close();
        System.out.println("[-] INIT ADMIN USER OK!");
    }

    public static void createProductTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT\n" +
                "(\n" +
                " ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,\n" +
                " NAME VARCHAR(50) NOT NULL,\n" +
                " PRICE DOUBLE NOT NULL,\n" +
                " DESCRIP VARCHAR(100) NOT NULL\n" +
                ");";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        Statement st = conn.createStatement();
        st.execute(sql);
        st.close();
        conn.close();
        System.out.println("[-] PRODUCT TABLE OK!");
    }

    public static void createUserTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS USER\n" +
                "(\n" +
                " USERNAME VARCHAR(50) PRIMARY KEY NOT NULL,\n" +
                "FULLNAME VARCHAR(100) NOT NULL,\n" +
                "PASSWORD VARCHAR(50) NOT NULL\n" +
                ");";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        Statement st = conn.createStatement();
        st.execute(sql);
        st.close();
        conn.close();
        System.out.println("[-] USER TABLE OK!");
    }

    public static void createInvoiceTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS INVOICE\n" +
                "(\n" +
                " ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,\n" +
                "CLIENTNAME VARCHAR(100) NOT NULL,\n" +
                "CREATED_DATE DATE NOT NULL,\n" +
                "TOTAL_PRICE FLOAT NOT NULL\n" +
                ");";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        Statement st = conn.createStatement();
        st.execute(sql);
        st.close();
        conn.close();
        System.out.println("[-] INVOICE TABLE OK!");
    }

    public static void createInvoiceProductRelTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS INVOICEPRODUCT\n" +
                "(\n" +
                " ID_INVOICE INTEGER NOT NULL,\n" +
                "ID_PRODUCT INTEGER NOT NULL,\n" +
                "CANT INTEGER NOT NULL,\n" +
                "FOREIGN KEY (ID_INVOICE) REFERENCES INVOICE(ID),\n" +
                "FOREIGN KEY (ID_PRODUCT) REFERENCES PRODUCT(ID),\n" +
                "PRIMARY KEY (ID_INVOICE, ID_PRODUCT)\n" +
                ");";
        Connection conn = DataBaseH2Services.getDataBaseConnection();
        Statement st = conn.createStatement();
        st.execute(sql);
        st.close();
        conn.close();
        System.out.println("[-] INVOICEPRODUCT TABLE OK!");
    }








}
