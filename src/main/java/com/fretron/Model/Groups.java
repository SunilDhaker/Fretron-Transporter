/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package com.fretron.Model;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Groups extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Groups\",\"namespace\":\"com.fretron.Model\",\"fields\":[{\"name\":\"groupId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"subgroups\",\"type\":{\"type\":\"array\",\"items\":\"Groups\"}},{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"admin\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"members\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"default\":null}]}");
    @Deprecated
    public java.lang.String groupId;
    @Deprecated
    public java.util.List<com.fretron.Model.Groups> subgroups;
    @Deprecated
    public java.lang.String name;
    @Deprecated
    public java.lang.String admin;
    @Deprecated
    public java.util.List<java.lang.String> members;
    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public Groups() {
    }

    /**
     * All-args constructor.
     */
    public Groups(java.lang.String groupId, java.util.List<com.fretron.Model.Groups> subgroups, java.lang.String name, java.lang.String admin, java.util.List<java.lang.String> members) {
        this.groupId = groupId;
        this.subgroups = subgroups;
        this.name = name;
        this.admin = admin;
        this.members = members;
    }

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    /** Creates a new Groups RecordBuilder */
    public static com.fretron.Model.Groups.Builder newBuilder() {
        return new com.fretron.Model.Groups.Builder();
    }

    /** Creates a new Groups RecordBuilder by copying an existing Builder */
    public static com.fretron.Model.Groups.Builder newBuilder(com.fretron.Model.Groups.Builder other) {
        return new com.fretron.Model.Groups.Builder(other);
    }

    /** Creates a new Groups RecordBuilder by copying an existing Groups instance */
    public static com.fretron.Model.Groups.Builder newBuilder(com.fretron.Model.Groups other) {
        return new com.fretron.Model.Groups.Builder(other);
    }

    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    public java.lang.Object get(int field$) {
        switch (field$) {
            case 0:
                return groupId;
            case 1:
                return subgroups;
            case 2:
                return name;
            case 3:
                return admin;
            case 4:
                return members;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, java.lang.Object value$) {
        switch (field$) {
            case 0:
                groupId = (java.lang.String) value$;
                break;
            case 1:
                subgroups = (java.util.List<com.fretron.Model.Groups>) value$;
                break;
            case 2:
                name = (java.lang.String) value$;
                break;
            case 3:
                admin = (java.lang.String) value$;
                break;
            case 4:
                members = (java.util.List<java.lang.String>) value$;
                break;
            default:
                throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
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
     * Gets the value of the 'subgroups' field.
     */
    public java.util.List<com.fretron.Model.Groups> getSubgroups() {
        return subgroups;
    }

    /**
     * Sets the value of the 'subgroups' field.
     * @param value the value to set.
     */
    public void setSubgroups(java.util.List<com.fretron.Model.Groups> value) {
        this.subgroups = value;
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
     * Gets the value of the 'admin' field.
     */
    public java.lang.String getAdmin() {
        return admin;
    }

    /**
     * Sets the value of the 'admin' field.
     * @param value the value to set.
     */
    public void setAdmin(java.lang.String value) {
        this.admin = value;
    }

    /**
     * Gets the value of the 'members' field.
     */
    public java.util.List<java.lang.String> getMembers() {
        return members;
    }

    /**
     * Sets the value of the 'members' field.
     * @param value the value to set.
     */
    public void setMembers(java.util.List<java.lang.String> value) {
        this.members = value;
    }

    /**
     * RecordBuilder for Groups instances.
     */
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Groups>
            implements org.apache.avro.data.RecordBuilder<Groups> {

        private java.lang.String groupId;
        private java.util.List<com.fretron.Model.Groups> subgroups;
        private java.lang.String name;
        private java.lang.String admin;
        private java.util.List<java.lang.String> members;

        /** Creates a new Builder */
        private Builder() {
            super(com.fretron.Model.Groups.SCHEMA$);
        }

        /** Creates a Builder by copying an existing Builder */
        private Builder(com.fretron.Model.Groups.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.groupId)) {
                this.groupId = data().deepCopy(fields()[0].schema(), other.groupId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.subgroups)) {
                this.subgroups = data().deepCopy(fields()[1].schema(), other.subgroups);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.name)) {
                this.name = data().deepCopy(fields()[2].schema(), other.name);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.admin)) {
                this.admin = data().deepCopy(fields()[3].schema(), other.admin);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.members)) {
                this.members = data().deepCopy(fields()[4].schema(), other.members);
                fieldSetFlags()[4] = true;
            }
        }

        /** Creates a Builder by copying an existing Groups instance */
        private Builder(com.fretron.Model.Groups other) {
            super(com.fretron.Model.Groups.SCHEMA$);
            if (isValidValue(fields()[0], other.groupId)) {
                this.groupId = data().deepCopy(fields()[0].schema(), other.groupId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.subgroups)) {
                this.subgroups = data().deepCopy(fields()[1].schema(), other.subgroups);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.name)) {
                this.name = data().deepCopy(fields()[2].schema(), other.name);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.admin)) {
                this.admin = data().deepCopy(fields()[3].schema(), other.admin);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.members)) {
                this.members = data().deepCopy(fields()[4].schema(), other.members);
                fieldSetFlags()[4] = true;
            }
        }

        /** Gets the value of the 'groupId' field */
        public java.lang.String getGroupId() {
            return groupId;
        }

        /** Sets the value of the 'groupId' field */
        public com.fretron.Model.Groups.Builder setGroupId(java.lang.String value) {
            validate(fields()[0], value);
            this.groupId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /** Checks whether the 'groupId' field has been set */
        public boolean hasGroupId() {
            return fieldSetFlags()[0];
        }

        /** Clears the value of the 'groupId' field */
        public com.fretron.Model.Groups.Builder clearGroupId() {
            groupId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /** Gets the value of the 'subgroups' field */
        public java.util.List<com.fretron.Model.Groups> getSubgroups() {
            return subgroups;
        }

        /** Sets the value of the 'subgroups' field */
        public com.fretron.Model.Groups.Builder setSubgroups(java.util.List<com.fretron.Model.Groups> value) {
            validate(fields()[1], value);
            this.subgroups = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /** Checks whether the 'subgroups' field has been set */
        public boolean hasSubgroups() {
            return fieldSetFlags()[1];
        }

        /** Clears the value of the 'subgroups' field */
        public com.fretron.Model.Groups.Builder clearSubgroups() {
            subgroups = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /** Gets the value of the 'name' field */
        public java.lang.String getName() {
            return name;
        }

        /** Sets the value of the 'name' field */
        public com.fretron.Model.Groups.Builder setName(java.lang.String value) {
            validate(fields()[2], value);
            this.name = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /** Checks whether the 'name' field has been set */
        public boolean hasName() {
            return fieldSetFlags()[2];
        }

        /** Clears the value of the 'name' field */
        public com.fretron.Model.Groups.Builder clearName() {
            name = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /** Gets the value of the 'admin' field */
        public java.lang.String getAdmin() {
            return admin;
        }

        /** Sets the value of the 'admin' field */
        public com.fretron.Model.Groups.Builder setAdmin(java.lang.String value) {
            validate(fields()[3], value);
            this.admin = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /** Checks whether the 'admin' field has been set */
        public boolean hasAdmin() {
            return fieldSetFlags()[3];
        }

        /** Clears the value of the 'admin' field */
        public com.fretron.Model.Groups.Builder clearAdmin() {
            admin = null;
            fieldSetFlags()[3] = false;
            return this;
        }

        /** Gets the value of the 'members' field */
        public java.util.List<java.lang.String> getMembers() {
            return members;
        }

        /** Sets the value of the 'members' field */
        public com.fretron.Model.Groups.Builder setMembers(java.util.List<java.lang.String> value) {
            validate(fields()[4], value);
            this.members = value;
            fieldSetFlags()[4] = true;
            return this;
        }

        /** Checks whether the 'members' field has been set */
        public boolean hasMembers() {
            return fieldSetFlags()[4];
        }

        /** Clears the value of the 'members' field */
        public com.fretron.Model.Groups.Builder clearMembers() {
            members = null;
            fieldSetFlags()[4] = false;
            return this;
        }

        @Override
        public Groups build() {
            try {
                Groups record = new Groups();
                record.groupId = fieldSetFlags()[0] ? this.groupId : (java.lang.String) defaultValue(fields()[0]);
                record.subgroups = fieldSetFlags()[1] ? this.subgroups : (java.util.List<com.fretron.Model.Groups>) defaultValue(fields()[1]);
                record.name = fieldSetFlags()[2] ? this.name : (java.lang.String) defaultValue(fields()[2]);
                record.admin = fieldSetFlags()[3] ? this.admin : (java.lang.String) defaultValue(fields()[3]);
                record.members = fieldSetFlags()[4] ? this.members : (java.util.List<java.lang.String>) defaultValue(fields()[4]);
                return record;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }
}
