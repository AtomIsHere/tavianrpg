package com.taviannetwork.tavianrpg.entity.adapters;

import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EntityZombie;
import net.minecraft.server.v1_16_R1.World;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftChatMessage;

public abstract class CustomZombieAdapter<T extends CustomZombieAdapter<T>> extends EntityZombie implements CustomEntityAdapter<T> {
    protected final String baseName;
    protected final int level;

    public CustomZombieAdapter(EntityTypes<? extends EntityZombie> entitytypes, World world, String baseName, int level) {
        super(entitytypes, world);

        this.baseName = baseName;
        this.level = level;

        setCustomName(CraftChatMessage.fromStringOrNull(AdapterUtils.getDisplayName(this)));
        setCustomNameVisible(true);
    }

    @Override
    public String getBaseName() {
        return baseName;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void tick() {
        super.tick();
        setCustomName(CraftChatMessage.fromStringOrNull(AdapterUtils.getDisplayName(this)));
    }
}
