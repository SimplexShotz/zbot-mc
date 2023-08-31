
package com.oshotz.zbotmod;

import org.bson.types.ObjectId;

public class MongoDBEvent {
  
  private ObjectId id;
  
  public MongoDBEvent() {}

  public ObjectId getId() {

    return id;

  }

  public void setId(ObjectId id) {

    this.id = id;

  }

  public String type() {

    return "MongoDBEvent";

  }

  @Override
  public String toString() {

    return "MongoDBEvent [id=" + id + "]";

  }
  
}
