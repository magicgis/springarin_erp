
package com.ebay.soap.eBLBaseComponents;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CheckoutMethodCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CheckoutMethodCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="ThirdPartyCheckout"/>
 *     &lt;enumeration value="CustomCode"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 * Note: Per JAXB standards, underscores are added to separate words in enumerations (e.g., PayPal becomes PAY_PAL).
 */
@XmlType(name = "CheckoutMethodCodeType")
@XmlEnum
public enum CheckoutMethodCodeType {


    /**
     * 
     * 						
     * 						Other Checkout Method was used.
     * 					
     * 
     */
    @XmlEnumValue("Other")
    OTHER("Other"),

    /**
     * 
     * 						
     * 						As of July 1, 2011, third-party applications can no longer be used for
     * 						checkout. All purchased items must go through the eBay Checkout flow.
     * 					
     * 
     */
    @XmlEnumValue("ThirdPartyCheckout")
    THIRD_PARTY_CHECKOUT("ThirdPartyCheckout"),

    /**
     * 
     * 						
     * 						(out) Reserved for internal or future use.
     * 					
     * 
     */
    @XmlEnumValue("CustomCode")
    CUSTOM_CODE("CustomCode");
    private final String value;

    CheckoutMethodCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CheckoutMethodCodeType fromValue(String v) {
        for (CheckoutMethodCodeType c: CheckoutMethodCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
