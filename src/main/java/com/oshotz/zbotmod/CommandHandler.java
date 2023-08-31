
package com.oshotz.zbotmod;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler {

  // The command queue:
  private ArrayList<CommandQueued> queue;

  // Default constructor:
  public CommandHandler() {

    queue = new ArrayList<CommandQueued>();

  }
  
  // Run a command:
  public void run(Command command, String commsID) {

    // First, add the command to the queue:
    queue.add(new CommandQueued(command, commsID));
    System.out.println("[Q" + queue.size() + "] Queued execution of command " + command + ".");

    // If it is the only command in the queue, run it immediately:
    if (queue.size() == 1)
      runNext();

    // Otherwise, the command will be run once it is at the front of the queue.

  }

  private void runNext() {

    // Default message for "runNext":
    runNext("[Q" + queue.size() + "] Execution of command " + queue.get(0).toString() + " initiated.");

  }

  private void runNext(String res) {

    // Print out the command response:
    System.out.println(res);

    // Run the next command in the queue:
    queue.get(0).run();

  }

  public void onComplete() {

    // Default message and resEvent for "onComplete" (resEvent does nothing):
    onComplete("[Q" + (queue.size() - 1) + "] Execution of command " + queue.get(0).toString() + " complete.", new MongoDBEvent());

  }
  
  public void onComplete(String resString) {

    // Default resEvent for "onComplete" (resEvent does nothing):
    onComplete(resString, new MongoDBEvent());

  }

  public void onComplete(String resString, String resEventString) {

    // Run "onComplete" with a MongoDBCommsEvent generated from the resEventString:
    onComplete(resString, new MongoDBCommsEvent(queue.get(0).getCommsID(), "res", new ArrayList<String>(Arrays.asList(resEventString))));

  }

  public void onComplete(String resString, ArrayList<String> resEventArrayList) {

    // Run "onComplete" with a MongoDBCommsEvent generated from the resEventString:
    onComplete(resString, new MongoDBCommsEvent(queue.get(0).getCommsID(), "res", resEventArrayList));

  }

  public void onComplete(String resString, boolean copyResString) {

    // Run "onComplete" with a MongoDBCommsEvent generated from the resString:
    if (copyResString)
      onComplete(resString, resString);
    else
      onComplete(resString);

  }
  
  public <T extends MongoDBEvent> void onComplete(T resEvent) {

    // Default message for "onComplete":
    onComplete("[Q" + (queue.size() - 1) + "] Execution of command " + queue.get(0).toString() + " complete.", resEvent);

  }

  public <T extends MongoDBEvent> void onComplete(String resString, T resEvent) {

    // Print out the command response:
    System.out.println(resString);

    // Send the response event to MongoDB:
    switch(resEvent.type()) {

      case "MongoDBCommsEvent":
        ZBot.getCollection("comms", MongoDBCommsEvent.class).insertOne((MongoDBCommsEvent)resEvent);
      break;

    }
    
    // Close any command threads still running:
    queue.get(0).close();

    // Remove the finished command from the queue:
    queue.remove(0);

    // If there are more commands in the queue, run them in sequence:
    if (queue.size() >= 1)
      runNext();

  }

  public void onError(String resString) {

    onComplete("[Q" + (queue.size() - 1) + "] Call to " + queue.get(0).toString() + " failed: " + resString);

  }
  
  public <T extends MongoDBEvent> void onError(String resString, T resEvent) {

    onComplete("[Q" + (queue.size() - 1) + "] Call to " + queue.get(0).toString() + " failed: " + resString, resEvent);

  }
  
  public void onError(String resString, String resEventString) {

    onComplete("[Q" + (queue.size() - 1) + "] Call to " + queue.get(0).toString() + " failed: " + resString, new MongoDBCommsEvent(queue.get(0).getCommsID(), "err", new ArrayList<String>(Arrays.asList(resEventString))));

  }

  public void onError(String resString, boolean copyResString) {

    if (copyResString)
      onError(resString, resString);
    else
      onError(resString);

  }

  public void onChat(String message) {

    // When a Chest GUI is opened, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onChat(message);

  }

  public void onGUI(String GUIName, ArrayList<GUIStack> GUIInventory, int GUIID) {

    // When a Chest GUI is opened, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onGUI(GUIName, GUIInventory, GUIID);

  }

  public void onTabHeaderChange(String tabHeader) {

    // When the tab header changes, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onTabHeaderChange(tabHeader);

  }

  public void onTabFooterChange(String tabFooter) {

    // When the tab footer changes, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onTabFooterChange(tabFooter);

  }

  public void onSidebarHeaderChange(String sidebarHeader) {

    // When the sidebar header changes, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onSidebarHeaderChange(sidebarHeader);

  }

  public void onHousingUpdate(Housing housing) {

    // When the housing is updated, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onHousingUpdate(housing);

  }

  public void onHousingChange(Housing housing) {

    // When the housing changes, pass the event to the current command, if there is currently a command running:
    if (queue.size() >= 1)
      queue.get(0).getCommand().onHousingChange(housing);

  }

}
