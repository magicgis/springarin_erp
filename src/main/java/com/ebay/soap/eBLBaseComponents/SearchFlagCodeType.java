
package com.ebay.soap.eBLBaseComponents;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchFlagCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SearchFlagCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="Charity"/>
 *     &lt;enumeration value="Gift"/>
 *     &lt;enumeration value="NowAndNew"/>
 *     &lt;enumeration value="LocalSearch"/>
 *     &lt;enumeration value="FreeShipping"/>
 *     &lt;enumeration value="Gallery"/>
 *     &lt;enumeration value="Picture"/>
 *     &lt;enumeration value="GetItFast"/>
 *     &lt;enumeration value="Lot"/>
 *     &lt;enumeration value="GermanMotorsSearchable"/>
 *     &lt;enumeration value="WorldOfGood"/>
 *     &lt;enumeration value="DigitalDelivery"/>
 *     &lt;enumeration value="CustomCode"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 * Note: Per JAXB standards, underscores are added to separate words in enumerations (e.g., PayPal becomes PAY_PAL).
 */
@XmlType(name = "SearchFlagCodeType")
@XmlEnum
public enum SearchFlagCodeType {


    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected the <b>eBay Giving Works</b> 
     * 						option in the Saved Search.
     * 					
     * 
     */
    @XmlEnumValue("Charity")
    CHARITY("Charity"),

    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer set a filter to only retrieve listings where the  
     * 						seller offers gift services to the buyer.
     * 						<br/><br/>
     * 						Gift services are not available on all sites (including the US). To see if a
     * 						country supports gift services, call <b>GeteBayDetails</b>, using the
     * 						appropriate eBay Site ID in the call header and 'ListingFeatureDetails' as a
     * 						<b>DetailName</b> filter in the request, and then look for a
     * 						<b>ListingFeatureDetails.GiftIcon</b> flag in the response.
     * 					
     * 
     */
    @XmlEnumValue("Gift")
    GIFT("Gift"),
    @XmlEnumValue("NowAndNew")
    NOW_AND_NEW("NowAndNew"),

    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected the <b>Local pickup</b> 
     * 						option in the Saved Search. A buyer would select the <b>Local Pickup</b> 
     * 						filter in a Saved Search to restrict retrieved listings to those that offer 'local pickup' 
     * 						as an option to buyers.
     * 					
     * 
     */
    @XmlEnumValue("LocalSearch")
    LOCAL_SEARCH("LocalSearch"),

    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected the <b>Free shipping</b> 
     * 						option in the Saved Search. A buyer would select the <b>Free shipping</b> 
     * 						filter in a Saved Search to restrict retrieved listings to those that offer a free 
     * 						shipping option to that specific buyer (the availability of free shipping may 
     * 						be dependent on the buyer's location).
     * 					
     * 
     */
    @XmlEnumValue("FreeShipping")
    FREE_SHIPPING("FreeShipping"),

    /**
     * 
     * 						
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected a filter that restricts retrieved listings
     * 						to those that have a gallery picture.
     * 					
     * 
     */
    @XmlEnumValue("Gallery")
    GALLERY("Gallery"),

    /**
     * 
     * 						
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected a filter that restricts retrieved listings
     * 						to those that have one or more pictures.
     * 					
     * 
     */
    @XmlEnumValue("Picture")
    PICTURE("Picture"),

    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected the <b>Expedited shipping</b> 
     * 						option in the Saved Search. A buyer would select the <b>Expedited shipping</b> 
     * 						filter in a Saved Search to restrict retrieved listings to those that offer "Get 
     * 						It Fast" shipping to that specific buyer (the availability of "Get It Fast" 
     * 						shipping may be dependent on the buyer's location).
     * 						<br/><br/>
     * 						To qualify as a "Get It Fast" listing, the seller must offer at least one 
     * 						expedited (overnight) shipping service to the buyer, and a handling time (also 
     * 						known as the <b>DipatchTimeMax</b> value in the Trading API) of 
     * 						0 days (zero-day handling becomes available with Seller Release 13.1) or 1 day.	
     * 					
     * 
     */
    @XmlEnumValue("GetItFast")
    GET_IT_FAST("GetItFast"),

    /**
     * 
     * 						This value being returned in the <b>FavoriteSearch.SearchFlag</b> 
     * 						field indicates that the buyer selected the <b>Items listed as lots</b> 
     * 						option in the Saved Search. A buyer would select the <b>Items listed as lots</b> 
     * 						filter in a Saved Search to restrict retrieved listings to those that are offering 
     * 						a "lot" of items in one listing. Specifically, a "lot" is defined as, "a group of
     * 						similar or identical items that are sold together to one buyer."
     * 					
     * 
     */
    @XmlEnumValue("Lot")
    LOT("Lot"),

    /**
     * 
     * 						This value is only applicable to the German site and if the user is searching for
     * 						motor vehicle listings. This value being returned in the 
     * 						<b>FavoriteSearch.SearchFlag</b> field indicates that the buyer 
     * 						selected a filter in a Saved Search to restrict retrieved eBay Germany motor 
     * 						vehicle listings that are also being displayed on the mobile.de vehicle 
     * 						marketplace.
     * 					
     * 
     */
    @XmlEnumValue("GermanMotorsSearchable")
    GERMAN_MOTORS_SEARCHABLE("GermanMotorsSearchable"),

    /**
     * 
     * 						
     * 						Returns items that are also listed on the WorldOfGood.com website. (The Item IDs
     * 						are the same on both websites.)
     * 					
     * 
     */
    @XmlEnumValue("WorldOfGood")
    WORLD_OF_GOOD("WorldOfGood"),
    @XmlEnumValue("DigitalDelivery")
    DIGITAL_DELIVERY("DigitalDelivery"),

    /**
     * 
     * 						Reserved for internal or future use.
     * 					
     * 
     */
    @XmlEnumValue("CustomCode")
    CUSTOM_CODE("CustomCode");
    private final String value;

    SearchFlagCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SearchFlagCodeType fromValue(String v) {
        for (SearchFlagCodeType c: SearchFlagCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
