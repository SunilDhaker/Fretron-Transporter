/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.transporter.Model;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Lane extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Lane\",\"namespace\":\"com.fretron.transporter.Model\",\"fields\":[{\"name\":\"transporterId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"type\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"baseOrigin\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"FreightLocation\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"geofence\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Geofence\",\"fields\":[{\"name\":\"center\",\"type\":{\"type\":\"record\",\"name\":\"Location\",\"fields\":[{\"name\":\"latitude\",\"type\":\"double\"},{\"name\":\"longitude\",\"type\":\"double\"}]}},{\"name\":\"boundary\",\"type\":{\"type\":\"array\",\"items\":\"Location\"}}]}],\"default\":null},{\"name\":\"material\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null}]}],\"default\":null},{\"name\":\"baseDestination\",\"type\":[\"null\",\"FreightLocation\"],\"default\":null},{\"name\":\"basePrice\",\"type\":[\"null\",\"double\"]},{\"name\":\"baseMaterial\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"consigner\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"FreightLocationOffset\",\"fields\":[{\"name\":\"freightLocation\",\"type\":\"FreightLocation\"},{\"name\":\"priceOffset\",\"type\":\"int\"},{\"name\":\"etdOffset\",\"type\":\"int\"}]}],\"default\":null},{\"name\":\"consignee\",\"type\":[\"null\",\"FreightLocationOffset\"],\"default\":null},{\"name\":\"material\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"MaterialOffset\",\"fields\":[{\"name\":\"material\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"materialOffset\",\"type\":\"int\"}]}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.String transporterId;
  @Deprecated public java.lang.String type;
  @Deprecated public com.fretron.transporter.Model.FreightLocation baseOrigin;
  @Deprecated public com.fretron.transporter.Model.FreightLocation baseDestination;
  @Deprecated public java.lang.Double basePrice;
  @Deprecated public java.lang.String baseMaterial;
  @Deprecated public com.fretron.transporter.Model.FreightLocationOffset consigner;
  @Deprecated public com.fretron.transporter.Model.FreightLocationOffset consignee;
  @Deprecated public com.fretron.transporter.Model.MaterialOffset material;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public Lane() {}

  /**
   * All-args constructor.
   */
  public Lane(java.lang.String transporterId, java.lang.String type, com.fretron.transporter.Model.FreightLocation baseOrigin, com.fretron.transporter.Model.FreightLocation baseDestination, java.lang.Double basePrice, java.lang.String baseMaterial, com.fretron.transporter.Model.FreightLocationOffset consigner, com.fretron.transporter.Model.FreightLocationOffset consignee, com.fretron.transporter.Model.MaterialOffset material) {
    this.transporterId = transporterId;
    this.type = type;
    this.baseOrigin = baseOrigin;
    this.baseDestination = baseDestination;
    this.basePrice = basePrice;
    this.baseMaterial = baseMaterial;
    this.consigner = consigner;
    this.consignee = consignee;
    this.material = material;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return transporterId;
    case 1: return type;
    case 2: return baseOrigin;
    case 3: return baseDestination;
    case 4: return basePrice;
    case 5: return baseMaterial;
    case 6: return consigner;
    case 7: return consignee;
    case 8: return material;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: transporterId = (java.lang.String)value$; break;
    case 1: type = (java.lang.String)value$; break;
    case 2: baseOrigin = (com.fretron.transporter.Model.FreightLocation)value$; break;
    case 3: baseDestination = (com.fretron.transporter.Model.FreightLocation)value$; break;
    case 4: basePrice = (java.lang.Double)value$; break;
    case 5: baseMaterial = (java.lang.String)value$; break;
    case 6: consigner = (com.fretron.transporter.Model.FreightLocationOffset)value$; break;
    case 7: consignee = (com.fretron.transporter.Model.FreightLocationOffset)value$; break;
    case 8: material = (com.fretron.transporter.Model.MaterialOffset)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
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
   * Gets the value of the 'baseOrigin' field.
   */
  public com.fretron.transporter.Model.FreightLocation getBaseOrigin() {
    return baseOrigin;
  }

  /**
   * Sets the value of the 'baseOrigin' field.
   * @param value the value to set.
   */
  public void setBaseOrigin(com.fretron.transporter.Model.FreightLocation value) {
    this.baseOrigin = value;
  }

  /**
   * Gets the value of the 'baseDestination' field.
   */
  public com.fretron.transporter.Model.FreightLocation getBaseDestination() {
    return baseDestination;
  }

  /**
   * Sets the value of the 'baseDestination' field.
   * @param value the value to set.
   */
  public void setBaseDestination(com.fretron.transporter.Model.FreightLocation value) {
    this.baseDestination = value;
  }

  /**
   * Gets the value of the 'basePrice' field.
   */
  public java.lang.Double getBasePrice() {
    return basePrice;
  }

  /**
   * Sets the value of the 'basePrice' field.
   * @param value the value to set.
   */
  public void setBasePrice(java.lang.Double value) {
    this.basePrice = value;
  }

  /**
   * Gets the value of the 'baseMaterial' field.
   */
  public java.lang.String getBaseMaterial() {
    return baseMaterial;
  }

  /**
   * Sets the value of the 'baseMaterial' field.
   * @param value the value to set.
   */
  public void setBaseMaterial(java.lang.String value) {
    this.baseMaterial = value;
  }

  /**
   * Gets the value of the 'consigner' field.
   */
  public com.fretron.transporter.Model.FreightLocationOffset getConsigner() {
    return consigner;
  }

  /**
   * Sets the value of the 'consigner' field.
   * @param value the value to set.
   */
  public void setConsigner(com.fretron.transporter.Model.FreightLocationOffset value) {
    this.consigner = value;
  }

  /**
   * Gets the value of the 'consignee' field.
   */
  public com.fretron.transporter.Model.FreightLocationOffset getConsignee() {
    return consignee;
  }

  /**
   * Sets the value of the 'consignee' field.
   * @param value the value to set.
   */
  public void setConsignee(com.fretron.transporter.Model.FreightLocationOffset value) {
    this.consignee = value;
  }

  /**
   * Gets the value of the 'material' field.
   */
  public com.fretron.transporter.Model.MaterialOffset getMaterial() {
    return material;
  }

  /**
   * Sets the value of the 'material' field.
   * @param value the value to set.
   */
  public void setMaterial(com.fretron.transporter.Model.MaterialOffset value) {
    this.material = value;
  }

  /** Creates a new Lane RecordBuilder */
  public static com.fretron.transporter.Model.Lane.Builder newBuilder() {
    return new com.fretron.transporter.Model.Lane.Builder();
  }
  
  /** Creates a new Lane RecordBuilder by copying an existing Builder */
  public static com.fretron.transporter.Model.Lane.Builder newBuilder(com.fretron.transporter.Model.Lane.Builder other) {
    return new com.fretron.transporter.Model.Lane.Builder(other);
  }
  
  /** Creates a new Lane RecordBuilder by copying an existing Lane instance */
  public static com.fretron.transporter.Model.Lane.Builder newBuilder(com.fretron.transporter.Model.Lane other) {
    return new com.fretron.transporter.Model.Lane.Builder(other);
  }
  
  /**
   * RecordBuilder for Lane instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Lane>
    implements org.apache.avro.data.RecordBuilder<Lane> {

    private java.lang.String transporterId;
    private java.lang.String type;
    private com.fretron.transporter.Model.FreightLocation baseOrigin;
    private com.fretron.transporter.Model.FreightLocation baseDestination;
    private java.lang.Double basePrice;
    private java.lang.String baseMaterial;
    private com.fretron.transporter.Model.FreightLocationOffset consigner;
    private com.fretron.transporter.Model.FreightLocationOffset consignee;
    private com.fretron.transporter.Model.MaterialOffset material;

    /** Creates a new Builder */
    private Builder() {
      super(com.fretron.transporter.Model.Lane.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(com.fretron.transporter.Model.Lane.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.transporterId)) {
        this.transporterId = data().deepCopy(fields()[0].schema(), other.transporterId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.type)) {
        this.type = data().deepCopy(fields()[1].schema(), other.type);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.baseOrigin)) {
        this.baseOrigin = data().deepCopy(fields()[2].schema(), other.baseOrigin);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.baseDestination)) {
        this.baseDestination = data().deepCopy(fields()[3].schema(), other.baseDestination);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.basePrice)) {
        this.basePrice = data().deepCopy(fields()[4].schema(), other.basePrice);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.baseMaterial)) {
        this.baseMaterial = data().deepCopy(fields()[5].schema(), other.baseMaterial);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.consigner)) {
        this.consigner = data().deepCopy(fields()[6].schema(), other.consigner);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.consignee)) {
        this.consignee = data().deepCopy(fields()[7].schema(), other.consignee);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.material)) {
        this.material = data().deepCopy(fields()[8].schema(), other.material);
        fieldSetFlags()[8] = true;
      }
    }
    
    /** Creates a Builder by copying an existing Lane instance */
    private Builder(com.fretron.transporter.Model.Lane other) {
            super(com.fretron.transporter.Model.Lane.SCHEMA$);
      if (isValidValue(fields()[0], other.transporterId)) {
        this.transporterId = data().deepCopy(fields()[0].schema(), other.transporterId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.type)) {
        this.type = data().deepCopy(fields()[1].schema(), other.type);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.baseOrigin)) {
        this.baseOrigin = data().deepCopy(fields()[2].schema(), other.baseOrigin);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.baseDestination)) {
        this.baseDestination = data().deepCopy(fields()[3].schema(), other.baseDestination);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.basePrice)) {
        this.basePrice = data().deepCopy(fields()[4].schema(), other.basePrice);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.baseMaterial)) {
        this.baseMaterial = data().deepCopy(fields()[5].schema(), other.baseMaterial);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.consigner)) {
        this.consigner = data().deepCopy(fields()[6].schema(), other.consigner);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.consignee)) {
        this.consignee = data().deepCopy(fields()[7].schema(), other.consignee);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.material)) {
        this.material = data().deepCopy(fields()[8].schema(), other.material);
        fieldSetFlags()[8] = true;
      }
    }

    /** Gets the value of the 'transporterId' field */
    public java.lang.String getTransporterId() {
      return transporterId;
    }
    
    /** Sets the value of the 'transporterId' field */
    public com.fretron.transporter.Model.Lane.Builder setTransporterId(java.lang.String value) {
      validate(fields()[0], value);
      this.transporterId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'transporterId' field has been set */
    public boolean hasTransporterId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'transporterId' field */
    public com.fretron.transporter.Model.Lane.Builder clearTransporterId() {
      transporterId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'type' field */
    public java.lang.String getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public com.fretron.transporter.Model.Lane.Builder setType(java.lang.String value) {
      validate(fields()[1], value);
      this.type = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'type' field */
    public com.fretron.transporter.Model.Lane.Builder clearType() {
      type = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'baseOrigin' field */
    public com.fretron.transporter.Model.FreightLocation getBaseOrigin() {
      return baseOrigin;
    }
    
    /** Sets the value of the 'baseOrigin' field */
    public com.fretron.transporter.Model.Lane.Builder setBaseOrigin(com.fretron.transporter.Model.FreightLocation value) {
      validate(fields()[2], value);
      this.baseOrigin = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'baseOrigin' field has been set */
    public boolean hasBaseOrigin() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'baseOrigin' field */
    public com.fretron.transporter.Model.Lane.Builder clearBaseOrigin() {
      baseOrigin = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'baseDestination' field */
    public com.fretron.transporter.Model.FreightLocation getBaseDestination() {
      return baseDestination;
    }
    
    /** Sets the value of the 'baseDestination' field */
    public com.fretron.transporter.Model.Lane.Builder setBaseDestination(com.fretron.transporter.Model.FreightLocation value) {
      validate(fields()[3], value);
      this.baseDestination = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'baseDestination' field has been set */
    public boolean hasBaseDestination() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'baseDestination' field */
    public com.fretron.transporter.Model.Lane.Builder clearBaseDestination() {
      baseDestination = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'basePrice' field */
    public java.lang.Double getBasePrice() {
      return basePrice;
    }
    
    /** Sets the value of the 'basePrice' field */
    public com.fretron.transporter.Model.Lane.Builder setBasePrice(java.lang.Double value) {
      validate(fields()[4], value);
      this.basePrice = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'basePrice' field has been set */
    public boolean hasBasePrice() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'basePrice' field */
    public com.fretron.transporter.Model.Lane.Builder clearBasePrice() {
      basePrice = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'baseMaterial' field */
    public java.lang.String getBaseMaterial() {
      return baseMaterial;
    }
    
    /** Sets the value of the 'baseMaterial' field */
    public com.fretron.transporter.Model.Lane.Builder setBaseMaterial(java.lang.String value) {
      validate(fields()[5], value);
      this.baseMaterial = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'baseMaterial' field has been set */
    public boolean hasBaseMaterial() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'baseMaterial' field */
    public com.fretron.transporter.Model.Lane.Builder clearBaseMaterial() {
      baseMaterial = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'consigner' field */
    public com.fretron.transporter.Model.FreightLocationOffset getConsigner() {
      return consigner;
    }
    
    /** Sets the value of the 'consigner' field */
    public com.fretron.transporter.Model.Lane.Builder setConsigner(com.fretron.transporter.Model.FreightLocationOffset value) {
      validate(fields()[6], value);
      this.consigner = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'consigner' field has been set */
    public boolean hasConsigner() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'consigner' field */
    public com.fretron.transporter.Model.Lane.Builder clearConsigner() {
      consigner = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /** Gets the value of the 'consignee' field */
    public com.fretron.transporter.Model.FreightLocationOffset getConsignee() {
      return consignee;
    }
    
    /** Sets the value of the 'consignee' field */
    public com.fretron.transporter.Model.Lane.Builder setConsignee(com.fretron.transporter.Model.FreightLocationOffset value) {
      validate(fields()[7], value);
      this.consignee = value;
      fieldSetFlags()[7] = true;
      return this; 
    }
    
    /** Checks whether the 'consignee' field has been set */
    public boolean hasConsignee() {
      return fieldSetFlags()[7];
    }
    
    /** Clears the value of the 'consignee' field */
    public com.fretron.transporter.Model.Lane.Builder clearConsignee() {
      consignee = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /** Gets the value of the 'material' field */
    public com.fretron.transporter.Model.MaterialOffset getMaterial() {
      return material;
    }
    
    /** Sets the value of the 'material' field */
    public com.fretron.transporter.Model.Lane.Builder setMaterial(com.fretron.transporter.Model.MaterialOffset value) {
      validate(fields()[8], value);
      this.material = value;
      fieldSetFlags()[8] = true;
      return this; 
    }
    
    /** Checks whether the 'material' field has been set */
    public boolean hasMaterial() {
      return fieldSetFlags()[8];
    }
    
    /** Clears the value of the 'material' field */
    public com.fretron.transporter.Model.Lane.Builder clearMaterial() {
      material = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    @Override
    public Lane build() {
      try {
        Lane record = new Lane();
        record.transporterId = fieldSetFlags()[0] ? this.transporterId : (java.lang.String) defaultValue(fields()[0]);
        record.type = fieldSetFlags()[1] ? this.type : (java.lang.String) defaultValue(fields()[1]);
        record.baseOrigin = fieldSetFlags()[2] ? this.baseOrigin : (com.fretron.transporter.Model.FreightLocation) defaultValue(fields()[2]);
        record.baseDestination = fieldSetFlags()[3] ? this.baseDestination : (com.fretron.transporter.Model.FreightLocation) defaultValue(fields()[3]);
        record.basePrice = fieldSetFlags()[4] ? this.basePrice : (java.lang.Double) defaultValue(fields()[4]);
        record.baseMaterial = fieldSetFlags()[5] ? this.baseMaterial : (java.lang.String) defaultValue(fields()[5]);
        record.consigner = fieldSetFlags()[6] ? this.consigner : (com.fretron.transporter.Model.FreightLocationOffset) defaultValue(fields()[6]);
        record.consignee = fieldSetFlags()[7] ? this.consignee : (com.fretron.transporter.Model.FreightLocationOffset) defaultValue(fields()[7]);
        record.material = fieldSetFlags()[8] ? this.material : (com.fretron.transporter.Model.MaterialOffset) defaultValue(fields()[8]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
