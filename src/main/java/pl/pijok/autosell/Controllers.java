package pl.pijok.autosell;

import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.selling.SellingController;

public class Controllers {

    private static MinersController minersController;
    private static SellingController sellingController;

    public static void create(){

        minersController = new MinersController();
        sellingController = new SellingController();

    }

    public static MinersController getMinersController() {
        return minersController;
    }

    public static SellingController getSellingController() {
        return sellingController;
    }
}
