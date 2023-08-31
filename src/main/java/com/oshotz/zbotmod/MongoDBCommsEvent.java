
package com.oshotz.zbotmod;

import java.util.ArrayList;

import org.bson.types.ObjectId;

public class MongoDBCommsEvent extends MongoDBEvent {

  // Private class variables:
  private ObjectId id;
  private String commsID;
  private String method;
  private ArrayList<String> args;

  // Constructors:
  public MongoDBCommsEvent() {}

  public MongoDBCommsEvent(String commsID, String method, ArrayList<String> args) {

    this.commsID = commsID;
    this.method = method;
    this.args = args;
    
  }

  // Public getters and setters:
  public ObjectId getID() {

    return id;

  }

  public void setID(ObjectId id) {

    this.id = id;

  }

  public String getCommsID() {

    return commsID;

  }

  public void setCommsID(String commsID) {

    this.commsID = commsID;

  }

  public String getMethod() {

    return method;

  }

  public void setMethod(String method) {

    this.method = method;

  }

  public ArrayList<String> getArgs() {

    return args;

  }

  public void setArgs(ArrayList<String> args) {

    this.args = args;

  }

  public String type() {

    return "MongoDBCommsEvent";

  }

  @Override
  public String toString() {

    return "MongoDBCommsEvent [id=" + id + ", method=" + method + ", args=" + args + "]";

  }
  
}