
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class Command_getHousingStats extends Command {

  private boolean visited;

  // Inherit the constructors from Command:
  public Command_getHousingStats() { super(); }
  public Command_getHousingStats(String... args) { super(args); }

  public void run() {

    visited = false;

    // If the bot is already in a housing server, run the /visit command:
    if (ZBot.isInHousing()) {
      visit();
      return;
    }

    // Otherwise, join a housing lobby first so the /visit command is available:
    ZBot.sendCommand("lobby", "housing");

  }

  private void visit() {

    if (visited)
      return;
    
    // Visit the specified housing (if the bot hasn't already done so):
    ZBot.sendCommand("visit", args.get(0));
    visited = true;

  }

  public void onSidebarHeaderChange(String sidebarHeader) {

    // Ensure that the bot is now in a housing server:
    if (!ZBot.isInHousing())
      return;

    // If it is, visit the housing:
    visit();

  }

  public void onChat(String rawMessage) {

    // Clean the message:
    String message = ZBot.removeFormatting(rawMessage);

    // The specified player doesn't have any houses:
    if (message.contains("No houses found for that player!"))
      ZBot.commandHandler.onError("The player " + args.get(0) + " doesn't have any housings.", true);
    
  }

  public void onGUI(String GUIName, ArrayList<GUIStack> GUIInventory, int GUIID) {

    // Keep track of the housing data in a flat ArrayList (ordered [<name>, <server>, <cookies>, <guests>]):
    ArrayList<String> housingData = new ArrayList<String>();
    String cleanString;

    // Ensure that the housing is valid:
    if (Integer.parseInt(args.get(1)) < 1 || Integer.parseInt(args.get(1)) > GUIInventory.size()) {
    
      // No housing was found; close the GUI and return an error:
      ZBot.closeGUI();
      ZBot.commandHandler.onError("Invalid housing number.", "Unable to join " + args.get(0) + "'s housing: Housing #" + args.get(1) + " does not exist.");
      return;

    }

    // housingData[0] will store the proper username of the player:
    housingData.add(GUIName.substring(0, GUIName.indexOf("'s Houses")));
    
    // Get the lore:
    ArrayList<String> lore = GUIInventory.get(Integer.parseInt(args.get(1)) - 1).getLore();

    // Add the housing's name:
    housingData.add(GUIInventory.get(Integer.parseInt(args.get(1)) - 1).getName());

    // Get and add the housing's server:
    String name = ZBot.removeFormatting(lore.get(0));
    housingData.add((name.equals("Active") || name.equals("No server")) ? "Offline" : name);

    // Get the cookies and guests:
    String cookies = "0";
    String guests = "0";
    for (int j = 2; j < lore.size(); j++) {

      cleanString = ZBot.removeFormatting(lore.get(j));

      if (cleanString.startsWith("Cookies: "))
        cookies = cleanString.substring(cleanString.indexOf("Cookies: ") + 9);
      
      if (cleanString.startsWith("Players: "))
        guests = cleanString.substring(cleanString.indexOf("Players: ") + 9);

    }

    housingData.add(String.join("", cookies.split(",")));
    housingData.add(String.join("", guests.split(",")));

    // Close the GUI and return the housing data:
    ZBot.closeGUI();
    ZBot.commandHandler.onComplete("Successfully retrieved stats for " + GUIName.substring(0, GUIName.indexOf("'s Houses")) + "'s housing #" + args.get(1) + ".", housingData);

  }

  public String toString() {

    return "[Command_getHousingStats]";
    
  }
  
}
