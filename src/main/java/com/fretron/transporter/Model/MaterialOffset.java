/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.transporter.Model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class MaterialOffset extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"MaterialOffset\",\"namespace\":\"com.fretron.transporter.Model\",\"fields\":[{\"name\":\"material\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"materialOffset\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.String material;
  @Deprecated public int materialOffset;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public MaterialOffset() {}

  /**
   * All-args constructor.
   */
  public MaterialOffset(java.lang.String material, java.lang.Integer materialOffset) {
    this.material = material;
    this.materialOffset = materialOffset;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return material;
    case 1: return materialOffset;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: material = (java.lang.String)value$; break;
    case 1: materialOffset = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'material' field.
   */
  public java.lang.String getMaterial() {
    return material;
  }

  /**
   * Sets the value of the 'material' field.
   * @param value the value to set.
   */
  public void setMaterial(java.lang.String value) {
    this.material = value;
  }

  /**
   * Gets the value of the 'materialOffset' field.
   */
  public java.lang.Integer getMaterialOffset() {
    return materialOffset;
  }

  /**
   * Sets the value of the 'materialOffset' field.
   * @param value the value to set.
   */
  public void setMaterialOffset(java.lang.Integer value) {
    this.materialOffset = value;
  }

  /** Creates a new MaterialOffset RecordBuilder */
  public static com.fretron.transporter.Model.MaterialOffset.Builder newBuilder() {
    return new com.fretron.transporter.Model.MaterialOffset.Builder();
  }
  
  /** Creates a new MaterialOffset RecordBuilder by copying an existing Builder */
  public static com.fretron.transporter.Model.MaterialOffset.Builder newBuilder(com.fretron.transporter.Model.MaterialOffset.Builder other) {
    return new com.fretron.transporter.Model.MaterialOffset.Builder(other);
  }
  
  /** Creates a new MaterialOffset RecordBuilder by copying an existing MaterialOffset instance */
  public static com.fretron.transporter.Model.MaterialOffset.Builder newBuilder(com.fretron.transporter.Model.MaterialOffset other) {
    return new com.fretron.transporter.Model.MaterialOffset.Builder(other);
  }
  
  /**
   * RecordBuilder for MaterialOffset instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<MaterialOffset>
    implements org.apache.avro.data.RecordBuilder<MaterialOffset> {

    private java.lang.String material;
    private int materialOffset;

    /** Creates a new Builder */
    private Builder() {
      super(com.fretron.transporter.Model.MaterialOffset.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.fretron.transporter.Model.MaterialOffset.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.material)) {
        this.material = data().deepCopy(fields()[0].schema(), other.material);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.materialOffset)) {
        this.materialOffset = data().deepCopy(fields()[1].schema(), other.materialOffset);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing MaterialOffset instance */
    private Builder(com.fretron.transporter.Model.MaterialOffset other) {
            super(com.fretron.transporter.Model.MaterialOffset.SCHEMA$);
      if (isValidValue(fields()[0], other.material)) {
        this.material = data().deepCopy(fields()[0].schema(), other.material);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.materialOffset)) {
        this.materialOffset = data().deepCopy(fields()[1].schema(), other.materialOffset);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'material' field */
    public java.lang.String getMaterial() {
      return material;
    }
    
    /** Sets the value of the 'material' field */
    public com.fretron.transporter.Model.MaterialOffset.Builder setMaterial(java.lang.String value) {
      validate(fields()[0], value);
      this.material = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'material' field has been set */
    public boolean hasMaterial() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'material' field */
    public com.fretron.transporter.Model.MaterialOffset.Builder clearMaterial() {
      material = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'materialOffset' field */
    public java.lang.Integer getMaterialOffset() {
      return materialOffset;
    }
    
    /** Sets the value of the 'materialOffset' field */
    public com.fretron.transporter.Model.MaterialOffset.Builder setMaterialOffset(int value) {
      validate(fields()[1], value);
      this.materialOffset = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'materialOffset' field has been set */
    public boolean hasMaterialOffset() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'materialOffset' field */
    public com.fretron.transporter.Model.MaterialOffset.Builder clearMaterialOffset() {
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public MaterialOffset build() {
      try {
        MaterialOffset record = new MaterialOffset();
        record.material = fieldSetFlags()[0] ? this.material : (java.lang.String) defaultValue(fields()[0]);
        record.materialOffset = fieldSetFlags()[1] ? this.materialOffset : (java.lang.Integer) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
