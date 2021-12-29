package pl.pijok.autosell.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BoosterEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String nickname;

    public BoosterEndEvent(String nickname){
        this.nickname = nickname;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getNickname() {
        return nickname;
    }
}
