
package com.oshotz.zbotmod;

public class Enchantment {

  // Private class variables:
  private String enchantment;
  private int level;
  
  // Constructors:
  Enchantment() {}

  Enchantment(String enchantment, int level) {

    this.enchantment = enchantment;
    this.level = level;

  }
  
  // Public getters and setters:
  public void setEnchantment(String enchantment) {

    this.enchantment = enchantment;

  }

  public String getEnchantment() {

    return enchantment;

  }
  
  public void setLevel(int level) {

    this.level = level;

  }

  public int getLevel() {

    return level;

  }

  public String toString() {

    return "Enchantment [enchantment=" + enchantment + ", level=" + level + "]";

  }

}
