package controllers;

import encapsulations.InvoiceProduct;
import encapsulations.Product;
import encapsulations.ShoppingCart;
import encapsulations.User;
import io.javalin.Javalin;
import services.DataBaseH2Services;

import java.sql.SQLException;
import java.util.*;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MainController {
    Javalin app;
    StoreController storeController = StoreController.getInstance();
    static String tempURI = "";
    static int idInvoice = 0;

    public MainController(Javalin app) throws SQLException {
        this.app = app;
        /*User n = new User("anthony sakamoto","admin","admin");
        storeController.addUser(n);
        Product pNew = new Product(1,"LAPTOP HP", 40500.0,"THE BEST IN THE MARKET");
        Product pNew1 = new Product(2,"IPHONE 8", 15000.0,"LIKE NEW");
        Product pNew2 = new Product(3,"RASPBERRY PI 4", 5500.0,"KIT FULL");
        storeController.addProduct(pNew);
        storeController.addProduct(pNew1);
        storeController.addProduct(pNew2);*/

    }


    public void routesControl(){
        app.get("/", ctx -> ctx.redirect("/products"));

        app.routes(() -> {

            path("/products", () -> {

                get("/", ctx -> {
                    List<Product> listProduct = storeController.getListProduct();
                    Map<String, Object> model = new HashMap<>();
                    model.put("listProduct",listProduct);

                    if (ctx.sessionAttribute("cart") == null){
                        model.put("itemCant",0);
                    } else {
                        int x = 0;
                        List<ShoppingCart> pl = ctx.sessionAttribute("cart");
                        for (ShoppingCart p: pl) {
                            x += p.getCant();
                        }

                        model.put("itemCant",x);
                    }
                    ctx.render("/templates/products.html",model);
                });

                before("/admin", ctx -> {
                    User auxUser = ctx.sessionAttribute("user");
                    if (auxUser == null){
                         tempURI = ctx.req.getRequestURI();
                        ctx.redirect("/login.html");
                    }
                });

                get("/admin", ctx -> {
                    List<Product> listProduct = storeController.getListProduct();
                    Map<String, Object> model = new HashMap<>();
                    User adminUser = ctx.sessionAttribute("user");

                    model.put("adminUser", adminUser);
                    model.put("listProduct", listProduct);

                    if (ctx.sessionAttribute("cart") == null) {
                        model.put("itemCant", 0);
                    } else {
                        int x = 0;
                        List<ShoppingCart> pl = ctx.sessionAttribute("cart");
                        for (ShoppingCart p : pl) {
                            x += p.getCant();
                        }

                        model.put("itemCant", x);
                    }
                    ctx.render("/templates/adminProducts.html",model);
                });

                before("/saleshistory", ctx -> {
                    User auxUser = ctx.sessionAttribute("user");
                    if (auxUser == null){
                        tempURI = ctx.req.getRequestURI();
                        ctx.redirect("/login.html");
                    }
                });

                get("/saleshistory", ctx -> {
                    List<InvoiceProduct> invp = storeController.getListSaleProduct();
                    Map<String, Object> model = new HashMap<>();
                    model.put("listInvoice", invp);

                    if (ctx.sessionAttribute("cart") == null){
                        model.put("itemCant",0);
                    } else {
                        int x = 0;
                        List<ShoppingCart> pl = ctx.sessionAttribute("cart");
                        for (ShoppingCart p: pl) {
                            x += p.getCant();
                        }

                        model.put("itemCant",x);
                    }
                    ctx.render("/templates/historialProduct.html",model);
                });

            });

            path("/products/admin", () -> {

                get("/delete/:id", ctx -> {
                    Integer id = ctx.pathParam("id",Integer.class).get();
                    storeController.deleteProduct(id);
                    storeController.loadProduct();
                    ctx.redirect("/products/admin");

                });

                get("/edit/:id", ctx -> {
                    Integer id = ctx.pathParam("id",Integer.class).get();
                    Product auxProd = storeController.searchProduct(id);
                    Map<String, Object> model = new HashMap<>();
                    model.put("auxProd",auxProd);

                    ctx.render("/templates/editProduct.html",model);

                });

                post("/edit", ctx -> {
                   Integer id = ctx.formParam("idProduct", Integer.class).get();
                   String name = ctx.formParam("nameProduct");
                   Double price = ctx.formParam("priceProduct",Double.class).get();
                   String descrip = ctx.formParam("descriptionProduct");

                   storeController.editProduct(id,name,price,descrip);
                   storeController.loadProduct();

                    ctx.redirect("/products/admin");

                });

                post("/add", ctx -> {
                    Integer id = ctx.formParam("idProduct", Integer.class).get();
                    String name = ctx.formParam("nameProduct");
                    Double price = ctx.formParam("priceProduct",Double.class).get();
                    String descrip = ctx.formParam("descriptionProduct");

                    Product p = new Product(id,name,price,descrip);
                    storeController.addProduct(p); //save to db
                    storeController.loadProduct(); // load from db

                    ctx.redirect("/products/admin");
                });


            });


            path("/addtocart", () -> {

                post("/", ctx -> {
                    Integer id = ctx.formParam("id", Integer.class).get();
                    Integer cant = ctx.formParam("cant", Integer.class).get();
                    Product auxProd = storeController.searchProduct(id);

                    if (ctx.sessionAttribute("cart") == null){
                        ArrayList<ShoppingCart> p = new ArrayList<ShoppingCart>();
                        p.add(new ShoppingCart(auxProd,cant));
                        ctx.sessionAttribute("cart",p);
                    } else {
                        ArrayList<ShoppingCart> p = ctx.sessionAttribute("cart");
                        p.add(new ShoppingCart(auxProd,cant));
                        ctx.sessionAttribute("cart",p);
                    }
                    ctx.redirect("/products");
                });

            });

            path("/cart", () -> {

                get("/", ctx -> {
                    List<ShoppingCart> listProductCart = ctx.sessionAttribute("cart");
                    Map<String, Object> model = new HashMap<>();
                    model.put("listProductCart",listProductCart);

                    if (ctx.sessionAttribute("cart") == null){
                        model.put("itemCant",0);
                    } else {
                        int x = 0;
                        List<ShoppingCart> pl = ctx.sessionAttribute("cart");
                        for (ShoppingCart p: pl) {
                            x += p.getCant();
                        }

                        model.put("itemCant",x);
                    }
                    Double total = 0.0;
                    if (listProductCart !=null){
                        for (ShoppingCart p: listProductCart) {
                            total += (p.getProduct().getPrice()*p.getCant());
                        }
                        model.put("total",total);
                    }


                    ctx.render("/templates/shoppingCart.html",model);
                });

                post("/delete",ctx -> {
                    Integer id = ctx.formParam("id",Integer.class).get();
                    Integer cant = ctx.formParam("cant",Integer.class).get();
                    List<ShoppingCart> listProductCart = ctx.sessionAttribute("cart");

                    for (int x = 0;x<listProductCart.size();x++){
                        if (listProductCart.get(x).getCant().equals(cant) && listProductCart.get(x).getProduct().getId().equals(id)){
                            listProductCart.remove(x);
                            break;
                        }
                    }

                    ctx.redirect("/cart");
                });

                post("/pay", ctx -> {
                    String name = ctx.formParam("name");
                    List<ShoppingCart> listProductCart = ctx.sessionAttribute("cart");
                    Date date = new Date();
                    int lastid = storeController.searchIdLastInvoice();

                    InvoiceProduct invp = new InvoiceProduct(name,date);
                    float pr = invp.totalPrice();
                    invp.setTotalPrice(pr);

                    storeController.addInvoice(invp);
                    storeController.addProductToInvoice(lastid,listProductCart);
                    storeController.loadSalesHistory();

                    ctx.sessionAttribute("cart",null);

                    ctx.redirect("/products");
                });

            });


            path("/authenticate", () -> {

                before("/", ctx -> {
                    String username = ctx.formParam("username");
                    String password = ctx.formParam("password");
                    User auxUser = storeController.searchUser(username,password);
                    if (auxUser == null){
                        ctx.redirect("/401.html");
                    }else{
                        ctx.attribute("userFound", auxUser);
                    }
                });

                post("/", ctx -> {
                    ctx.sessionAttribute("user",ctx.attribute("userFound"));
                    ctx.redirect(tempURI);
                });

            });


            path("/products/admin/addproduct", () -> {

                before("/", ctx -> {
                    User auxUser = ctx.sessionAttribute("user");
                    if (auxUser == null){
                        tempURI = ctx.req.getRequestURI();
                        ctx.redirect("/login.html");
                    }
                });

                get("/", ctx -> {
                    ctx.render("/templates/addProduct.html");
                });

            });


        });



    }
}
