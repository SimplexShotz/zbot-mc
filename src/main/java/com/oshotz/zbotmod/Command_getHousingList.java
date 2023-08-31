
package com.oshotz.zbotmod;

import java.util.ArrayList;

public class Command_getHousingList extends Command {

  private boolean visited;

  // Inherit the constructors from Command:
  public Command_getHousingList() { super(); }
  public Command_getHousingList(String... args) { super(args); }

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
    ArrayList<String> housingData = new ArrayList<String>(), lore;
    String name, cleanString, cookies, guests;

    // housingData[0] will store the proper username of the player:
    housingData.add(GUIName.substring(0, GUIName.indexOf("'s Houses")));

    // Get the housing data:
    for (int i = 0; i < GUIInventory.size(); i++) {

      // Get the lore:
      lore = GUIInventory.get(i).getLore();

      // Add the housing's name:
      housingData.add(GUIInventory.get(i).getName());

      // Get and add the housing's server:
      name = ZBot.removeFormatting(lore.get(0));
      housingData.add((name.equals("Active") || name.equals("No server")) ? "Offline" : name);

      // Get the cookies and guests:
      cookies = "0";
      guests = "0";
      for (int j = 2; j < lore.size(); j++) {

        cleanString = ZBot.removeFormatting(lore.get(j));

        if (cleanString.startsWith("Cookies: "))
          cookies = cleanString.substring(cleanString.indexOf("Cookies: ") + 9);
        
        if (cleanString.startsWith("Players: "))
          guests = cleanString.substring(cleanString.indexOf("Players: ") + 9);

      }

      housingData.add(String.join("", cookies.split(",")));
      housingData.add(String.join("", guests.split(",")));

    }

    // Close the GUI and return the housing data:
    ZBot.closeGUI();
    ZBot.commandHandler.onComplete("Successfully retrieved housings for " + GUIName.substring(0, GUIName.indexOf("'s Houses")) + ".", housingData);

  }

  public String toString() {

    return "[Command_getHousingList]";
    
  }
  
}
