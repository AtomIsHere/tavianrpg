package com.taviannetwork.tavianrpg.items;

import com.taviannetwork.tavianrpg.items.datatypes.EnchantmentMapDataType;
import com.taviannetwork.tavianrpg.items.enums.CustomEnchant;

import java.io.Serializable;
import java.util.HashMap;

//Placeholder class for persistent data types
public class EnchantmentMap extends HashMap<CustomEnchant, Integer> implements Serializable {
    private static final long serialVersionUID = 7480451393546081850L;

    public static final EnchantmentMapDataType DATA_TYPE = new EnchantmentMapDataType();
}
