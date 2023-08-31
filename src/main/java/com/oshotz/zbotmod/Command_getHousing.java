
package com.oshotz.zbotmod;

public class Command_getHousing extends Command {

  // Inherit the constructors from Command:
  public Command_getHousing() { super(); }
  public Command_getHousing(String... args) { super(args); }

  public void run() throws InterruptedException {

    // The bot is not in a housing server:
    if (!ZBot.isInHousing()) {
      ZBot.commandHandler.onError("The bot is not currently in any Housing server.", true);
      return;
    }

    // The bot is in a housing lobby:
    if (!ZBot.isInHouse()) {
      ZBot.commandHandler.onError("The bot is currently in a Housing lobby.", true);
      return;
    }

    // The bot is in a housing; get the housing's data:
    Housing housing = ZBot.getHousing();

    // And return it as a response:
    ZBot.commandHandler.onComplete("Successfully found the bot's housing.", housing.toArrayList());

  }

  public String toString() {

    return "[Command_getHousing]";
    
  }
  
}
