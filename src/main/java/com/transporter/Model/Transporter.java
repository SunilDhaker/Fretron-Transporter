/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.transporter.Model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Transporter extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Transporter\",\"namespace\":\"com.transporter.Model\",\"fields\":[{\"name\":\"adminEmail\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null},{\"name\":\"groups\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Groups\",\"fields\":[{\"name\":\"subgroups\",\"type\":{\"type\":\"array\",\"items\":\"Groups\"}},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"admin\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"members\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null}]}}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.util.List<java.lang.String> adminEmail;
  @Deprecated public java.util.List<com.transporter.Model.Groups> groups;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public Transporter() {}

  /**
   * All-args constructor.
   */
  public Transporter(java.util.List<java.lang.String> adminEmail, java.util.List<com.transporter.Model.Groups> groups) {
    this.adminEmail = adminEmail;
    this.groups = groups;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return adminEmail;
    case 1: return groups;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: adminEmail = (java.util.List<java.lang.String>)value$; break;
    case 1: groups = (java.util.List<com.transporter.Model.Groups>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'adminEmail' field.
   */
  public java.util.List<java.lang.String> getAdminEmail() {
    return adminEmail;
  }

  /**
   * Sets the value of the 'adminEmail' field.
   * @param value the value to set.
   */
  public void setAdminEmail(java.util.List<java.lang.String> value) {
    this.adminEmail = value;
  }

  /**
   * Gets the value of the 'groups' field.
   */
  public java.util.List<com.transporter.Model.Groups> getGroups() {
    return groups;
  }

  /**
   * Sets the value of the 'groups' field.
   * @param value the value to set.
   */
  public void setGroups(java.util.List<com.transporter.Model.Groups> value) {
    this.groups = value;
  }

  /** Creates a new Transporter RecordBuilder */
  public static com.transporter.Model.Transporter.Builder newBuilder() {
    return new com.transporter.Model.Transporter.Builder();
  }
  
  /** Creates a new Transporter RecordBuilder by copying an existing Builder */
  public static com.transporter.Model.Transporter.Builder newBuilder(com.transporter.Model.Transporter.Builder other) {
    return new com.transporter.Model.Transporter.Builder(other);
  }
  
  /** Creates a new Transporter RecordBuilder by copying an existing Transporter instance */
  public static com.transporter.Model.Transporter.Builder newBuilder(com.transporter.Model.Transporter other) {
    return new com.transporter.Model.Transporter.Builder(other);
  }
  
  /**
   * RecordBuilder for Transporter instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Transporter>
    implements org.apache.avro.data.RecordBuilder<Transporter> {

    private java.util.List<java.lang.String> adminEmail;
    private java.util.List<com.transporter.Model.Groups> groups;

    /** Creates a new Builder */
    private Builder() {
      super(com.transporter.Model.Transporter.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.transporter.Model.Transporter.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.adminEmail)) {
        this.adminEmail = data().deepCopy(fields()[0].schema(), other.adminEmail);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groups)) {
        this.groups = data().deepCopy(fields()[1].schema(), other.groups);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing Transporter instance */
    private Builder(com.transporter.Model.Transporter other) {
            super(com.transporter.Model.Transporter.SCHEMA$);
      if (isValidValue(fields()[0], other.adminEmail)) {
        this.adminEmail = data().deepCopy(fields()[0].schema(), other.adminEmail);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groups)) {
        this.groups = data().deepCopy(fields()[1].schema(), other.groups);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'adminEmail' field */
    public java.util.List<java.lang.String> getAdminEmail() {
      return adminEmail;
    }
    
    /** Sets the value of the 'adminEmail' field */
    public com.transporter.Model.Transporter.Builder setAdminEmail(java.util.List<java.lang.String> value) {
      validate(fields()[0], value);
      this.adminEmail = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'adminEmail' field has been set */
    public boolean hasAdminEmail() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'adminEmail' field */
    public com.transporter.Model.Transporter.Builder clearAdminEmail() {
      adminEmail = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'groups' field */
    public java.util.List<com.transporter.Model.Groups> getGroups() {
      return groups;
    }
    
    /** Sets the value of the 'groups' field */
    public com.transporter.Model.Transporter.Builder setGroups(java.util.List<com.transporter.Model.Groups> value) {
      validate(fields()[1], value);
      this.groups = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'groups' field has been set */
    public boolean hasGroups() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'groups' field */
    public com.transporter.Model.Transporter.Builder clearGroups() {
      groups = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public Transporter build() {
      try {
        Transporter record = new Transporter();
        record.adminEmail = fieldSetFlags()[0] ? this.adminEmail : (java.util.List<java.lang.String>) defaultValue(fields()[0]);
        record.groups = fieldSetFlags()[1] ? this.groups : (java.util.List<com.transporter.Model.Groups>) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
