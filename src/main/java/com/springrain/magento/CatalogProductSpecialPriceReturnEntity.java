/**
 * CatalogProductSpecialPriceReturnEntity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.springrain.magento;

public class CatalogProductSpecialPriceReturnEntity  implements java.io.Serializable {
    private java.lang.String special_price;

    private java.lang.String special_from_date;

    private java.lang.String special_to_date;

    public CatalogProductSpecialPriceReturnEntity() {
    }

    public CatalogProductSpecialPriceReturnEntity(
           java.lang.String special_price,
           java.lang.String special_from_date,
           java.lang.String special_to_date) {
           this.special_price = special_price;
           this.special_from_date = special_from_date;
           this.special_to_date = special_to_date;
    }


    /**
     * Gets the special_price value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @return special_price
     */
    public java.lang.String getSpecial_price() {
        return special_price;
    }


    /**
     * Sets the special_price value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @param special_price
     */
    public void setSpecial_price(java.lang.String special_price) {
        this.special_price = special_price;
    }


    /**
     * Gets the special_from_date value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @return special_from_date
     */
    public java.lang.String getSpecial_from_date() {
        return special_from_date;
    }


    /**
     * Sets the special_from_date value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @param special_from_date
     */
    public void setSpecial_from_date(java.lang.String special_from_date) {
        this.special_from_date = special_from_date;
    }


    /**
     * Gets the special_to_date value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @return special_to_date
     */
    public java.lang.String getSpecial_to_date() {
        return special_to_date;
    }


    /**
     * Sets the special_to_date value for this CatalogProductSpecialPriceReturnEntity.
     * 
     * @param special_to_date
     */
    public void setSpecial_to_date(java.lang.String special_to_date) {
        this.special_to_date = special_to_date;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CatalogProductSpecialPriceReturnEntity)) return false;
        CatalogProductSpecialPriceReturnEntity other = (CatalogProductSpecialPriceReturnEntity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.special_price==null && other.getSpecial_price()==null) || 
             (this.special_price!=null &&
              this.special_price.equals(other.getSpecial_price()))) &&
            ((this.special_from_date==null && other.getSpecial_from_date()==null) || 
             (this.special_from_date!=null &&
              this.special_from_date.equals(other.getSpecial_from_date()))) &&
            ((this.special_to_date==null && other.getSpecial_to_date()==null) || 
             (this.special_to_date!=null &&
              this.special_to_date.equals(other.getSpecial_to_date())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSpecial_price() != null) {
            _hashCode += getSpecial_price().hashCode();
        }
        if (getSpecial_from_date() != null) {
            _hashCode += getSpecial_from_date().hashCode();
        }
        if (getSpecial_to_date() != null) {
            _hashCode += getSpecial_to_date().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CatalogProductSpecialPriceReturnEntity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Magento", "catalogProductSpecialPriceReturnEntity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("special_price");
        elemField.setXmlName(new javax.xml.namespace.QName("", "special_price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("special_from_date");
        elemField.setXmlName(new javax.xml.namespace.QName("", "special_from_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("special_to_date");
        elemField.setXmlName(new javax.xml.namespace.QName("", "special_to_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
