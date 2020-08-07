package com.taviannetwork.tavianrpg.entity.adapters;

import com.taviannetwork.tavianrpg.entity.CustomEntityType;
import net.minecraft.server.v1_16_R1.EntityLiving;

public interface CustomEntityAdapter<T extends EntityLiving> {
    String getBaseName();
    CustomEntityType<T> getCustomType();
    int getLevel(); // Will be primarily used to calculate xp once skills are implemented.
    T get();
}
