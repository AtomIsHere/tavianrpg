package com.taviannetwork.tavianrpg.entity;

import com.taviannetwork.tavianrpg.entity.adapters.CustomZombieAdapter;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EntityZombie;
import net.minecraft.server.v1_16_R1.World;

public class DummyZombie extends CustomZombieAdapter<DummyZombie> {
    public DummyZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world, "Dummy Zombie", 1);
    }

    @Override
    public CustomEntityType<DummyZombie> getCustomType() {
        return null;
    }

    @Override
    public DummyZombie get() {
        return this;
    }


}
