package com.taviannetwork.tavianrpg.damage;

import lombok.Data;
import org.bukkit.entity.LivingEntity;

@Data
public class DamageSource {
    private final LivingEntity damager;
    private final LivingEntity damaged;

    private double currentDamage = 0;
    private boolean critical = false;
}
