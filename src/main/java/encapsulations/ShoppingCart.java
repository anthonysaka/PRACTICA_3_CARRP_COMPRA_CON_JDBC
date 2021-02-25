package encapsulations;

public class ShoppingCart {

    private Product product;
    private Integer cant;

    public ShoppingCart(Product product, Integer cant) {
        this.product = product;
        this.cant = cant;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCant() {
        return cant;
    }

    public void setCant(Integer cant) {
        this.cant = cant;
    }
}
