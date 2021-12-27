package pl.pijok.autosell;

import pl.pijok.autosell.database.DatabaseManager;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.selling.SellingController;

public class Controllers {

    private static MinersController minersController;
    private static SellingController sellingController;
    private static DatabaseManager databaseManager;

    public static void create(){

        minersController = new MinersController();
        sellingController = new SellingController();
        databaseManager = new DatabaseManager();

    }

    public static MinersController getMinersController() {
        return minersController;
    }

    public static SellingController getSellingController() {
        return sellingController;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
