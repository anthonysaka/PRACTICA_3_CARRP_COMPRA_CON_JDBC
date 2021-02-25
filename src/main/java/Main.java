import controllers.MainController;
import controllers.StoreController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import services.DataBaseH2Services;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        Javalin app = Javalin.create( config -> {
            //set configs
            config.addStaticFiles("/public");

        }).start(7000);

        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");

        DataBaseH2Services.startDb();

        // Creating DB Tables and Loading Data Init

        DataBaseH2Services.createUserTable();
        DataBaseH2Services.createProductTable();
        DataBaseH2Services.createInvoiceTable();
        DataBaseH2Services.createInvoiceProductRelTable();

        DataBaseH2Services.initAdminUser();

        StoreController.getInstance().loadUser();
        StoreController.getInstance().loadProduct();
        StoreController.getInstance().loadSalesHistory();


        new MainController(app).routesControl();



    }
}
