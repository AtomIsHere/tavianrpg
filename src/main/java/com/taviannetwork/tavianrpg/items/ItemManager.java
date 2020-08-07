package com.taviannetwork.tavianrpg.items;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.attributes.CustomBukkitAttribute;
import com.taviannetwork.tavianrpg.items.enums.CustomEnchant;
import com.taviannetwork.tavianrpg.items.enums.CustomItemRegistry;
import com.taviannetwork.tavianrpg.items.enums.ItemRarity;
import com.taviannetwork.tavianrpg.items.enums.ItemReforge;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;


@Singleton
@ServiceInfo(serviceName = "ItemManager", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
public class ItemManager implements Service {
    private final Map<Attribute, String> displayNames = new HashMap<>();
    private final Set<Attribute> ignoredAttributes = new HashSet<>();

    @Inject
    private TavianRPG plugin;

    private NamespacedKey itemKey;
    private NamespacedKey enchantmentMapKey;
    private NamespacedKey reforgeKey;

    @Override
    public void start() {
        displayNames.put(Attribute.GENERIC_MAX_HEALTH, "Health");
        displayNames.put(Attribute.GENERIC_MOVEMENT_SPEED, "Speed");
        displayNames.put(CustomBukkitAttribute.GENERIC_RPG_DAMAGE.getBukkitAttribute(), "Damage");
        displayNames.put(CustomBukkitAttribute.GENERIC_RPG_STRENGTH.getBukkitAttribute(), "Strength");
        displayNames.put(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_DAMAGE.getBukkitAttribute(), "Critical Damage");
        displayNames.put(CustomBukkitAttribute.GENERIC_RPG_CRITICAL_CHANCE.getBukkitAttribute(), "Critical Chance");
        displayNames.put(CustomBukkitAttribute.GENERIC_RPG_DEFENCE.getBukkitAttribute(), "Defence");

        ignoredAttributes.add(Attribute.GENERIC_ATTACK_DAMAGE);
        ignoredAttributes.add(Attribute.GENERIC_ATTACK_SPEED);

        itemKey = new NamespacedKey(plugin, "item");
        enchantmentMapKey = new NamespacedKey(plugin, "enchantMap");
        reforgeKey = new NamespacedKey(plugin, "reforge");
    }

    @Override
    public void stop() {
        displayNames.clear();
        ignoredAttributes.clear();
    }

    public ItemStack createCustomItem(CustomItemRegistry itemType) {
        ItemStack stack = new ItemStack(itemType.getBase());
        stack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(itemKey, CustomItemRegistry.DATA_TYPE, itemType);

        Arrays.stream(itemType.getAttributes()).forEach(ac -> meta.addAttributeModifier(ac.getAttribute(), new AttributeModifier("base", ac.getModifier(), AttributeModifier.Operation.ADD_NUMBER)));

        meta.setUnbreakable(true);
        stack.setItemMeta(meta);

        meta.setLore(buildLore(stack));
        stack.setItemMeta(meta);

        return stack;
    }

    public void addEnchant(CustomEnchant enchant, int level, ItemStack item) {
        EnchantmentMap enchantmentMap = getEnchantMap(item);
        if(enchantmentMap.containsKey(enchant)) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        enchantmentMap.put(enchant, level);
        Arrays.stream(enchant.getAttributes())
                .forEach(eac -> meta.addAttributeModifier(eac.getAttribute(), new AttributeModifier(
                    "item_enchant_"
                            + enchant.name().toLowerCase()
                            + "_"
                            + eac.getAttribute().getKey().getKey(),
                    eac.getModifier().apply(level),
                    AttributeModifier.Operation.ADD_NUMBER)));

        meta.getPersistentDataContainer().set(enchantmentMapKey, EnchantmentMap.DATA_TYPE, enchantmentMap);
    }

    public CustomItemRegistry getCustomItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        CustomItemRegistry customItem = dataContainer.get(itemKey, CustomItemRegistry.DATA_TYPE);
        if(customItem == null) {
            customItem = CustomItemRegistry.VANILLA_ITEM; // Default to vanilla item.
            dataContainer.set(itemKey, CustomItemRegistry.DATA_TYPE, customItem);
            item.setItemMeta(meta);
        }

        return customItem;
    }

    public EnchantmentMap getEnchantMap(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        EnchantmentMap enchantmentMap = dataContainer.get(enchantmentMapKey, EnchantmentMap.DATA_TYPE);
        if(enchantmentMap == null) {
            enchantmentMap = new EnchantmentMap();
            dataContainer.set(enchantmentMapKey, EnchantmentMap.DATA_TYPE, enchantmentMap);
            item.setItemMeta(meta);
        }

        return enchantmentMap;
    }

    public ItemReforge getReforge(ItemStack item) {
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();

        return dataContainer.get(reforgeKey, ItemReforge.DATA_TYPE);
    }

    public void setReforge(ItemReforge reforge, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta.getAttributeModifiers() != null) {
            meta.getAttributeModifiers().entries().stream().filter(e -> e.getValue().getName().startsWith("item_reforge")).forEach(e -> meta.removeAttributeModifier(e.getKey(), e.getValue()));
        }

        ItemRarity rarity = getCustomItem(item).getRarity();

        Arrays.stream(reforge.getAttributes())
                .forEach(ac -> meta.addAttributeModifier(ac.getAttribute(),
                        new AttributeModifier("item_reforge_"
                            + reforge.name().toLowerCase() + "_"
                            + ac.getAttribute().getKey().getKey(),
                        ac.getModifier() * rarity.getReforgeMultiplier(),
                        AttributeModifier.Operation.ADD_NUMBER)));

        meta.getPersistentDataContainer().set(reforgeKey, ItemReforge.DATA_TYPE, reforge);

        item.setItemMeta(meta);
    }

    private List<String> buildLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();

        if(meta.getAttributeModifiers() != null) {
            for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : meta.getAttributeModifiers().asMap().entrySet()) {
                if(ignoredAttributes.contains(entry.getKey())) {
                    continue;
                }

                AttributeModifier base = entry.getValue()
                        .stream()
                        .filter(am -> am.getName().equals("base"))
                        .findFirst()
                        .orElse(null);

                AtomicDouble total = new AtomicDouble();
                AtomicDouble addition = new AtomicDouble();

                entry.getValue()
                        .stream()
                        .filter(am -> !am.getName().equals("base"))
                        .forEach(am -> addition.addAndGet(am.getAmount()));

                if(base != null) {
                    total.set(base.getAmount());
                }

                total.addAndGet(addition.get());

                String display = displayNames.get(entry.getKey());
                if(display == null) {
                    display = entry.getKey().getKey().getKey();
                }

                lore.add(ChatColor.GRAY + display + ": " + Math.round(total.get()) + " (+" + Math.round(addition.get()) + ")");
            }

            lore.add(" ");
        }

        EnchantmentMap enchantmentMap = getEnchantMap(item);
        if(!enchantmentMap.isEmpty()) {
            StringBuilder enchantments = new StringBuilder(ChatColor.BLUE.toString());

            for(Map.Entry<CustomEnchant, Integer> entry : enchantmentMap.entrySet()) {
                String enchString = entry.getKey().getDisplayName() + " " + entry.getValue() + ", ";

                if((enchantments + enchString).length() > 32) {
                    lore.add(enchantments.toString());
                    enchantments = new StringBuilder(ChatColor.BLUE + enchString);
                } else {
                    enchantments.append(enchString);
                }
            }

            lore.add(enchantments.toString());
        }

        lore.add(" ");

        ItemRarity rarity = getCustomItem(item).getRarity();
        lore.add(ChatColor.BOLD.toString() + rarity.getRarityColor() + rarity.getDisplay());

        return lore;
    }
}
