
package com.oshotz.zbotmod;

public class MongoDBEventHandler {

  public <T extends MongoDBEvent> void onEvent(T event) { System.out.println("THIS IS THE WRONG ONE"); }

  public Class<? extends MongoDBEvent> getEventClass() { System.out.println("THIS IS THE WRONG ONE"); return MongoDBEvent.class; }
  
}
