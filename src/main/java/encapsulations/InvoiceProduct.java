package encapsulations;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class InvoiceProduct {
    private Integer id;
    private String clientName;
    private ArrayList<ShoppingCart> listProduct = new ArrayList<ShoppingCart>();
    private Date date;
    private Float totalPrice;

    /* Constructor */

    public InvoiceProduct(String clientName,  Date date) {
        this.clientName = clientName;
        this.date = date;
    }

    /* Gets and Sets */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<ShoppingCart> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<ShoppingCart> listProduct) {
        this.listProduct = listProduct;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    /* Methods */

    public void addProduct(List<ShoppingCart> p) {
        this.listProduct = (ArrayList<ShoppingCart>) p;
    }


}

