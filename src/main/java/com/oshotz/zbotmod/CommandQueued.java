
package com.oshotz.zbotmod;

public class CommandQueued {

  // The command and commsID:
  private Command command;
  private String commsID;
  
  // The run thread and timeout thread:
  private Thread runThread, timeoutThread;

  public CommandQueued() { }
  
  public CommandQueued(Command command, String commsID) {

    this.command = command;
    this.commsID = commsID;

  }

  // Public getters and setters:
  public void setCommand(Command command) {

    this.command = command;

  }

  public Command getCommand() {

    return command;

  }

  public void setCommsID(String commsID) {

    this.commsID = commsID;

  }

  public String getCommsID() {

    return commsID;

  }

  public void run() {

    // Run the command in a separate thread (so as to not block the main thread):
    runThread = new Thread(() -> {

      try {

        command.run();

      } catch (InterruptedException e) { }

    });

    // Initiate the execution:
    runThread.start();

    // In the event that something goes wrong and the command execution hangs, the thread will be timed out:
    timeoutThread = new Thread(() -> {

      try {

        // Wait 60 seconds:
        Thread.sleep(20 * 1000);

        // Then, interrupt the thread if needed:
        ZBot.commandHandler.onError("Command timed out!");

      } catch (InterruptedException e) { }

    });

    // Initiate the timeout thread:
    timeoutThread.start();

  }

  public void close() {

    // Interrupt the command thread if it is still running (in the case of an error, for example):
    if (runThread.isAlive())
      runThread.interrupt();

    if (timeoutThread.isAlive())
      timeoutThread.interrupt();

  }

  @Override
  public String toString() {

    return command.toString();

  }

}
