package pl.pijok.autosell;

import pl.pijok.autosell.miner.MinersController;

public class Controllers {

    private static MinersController minersController;

    public static void create(){

        minersController = new MinersController();

    }

    public static MinersController getMinersController() {
        return minersController;
    }
}
