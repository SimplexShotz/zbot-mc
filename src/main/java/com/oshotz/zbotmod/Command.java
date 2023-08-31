
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class Command {

  protected ArrayList<String> args;

  // Default Constructor:
  public Command() {

    this.args = new ArrayList<String>();

  }

  // Constructor with one or multiple arguments:
  public Command(String... args) {

    this.args = new ArrayList<String>();

    for (String arg : args) {
      this.args.add(arg);
    }

  }
  
  // Getter for the args variable:
  public ArrayList<String> getArgs() {

    return args;

  }

  // Setter for the args variable:
  public void setArgs(ArrayList<String> args) {

    this.args = args;

  }

  public void commandLog(String res) {

    System.out.println(toString() + " " + res);

  }

  // Virtual run function, to be implemented by commands:
  public void run() throws InterruptedException {}

  // Virtual onGUI event, run whenever a chest GUI is opened (during command lifespan, if implemented):
  public void onGUI(String GUIName, ArrayList<GUIStack> GUIInventory, int GUIID) {}
  
  // Virtual onChat event, run whenever a chat message is received (during command lifespan, if implemented):
  public void onChat(String message) {}
  
  // Virtual onTabHeaderChange event, run whenever the tab header changes (during command lifespan, if implemented):
  public void onTabHeaderChange(String tabHeader) {}
  
  // Virtual onTabFooterChange event, run whenever the tab footer changes (during command lifespan, if implemented):
  public void onTabFooterChange(String tabFooter) {}
  
  // Virtual onSidebarHeaderChange event, run whenever the sidebar header changes (during command lifespan, if implemented):
  public void onSidebarHeaderChange(String sidebarHeader) {}
  
  // Virtual onHousingUpdate event, run whenever the housing is updated (during command lifespan, if implemented):
  public void onHousingUpdate(Housing housing) {}
  
  // Virtual onHousingChange event, run whenever the housing changes (during command lifespan, if implemented):
  public void onHousingChange(Housing housing) {}

  // Default toString method, run whenever a command is printed to the console:
  public String toString() {

    return "[Command]";
    
  }
  
}
