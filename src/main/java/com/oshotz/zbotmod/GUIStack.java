
package com.oshotz.zbotmod;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class GUIStack {
  
  // Private class variables:
  private String name;
  private String identifier;
  private int discriminator;
  private int count;
  private int slotID;
  private ArrayList<String> lore;
  private ArrayList<Enchantment> enchantments;
  
  // Constructors:
  GUIStack() {}

  GUIStack(ItemStack itemStack, int slotID) {

    this.name = Reference.rarityToColor(itemStack.getRarity()) + itemStack.getDisplayName();
    this.identifier = ZBot.getIdentifier(itemStack);
    this.discriminator = itemStack.getMetadata();
    this.count = itemStack.stackSize;
    this.slotID = slotID;
    this.lore = ZBot.getLore(itemStack);
    this.enchantments = ZBot.getEnchantments(itemStack);

  }

  GUIStack(String name, String identifier, int discriminator, int count, int slotID, ArrayList<String> lore, ArrayList<Enchantment> enchantments) {

    this.name = name;
    this.identifier = identifier;
    this.discriminator = discriminator;
    this.count = count;
    this.slotID = slotID;
    this.lore = lore;
    this.enchantments = enchantments;

  }
  
  // Public getters and setters:
  public void setName(String name) {

    this.name = name;

  }

  public String getName() {

    return name;

  }

  public void setIdentifier(String identifier) {

    this.identifier = identifier;

  }

  public String getIdentifier() {

    return identifier;

  }

  public void setDiscriminator(int discriminator) {

    this.discriminator = discriminator;

  }

  public int getDiscriminator() {

    return discriminator;

  }

  public void setCount(int count) {

    this.count = count;

  }

  public int getCount() {

    return count;

  }

  public void setSlotID(int slotID) {

    this.slotID = slotID;

  }

  public int getSlotID() {

    return slotID;

  }

  public void setLore(ArrayList<String> lore) {

    this.lore = lore;

  }

  public ArrayList<String> getLore() {

    return lore;

  }

  public void setEnchantments(ArrayList<Enchantment> enchantments) {

    this.enchantments = enchantments;

  }

  public ArrayList<Enchantment> getEnchantments() {

    return enchantments;

  }

  public String toString() {

    return "GUIStack [name=" + name + ", identifier=" + identifier + ", discriminator=" + discriminator + ", count=" + count + ", slotID=" + slotID + ", lore=" + lore + ", enchantments=" + enchantments + "]";

  }

}
