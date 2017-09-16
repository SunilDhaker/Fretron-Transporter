/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.Model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class CommandOfTransporter extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CommandOfTransporter\",\"namespace\":\"com.fretron.Model\",\"fields\":[{\"name\":\"type\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"data\",\"type\":{\"type\":\"record\",\"name\":\"Transporter\",\"fields\":[{\"name\":\"transporterId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"adminEmail\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null},{\"name\":\"groups\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Groups\",\"fields\":[{\"name\":\"groupId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"subgroups\",\"type\":{\"type\":\"array\",\"items\":\"Groups\"}},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"admin\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"members\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null}]}}],\"default\":null}]}},{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"statusCode\",\"type\":[\"null\",\"int\"]},{\"name\":\"errorMessage\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"startTime\",\"type\":[\"null\",\"long\"],\"default\":null,\"logicalType\":\"timestamp-millis\"},{\"name\":\"processTime\",\"type\":[\"null\",\"long\"],\"default\":null,\"logicalType\":\"timestamp-millis\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.String type;
  @Deprecated public com.fretron.Model.Transporter data;
  @Deprecated public java.lang.String id;
  @Deprecated public java.lang.Integer statusCode;
  @Deprecated public java.lang.String errorMessage;
  @Deprecated public java.lang.Long startTime;
  @Deprecated public java.lang.Long processTime;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public CommandOfTransporter() {}

  /**
   * All-args constructor.
   */
  public CommandOfTransporter(java.lang.String type, com.fretron.Model.Transporter data, java.lang.String id, java.lang.Integer statusCode, java.lang.String errorMessage, java.lang.Long startTime, java.lang.Long processTime) {
    this.type = type;
    this.data = data;
    this.id = id;
    this.statusCode = statusCode;
    this.errorMessage = errorMessage;
    this.startTime = startTime;
    this.processTime = processTime;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return type;
    case 1: return data;
    case 2: return id;
    case 3: return statusCode;
    case 4: return errorMessage;
    case 5: return startTime;
    case 6: return processTime;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: type = (java.lang.String)value$; break;
    case 1: data = (com.fretron.Model.Transporter)value$; break;
    case 2: id = (java.lang.String)value$; break;
    case 3: statusCode = (java.lang.Integer)value$; break;
    case 4: errorMessage = (java.lang.String)value$; break;
    case 5: startTime = (java.lang.Long)value$; break;
    case 6: processTime = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'type' field.
   */
  public java.lang.String getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.String value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'data' field.
   */
  public com.fretron.Model.Transporter getData() {
    return data;
  }

  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(com.fretron.Model.Transporter value) {
    this.data = value;
  }

  /**
   * Gets the value of the 'id' field.
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.String value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'statusCode' field.
   */
  public java.lang.Integer getStatusCode() {
    return statusCode;
  }

  /**
   * Sets the value of the 'statusCode' field.
   * @param value the value to set.
   */
  public void setStatusCode(java.lang.Integer value) {
    this.statusCode = value;
  }

  /**
   * Gets the value of the 'errorMessage' field.
   */
  public java.lang.String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the value of the 'errorMessage' field.
   * @param value the value to set.
   */
  public void setErrorMessage(java.lang.String value) {
    this.errorMessage = value;
  }

  /**
   * Gets the value of the 'startTime' field.
   */
  public java.lang.Long getStartTime() {
    return startTime;
  }

  /**
   * Sets the value of the 'startTime' field.
   * @param value the value to set.
   */
  public void setStartTime(java.lang.Long value) {
    this.startTime = value;
  }

  /**
   * Gets the value of the 'processTime' field.
   */
  public java.lang.Long getProcessTime() {
    return processTime;
  }

  /**
   * Sets the value of the 'processTime' field.
   * @param value the value to set.
   */
  public void setProcessTime(java.lang.Long value) {
    this.processTime = value;
  }

  /** Creates a new CommandOfTransporter RecordBuilder */
  public static com.fretron.Model.CommandOfTransporter.Builder newBuilder() {
    return new com.fretron.Model.CommandOfTransporter.Builder();
  }
  
  /** Creates a new CommandOfTransporter RecordBuilder by copying an existing Builder */
  public static com.fretron.Model.CommandOfTransporter.Builder newBuilder(com.fretron.Model.CommandOfTransporter.Builder other) {
    return new com.fretron.Model.CommandOfTransporter.Builder(other);
  }
  
  /** Creates a new CommandOfTransporter RecordBuilder by copying an existing CommandOfTransporter instance */
  public static com.fretron.Model.CommandOfTransporter.Builder newBuilder(com.fretron.Model.CommandOfTransporter other) {
    return new com.fretron.Model.CommandOfTransporter.Builder(other);
  }
  
  /**
   * RecordBuilder for CommandOfTransporter instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CommandOfTransporter>
    implements org.apache.avro.data.RecordBuilder<CommandOfTransporter> {

    private java.lang.String type;
    private com.fretron.Model.Transporter data;
    private java.lang.String id;
    private java.lang.Integer statusCode;
    private java.lang.String errorMessage;
    private java.lang.Long startTime;
    private java.lang.Long processTime;

    /** Creates a new Builder */
    private Builder() {
      super(com.fretron.Model.CommandOfTransporter.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.fretron.Model.CommandOfTransporter.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.data)) {
        this.data = data().deepCopy(fields()[1].schema(), other.data);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.id)) {
        this.id = data().deepCopy(fields()[2].schema(), other.id);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.statusCode)) {
        this.statusCode = data().deepCopy(fields()[3].schema(), other.statusCode);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.errorMessage)) {
        this.errorMessage = data().deepCopy(fields()[4].schema(), other.errorMessage);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.startTime)) {
        this.startTime = data().deepCopy(fields()[5].schema(), other.startTime);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.processTime)) {
        this.processTime = data().deepCopy(fields()[6].schema(), other.processTime);
        fieldSetFlags()[6] = true;
      }
    }
    
    /** Creates a Builder by copying an existing CommandOfTransporter instance */
    private Builder(com.fretron.Model.CommandOfTransporter other) {
            super(com.fretron.Model.CommandOfTransporter.SCHEMA$);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.data)) {
        this.data = data().deepCopy(fields()[1].schema(), other.data);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.id)) {
        this.id = data().deepCopy(fields()[2].schema(), other.id);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.statusCode)) {
        this.statusCode = data().deepCopy(fields()[3].schema(), other.statusCode);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.errorMessage)) {
        this.errorMessage = data().deepCopy(fields()[4].schema(), other.errorMessage);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.startTime)) {
        this.startTime = data().deepCopy(fields()[5].schema(), other.startTime);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.processTime)) {
        this.processTime = data().deepCopy(fields()[6].schema(), other.processTime);
        fieldSetFlags()[6] = true;
      }
    }

    /** Gets the value of the 'type' field */
    public java.lang.String getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public com.fretron.Model.CommandOfTransporter.Builder setType(java.lang.String value) {
      validate(fields()[0], value);
      this.type = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'type' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearType() {
      type = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'data' field */
    public com.fretron.Model.Transporter getData() {
      return data;
    }
    
    /** Sets the value of the 'data' field */
    public com.fretron.Model.CommandOfTransporter.Builder setData(com.fretron.Model.Transporter value) {
      validate(fields()[1], value);
      this.data = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'data' field has been set */
    public boolean hasData() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'data' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearData() {
      data = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'id' field */
    public java.lang.String getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public com.fretron.Model.CommandOfTransporter.Builder setId(java.lang.String value) {
      validate(fields()[2], value);
      this.id = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'id' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearId() {
      id = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'statusCode' field */
    public java.lang.Integer getStatusCode() {
      return statusCode;
    }
    
    /** Sets the value of the 'statusCode' field */
    public com.fretron.Model.CommandOfTransporter.Builder setStatusCode(java.lang.Integer value) {
      validate(fields()[3], value);
      this.statusCode = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'statusCode' field has been set */
    public boolean hasStatusCode() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'statusCode' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearStatusCode() {
      statusCode = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'errorMessage' field */
    public java.lang.String getErrorMessage() {
      return errorMessage;
    }
    
    /** Sets the value of the 'errorMessage' field */
    public com.fretron.Model.CommandOfTransporter.Builder setErrorMessage(java.lang.String value) {
      validate(fields()[4], value);
      this.errorMessage = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'errorMessage' field has been set */
    public boolean hasErrorMessage() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'errorMessage' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearErrorMessage() {
      errorMessage = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'startTime' field */
    public java.lang.Long getStartTime() {
      return startTime;
    }
    
    /** Sets the value of the 'startTime' field */
    public com.fretron.Model.CommandOfTransporter.Builder setStartTime(java.lang.Long value) {
      validate(fields()[5], value);
      this.startTime = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'startTime' field has been set */
    public boolean hasStartTime() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'startTime' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearStartTime() {
      startTime = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'processTime' field */
    public java.lang.Long getProcessTime() {
      return processTime;
    }
    
    /** Sets the value of the 'processTime' field */
    public com.fretron.Model.CommandOfTransporter.Builder setProcessTime(java.lang.Long value) {
      validate(fields()[6], value);
      this.processTime = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'processTime' field has been set */
    public boolean hasProcessTime() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'processTime' field */
    public com.fretron.Model.CommandOfTransporter.Builder clearProcessTime() {
      processTime = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public CommandOfTransporter build() {
      try {
        CommandOfTransporter record = new CommandOfTransporter();
        record.type = fieldSetFlags()[0] ? this.type : (java.lang.String) defaultValue(fields()[0]);
        record.data = fieldSetFlags()[1] ? this.data : (com.fretron.Model.Transporter) defaultValue(fields()[1]);
        record.id = fieldSetFlags()[2] ? this.id : (java.lang.String) defaultValue(fields()[2]);
        record.statusCode = fieldSetFlags()[3] ? this.statusCode : (java.lang.Integer) defaultValue(fields()[3]);
        record.errorMessage = fieldSetFlags()[4] ? this.errorMessage : (java.lang.String) defaultValue(fields()[4]);
        record.startTime = fieldSetFlags()[5] ? this.startTime : (java.lang.Long) defaultValue(fields()[5]);
        record.processTime = fieldSetFlags()[6] ? this.processTime : (java.lang.Long) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
