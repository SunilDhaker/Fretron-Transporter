/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.Model;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class FreightLocation extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"FreightLocation\",\"namespace\":\"com.fretron.Model\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"geofence\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Geofence\",\"fields\":[{\"name\":\"center\",\"type\":{\"type\":\"record\",\"name\":\"Location\",\"fields\":[{\"name\":\"latitude\",\"type\":\"double\"},{\"name\":\"longitude\",\"type\":\"double\"}]}},{\"name\":\"boundary\",\"type\":{\"type\":\"array\",\"items\":\"Location\"}}]}],\"default\":null},{\"name\":\"material\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null}]}");
    @Deprecated
    public java.lang.String name;
    @Deprecated
    public com.fretron.Model.Geofence geofence;
    @Deprecated
    public java.util.List<java.lang.String> material;
    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public FreightLocation() {
    }

    /**
     * All-args constructor.
     */
    public FreightLocation(java.lang.String name, com.fretron.Model.Geofence geofence, java.util.List<java.lang.String> material) {
        this.name = name;
        this.geofence = geofence;
        this.material = material;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /** Creates a new FreightLocation RecordBuilder */
    public static com.fretron.Model.FreightLocation.Builder newBuilder() {
        return new com.fretron.Model.FreightLocation.Builder();
    }

    /** Creates a new FreightLocation RecordBuilder by copying an existing Builder */
    public static com.fretron.Model.FreightLocation.Builder newBuilder(com.fretron.Model.FreightLocation.Builder other) {
        return new com.fretron.Model.FreightLocation.Builder(other);
    }

    /** Creates a new FreightLocation RecordBuilder by copying an existing FreightLocation instance */
    public static com.fretron.Model.FreightLocation.Builder newBuilder(com.fretron.Model.FreightLocation other) {
        return new com.fretron.Model.FreightLocation.Builder(other);
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return name;
            case 1:
                return geofence;
            case 2:
                return material;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                name = (java.lang.String) value$;
                break;
            case 1:
                geofence = (com.fretron.Model.Geofence) value$;
                break;
            case 2:
                material = (java.util.List<java.lang.String>) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
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
     * Gets the value of the 'geofence' field.
     */
    public com.fretron.Model.Geofence getGeofence() {
        return geofence;
    }

    /**
     * Sets the value of the 'geofence' field.
     * @param value the value to set.
     */
    public void setGeofence(com.fretron.Model.Geofence value) {
        this.geofence = value;
    }

    /**
     * Gets the value of the 'material' field.
     */
    public java.util.List<java.lang.String> getMaterial() {
        return material;
    }

    /**
     * Sets the value of the 'material' field.
     * @param value the value to set.
     */
    public void setMaterial(java.util.List<java.lang.String> value) {
        this.material = value;
    }

    /**
     * RecordBuilder for FreightLocation instances.
     */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<FreightLocation>
            implements org.apache.avro.data.RecordBuilder<FreightLocation> {

        private java.lang.String name;
        private com.fretron.Model.Geofence geofence;
        private java.util.List<java.lang.String> material;

        /** Creates a new Builder */
        private Builder() {
            super(com.fretron.Model.FreightLocation.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(com.fretron.Model.FreightLocation.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.name)) {
                this.name = data().deepCopy(fields()[0].schema(), other.name);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.geofence)) {
                this.geofence = data().deepCopy(fields()[1].schema(), other.geofence);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.material)) {
                this.material = data().deepCopy(fields()[2].schema(), other.material);
                fieldSetFlags()[2] = true;
            }
        }

        /** Creates a Builder by copying an existing FreightLocation instance */
        private Builder(com.fretron.Model.FreightLocation other) {
            super(com.fretron.Model.FreightLocation.SCHEMA$);
            if (isValidValue(fields()[0], other.name)) {
                this.name = data().deepCopy(fields()[0].schema(), other.name);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.geofence)) {
                this.geofence = data().deepCopy(fields()[1].schema(), other.geofence);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.material)) {
                this.material = data().deepCopy(fields()[2].schema(), other.material);
                fieldSetFlags()[2] = true;
            }
        }

        /** Gets the value of the 'name' field */
        public java.lang.String getName() {
            return name;
        }

        /** Sets the value of the 'name' field */
        public com.fretron.Model.FreightLocation.Builder setName(java.lang.String value) {
            validate(fields()[0], value);
            this.name = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'name' field has been set */
        public boolean hasName() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'name' field */
        public com.fretron.Model.FreightLocation.Builder clearName() {
            name = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /** Gets the value of the 'geofence' field */
        public com.fretron.Model.Geofence getGeofence() {
            return geofence;
        }

        /** Sets the value of the 'geofence' field */
        public com.fretron.Model.FreightLocation.Builder setGeofence(com.fretron.Model.Geofence value) {
            validate(fields()[1], value);
            this.geofence = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /** Checks whether the 'geofence' field has been set */
        public boolean hasGeofence() {
            return fieldSetFlags()[1];
        }

        /** Clears the value of the 'geofence' field */
        public com.fretron.Model.FreightLocation.Builder clearGeofence() {
            geofence = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /** Gets the value of the 'material' field */
        public java.util.List<java.lang.String> getMaterial() {
            return material;
        }

        /** Sets the value of the 'material' field */
        public com.fretron.Model.FreightLocation.Builder setMaterial(java.util.List<java.lang.String> value) {
            validate(fields()[2], value);
            this.material = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /** Checks whether the 'material' field has been set */
        public boolean hasMaterial() {
            return fieldSetFlags()[2];
        }

        /** Clears the value of the 'material' field */
        public com.fretron.Model.FreightLocation.Builder clearMaterial() {
            material = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        public FreightLocation build() {
            try {
                FreightLocation record = new FreightLocation();
                record.name = fieldSetFlags()[0] ? this.name : (java.lang.String) defaultValue(fields()[0]);
                record.geofence = fieldSetFlags()[1] ? this.geofence : (com.fretron.Model.Geofence) defaultValue(fields()[1]);
                record.material = fieldSetFlags()[2] ? this.material : (java.util.List<java.lang.String>) defaultValue(fields()[2]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
