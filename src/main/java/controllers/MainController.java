package controllers;

import encapsulations.InvoiceProduct;
import encapsulations.Product;
import encapsulations.ShoppingCart;
import encapsulations.User;
import io.javalin.Javalin;
import org.jasypt.util.text.StrongTextEncryptor;
import services.DataBaseH2Services;

import javax.servlet.http.Cookie;
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
    }


    public void routesControl(){
        app.get("/", ctx -> ctx.redirect("/products"));

        app.routes(() -> {

            before( ctx -> {
                    if (ctx.cookie("user_remember") != null){
                        StrongTextEncryptor stE = new StrongTextEncryptor();
                        stE.setPassword("myEncryptionPassword");
                        ctx.sessionAttribute("user",stE.decrypt(ctx.cookie("user_remember")));
                    }
                }

            );


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
                    String auxUser = ctx.sessionAttribute("user");
                    if (auxUser == null){
                         tempURI = ctx.req.getRequestURI();
                        ctx.redirect("/login.html");
                    }
                });

                get("/admin", ctx -> {
                    List<Product> listProduct = storeController.getListProduct();
                    Map<String, Object> model = new HashMap<>();
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
                    String auxUser = ctx.sessionAttribute("user");
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
                    storeController.getListProduct().clear();
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
                   storeController.getListProduct().clear();
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
                    storeController.getListProduct().clear();
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
                    Date date1 = new Date();
                    java.sql.Date date = new java.sql.Date(date1.getTime());
                    int lastid = storeController.searchIdLastInvoice();
                    System.out.println(lastid);

                    InvoiceProduct invp = new InvoiceProduct(name,date);
                    storeController.addProductToInvoice((lastid+1),listProductCart);
                    float pr = 0.0f;
                    for (ShoppingCart p : listProductCart) {
                        pr += p.getProduct().getPrice()*p.getCant();
                    }
                    System.out.println(pr);
                    invp.setTotalPrice(pr);
                    storeController.addInvoice(invp);
                    storeController.getListSaleProduct().clear();
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
                        ctx.attribute("userFound", auxUser.getUsername());

                        if (ctx.formParam("chkRemember") != null){
                            StrongTextEncryptor stE = new StrongTextEncryptor();
                            stE.setPassword("myEncryptionPassword");
                            String userEncryp = stE.encrypt(auxUser.getUsername());
                            ctx.cookie("user_remember", userEncryp,604800);
                        }
                    }
                });

                post("/", ctx -> {
                    ctx.sessionAttribute("user",ctx.attribute("userFound"));
                    ctx.redirect(tempURI);
                });

            });

            path("/logout", () -> {
                get("/",ctx -> {
                    if (ctx.sessionAttribute("user") != null){
                        ctx.sessionAttribute("user",null);
                        ctx.req.getSession().invalidate();
                    }
                    if (ctx.cookie("user_remember") != null){
                        ctx.removeCookie("user_remember");
                    }
                    ctx.redirect("/");
                });
            });

            path("/products/admin/addproduct", () -> {

                before("/", ctx -> {
                    String auxUser = ctx.sessionAttribute("user");
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
