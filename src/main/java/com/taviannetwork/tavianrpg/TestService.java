package com.taviannetwork.tavianrpg;

import com.taviannetwork.tavianrpg.items.ItemManager;
import com.taviannetwork.tavianrpg.items.enums.CustomItemRegistry;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;

@ServiceInfo(serviceName = "TestService", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
@Singleton
public class TestService implements Service, Listener {
    @Inject
    private ItemManager itemManager;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.getPlayer().getInventory().addItem(itemManager.createCustomItem(CustomItemRegistry.TEST_SWORD));
    }

    @Override
    public List<Class<? extends Service>> getDependencies() {
        return Collections.singletonList(ItemManager.class);
    }
}
