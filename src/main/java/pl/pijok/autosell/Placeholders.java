package pl.pijok.autosell;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.pijok.autosell.miner.MinersController;

public class Placeholders extends PlaceholderExpansion {

    private final MinersController minersController = Controllers.getMinersController();

    @Override
    public @NotNull String getIdentifier() {
        return "autosell";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Pijok";
    }

    @Override
    public @NotNull String getVersion() {
        return AutoSell.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        if(params.equalsIgnoreCase("blocksMined")){
            return String.valueOf(minersController.getMiner(player.getName()).getMinedBlocks());
        }

        return null;
    }
}
