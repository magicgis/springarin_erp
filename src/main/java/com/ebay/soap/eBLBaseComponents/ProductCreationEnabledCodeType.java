
package com.ebay.soap.eBLBaseComponents;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductCreationEnabledCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProductCreationEnabledCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Disabled"/>
 *     &lt;enumeration value="Enabled"/>
 *     &lt;enumeration value="Required"/>
 *     &lt;enumeration value="CustomCode"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 * Note: Per JAXB standards, underscores are added to separate words in enumerations (e.g., PayPal becomes PAY_PAL).
 */
@XmlType(name = "ProductCreationEnabledCodeType")
@XmlEnum
public enum ProductCreationEnabledCodeType {


    /**
     * 
     * 						Product creation is not supported for the category. 
     * 						AddItem family calls can still list with product.
     * 					
     * 
     */
    @XmlEnumValue("Disabled")
    DISABLED("Disabled"),

    /**
     * 
     * 						Product creation is supported for the category. AddItem and related listing
     * 						calls can list with a product from the eBay catalog, but it is not mandatory.
     * 						Currently, unless listing with a product is required, the capability to add
     * 						products to eBay catalogs is only available to a select set of users
     * 						identified by eBay.
     * 					
     * 
     */
    @XmlEnumValue("Enabled")
    ENABLED("Enabled"),

    /**
     * 
     * 						Product creation is supported for the category and listing with a product is
     * 						required. If there is no matching product in the eBay catalog for the item,
     * 						the seller must add the product to the catalog before listing the item. For
     * 						categories that require listing with a product, any seller will be allowed to
     * 						add a product. Product creation will be supported in the API and other tools.
     * 					
     * 
     */
    @XmlEnumValue("Required")
    REQUIRED("Required"),

    /**
     * 
     * 						(out) Reserved for internal or future use.
     * 				  
     * 
     */
    @XmlEnumValue("CustomCode")
    CUSTOM_CODE("CustomCode");
    private final String value;

    ProductCreationEnabledCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProductCreationEnabledCodeType fromValue(String v) {
        for (ProductCreationEnabledCodeType c: ProductCreationEnabledCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
