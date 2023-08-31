
package com.oshotz.zbotmod;

import java.util.ArrayList;
import java.util.Arrays;

public class Housing {

  // Private class variables:
  private String owner;
  private String name;
  private int guests = -1;
  private int cookies = -1;

  // Constructors:
  Housing() {}

  Housing(String owner, String name, int guests, int cookies) {

    this.owner = owner;
    this.name = name;
    this.guests = guests;
    this.cookies = cookies;

  }
  
  // Public getters and setters:
  public void setOwner(String owner) {

    this.owner = owner;

  }

  public String getOwner() {

    return owner;

  }

  public void setName(String name) {

    this.name = name;

  }

  public String getName() {

    return name;

  }

  public void setGuests(int guests) {

    this.guests = guests;

  }

  public int getGuests() {

    return guests;

  }

  public void setCookies(int cookies) {

    this.cookies = cookies;

  }

  public int getCookies() {

    return cookies;

  }

  public boolean equals(Housing other) {

    if (!exists() || !other.exists())
      return !exists() && !other.exists();
    
    return owner.equals(other.getOwner()) && name.equals(other.getName());

  }

  public boolean equalsStrict(Housing other) {

    if (!exists() || !other.exists())
      return !exists() && !other.exists();
    
    return owner.equals(other.getOwner()) && name.equals(other.getName()) && (guests == other.getGuests()) && (cookies == other.getCookies());

  }

  public boolean exists() {

    return owner != null && name != null && guests != -1 && cookies != -1;

  }

  public ArrayList<String> toArrayList() {

    return new ArrayList<String>(Arrays.asList(owner, name, Integer.toString(guests), Integer.toString(cookies)));

  }

  public String toString() {

    return "Housing [owner=" + owner + ", name=" + name + ", guests=" + guests + ", cookies=" + cookies + "]";

  }
  
}
