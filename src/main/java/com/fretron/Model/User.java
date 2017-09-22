/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.Model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class User extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"User\",\"namespace\":\"com.fretron.Model\",\"fields\":[{\"name\":\"userId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"email\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"mobile\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"transporterId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"groupId\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"]},{\"name\":\"isDeleted\",\"type\":\"boolean\",\"default\":false}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.String userId;
  @Deprecated public java.lang.String name;
  @Deprecated public java.lang.String email;
  @Deprecated public java.lang.String mobile;
  @Deprecated public java.lang.String transporterId;
  @Deprecated public java.lang.String groupId;
  @Deprecated public boolean isDeleted;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public User() {}

  /**
   * All-args constructor.
   */
  public User(java.lang.String userId, java.lang.String name, java.lang.String email, java.lang.String mobile, java.lang.String transporterId, java.lang.String groupId, java.lang.Boolean isDeleted) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.mobile = mobile;
    this.transporterId = transporterId;
    this.groupId = groupId;
    this.isDeleted = isDeleted;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return userId;
    case 1: return name;
    case 2: return email;
    case 3: return mobile;
    case 4: return transporterId;
    case 5: return groupId;
    case 6: return isDeleted;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: userId = (java.lang.String)value$; break;
    case 1: name = (java.lang.String)value$; break;
    case 2: email = (java.lang.String)value$; break;
    case 3: mobile = (java.lang.String)value$; break;
    case 4: transporterId = (java.lang.String)value$; break;
    case 5: groupId = (java.lang.String)value$; break;
    case 6: isDeleted = (java.lang.Boolean)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'userId' field.
   */
  public java.lang.String getUserId() {
    return userId;
  }

  /**
   * Sets the value of the 'userId' field.
   * @param value the value to set.
   */
  public void setUserId(java.lang.String value) {
    this.userId = value;
  }

  /**
   * Gets the value of the 'name' field.
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'email' field.
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * Sets the value of the 'email' field.
   * @param value the value to set.
   */
  public void setEmail(java.lang.String value) {
    this.email = value;
  }

  /**
   * Gets the value of the 'mobile' field.
   */
  public java.lang.String getMobile() {
    return mobile;
  }

  /**
   * Sets the value of the 'mobile' field.
   * @param value the value to set.
   */
  public void setMobile(java.lang.String value) {
    this.mobile = value;
  }

  /**
   * Gets the value of the 'transporterId' field.
   */
  public java.lang.String getTransporterId() {
    return transporterId;
  }

  /**
   * Sets the value of the 'transporterId' field.
   * @param value the value to set.
   */
  public void setTransporterId(java.lang.String value) {
    this.transporterId = value;
  }

  /**
   * Gets the value of the 'groupId' field.
   */
  public java.lang.String getGroupId() {
    return groupId;
  }

  /**
   * Sets the value of the 'groupId' field.
   * @param value the value to set.
   */
  public void setGroupId(java.lang.String value) {
    this.groupId = value;
  }

  /**
   * Gets the value of the 'isDeleted' field.
   */
  public java.lang.Boolean getIsDeleted() {
    return isDeleted;
  }

  /**
   * Sets the value of the 'isDeleted' field.
   * @param value the value to set.
   */
  public void setIsDeleted(java.lang.Boolean value) {
    this.isDeleted = value;
  }

  /** Creates a new User RecordBuilder */
  public static com.fretron.Model.User.Builder newBuilder() {
    return new com.fretron.Model.User.Builder();
  }
  
  /** Creates a new User RecordBuilder by copying an existing Builder */
  public static com.fretron.Model.User.Builder newBuilder(com.fretron.Model.User.Builder other) {
    return new com.fretron.Model.User.Builder(other);
  }
  
  /** Creates a new User RecordBuilder by copying an existing User instance */
  public static com.fretron.Model.User.Builder newBuilder(com.fretron.Model.User other) {
    return new com.fretron.Model.User.Builder(other);
  }
  
  /**
   * RecordBuilder for User instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<User>
    implements org.apache.avro.data.RecordBuilder<User> {

    private java.lang.String userId;
    private java.lang.String name;
    private java.lang.String email;
    private java.lang.String mobile;
    private java.lang.String transporterId;
    private java.lang.String groupId;
    private boolean isDeleted;

    /** Creates a new Builder */
    private Builder() {
      super(com.fretron.Model.User.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.fretron.Model.User.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.email)) {
        this.email = data().deepCopy(fields()[2].schema(), other.email);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.mobile)) {
        this.mobile = data().deepCopy(fields()[3].schema(), other.mobile);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.transporterId)) {
        this.transporterId = data().deepCopy(fields()[4].schema(), other.transporterId);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.groupId)) {
        this.groupId = data().deepCopy(fields()[5].schema(), other.groupId);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.isDeleted)) {
        this.isDeleted = data().deepCopy(fields()[6].schema(), other.isDeleted);
        fieldSetFlags()[6] = true;
      }
    }
    
    /** Creates a Builder by copying an existing User instance */
    private Builder(com.fretron.Model.User other) {
            super(com.fretron.Model.User.SCHEMA$);
      if (isValidValue(fields()[0], other.userId)) {
        this.userId = data().deepCopy(fields()[0].schema(), other.userId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.email)) {
        this.email = data().deepCopy(fields()[2].schema(), other.email);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.mobile)) {
        this.mobile = data().deepCopy(fields()[3].schema(), other.mobile);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.transporterId)) {
        this.transporterId = data().deepCopy(fields()[4].schema(), other.transporterId);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.groupId)) {
        this.groupId = data().deepCopy(fields()[5].schema(), other.groupId);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.isDeleted)) {
        this.isDeleted = data().deepCopy(fields()[6].schema(), other.isDeleted);
        fieldSetFlags()[6] = true;
      }
    }

    /** Gets the value of the 'userId' field */
    public java.lang.String getUserId() {
      return userId;
    }
    
    /** Sets the value of the 'userId' field */
    public com.fretron.Model.User.Builder setUserId(java.lang.String value) {
      validate(fields()[0], value);
      this.userId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'userId' field has been set */
    public boolean hasUserId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'userId' field */
    public com.fretron.Model.User.Builder clearUserId() {
      userId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.String getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public com.fretron.Model.User.Builder setName(java.lang.String value) {
      validate(fields()[1], value);
      this.name = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'name' field has been set */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'name' field */
    public com.fretron.Model.User.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'email' field */
    public java.lang.String getEmail() {
      return email;
    }
    
    /** Sets the value of the 'email' field */
    public com.fretron.Model.User.Builder setEmail(java.lang.String value) {
      validate(fields()[2], value);
      this.email = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'email' field has been set */
    public boolean hasEmail() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'email' field */
    public com.fretron.Model.User.Builder clearEmail() {
      email = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'mobile' field */
    public java.lang.String getMobile() {
      return mobile;
    }
    
    /** Sets the value of the 'mobile' field */
    public com.fretron.Model.User.Builder setMobile(java.lang.String value) {
      validate(fields()[3], value);
      this.mobile = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'mobile' field has been set */
    public boolean hasMobile() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'mobile' field */
    public com.fretron.Model.User.Builder clearMobile() {
      mobile = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'transporterId' field */
    public java.lang.String getTransporterId() {
      return transporterId;
    }
    
    /** Sets the value of the 'transporterId' field */
    public com.fretron.Model.User.Builder setTransporterId(java.lang.String value) {
      validate(fields()[4], value);
      this.transporterId = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'transporterId' field has been set */
    public boolean hasTransporterId() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'transporterId' field */
    public com.fretron.Model.User.Builder clearTransporterId() {
      transporterId = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'groupId' field */
    public java.lang.String getGroupId() {
      return groupId;
    }
    
    /** Sets the value of the 'groupId' field */
    public com.fretron.Model.User.Builder setGroupId(java.lang.String value) {
      validate(fields()[5], value);
      this.groupId = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'groupId' field has been set */
    public boolean hasGroupId() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'groupId' field */
    public com.fretron.Model.User.Builder clearGroupId() {
      groupId = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'isDeleted' field */
    public java.lang.Boolean getIsDeleted() {
      return isDeleted;
    }
    
    /** Sets the value of the 'isDeleted' field */
    public com.fretron.Model.User.Builder setIsDeleted(boolean value) {
      validate(fields()[6], value);
      this.isDeleted = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'isDeleted' field has been set */
    public boolean hasIsDeleted() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'isDeleted' field */
    public com.fretron.Model.User.Builder clearIsDeleted() {
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public User build() {
      try {
        User record = new User();
        record.userId = fieldSetFlags()[0] ? this.userId : (java.lang.String) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.String) defaultValue(fields()[1]);
        record.email = fieldSetFlags()[2] ? this.email : (java.lang.String) defaultValue(fields()[2]);
        record.mobile = fieldSetFlags()[3] ? this.mobile : (java.lang.String) defaultValue(fields()[3]);
        record.transporterId = fieldSetFlags()[4] ? this.transporterId : (java.lang.String) defaultValue(fields()[4]);
        record.groupId = fieldSetFlags()[5] ? this.groupId : (java.lang.String) defaultValue(fields()[5]);
        record.isDeleted = fieldSetFlags()[6] ? this.isDeleted : (java.lang.Boolean) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
