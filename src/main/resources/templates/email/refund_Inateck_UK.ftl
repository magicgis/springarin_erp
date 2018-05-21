<div style=" font-size:18px; font-family:Arial;">
<p>
Certification of the entry of the object of an intra-Community supply into another EU Member State (Entry Certificate)
</p> 
</div>
<br/>

<span style="font-size:15px; font-family:Arial;">


<#if order.invoiceAddress??>
	${order.invoiceAddress.name}
	<#if order.invoiceAddress.addressLine1??><br/>${order.invoiceAddress.addressLine1}</#if>
	<#if order.invoiceAddress.addressLine2??><br/>${order.invoiceAddress.addressLine2}</#if>
	<#if order.invoiceAddress.addressLine3??><br/>${order.invoiceAddress.addressLine3}</#if>
	<#if order.invoiceAddress.postalCode??><br/>${order.invoiceAddress.postalCode}</#if>
	<#if order.invoiceAddress.city??><br/>${order.invoiceAddress.city}   </#if>
	<#if order.invoiceAddress.stateOrRegion??><br/>${order.invoiceAddress.stateOrRegion}   </#if>
	<#if order.invoiceAddress.countryCode??><br/>${order.invoiceAddress.countryCode}</#if>
<#else>
	${order.shippingAddress.name}
	<#if order.shippingAddress.addressLine1??><br/>${order.shippingAddress.addressLine1}</#if>
	<#if order.shippingAddress.addressLine2??><br/>${order.shippingAddress.addressLine2}</#if>
	<#if order.shippingAddress.addressLine3??><br/>${order.shippingAddress.addressLine3}</#if>
	<#if order.shippingAddress.postalCode??><br/>${order.shippingAddress.postalCode}</#if>
	<#if order.shippingAddress.city??><br/>${order.shippingAddress.city}   </#if>
	<#if order.shippingAddress.stateOrRegion??><br/>${order.shippingAddress.stateOrRegion}   </#if>
	<#if order.shippingAddress.countryCode??><br/>${order.shippingAddress.countryCode}</#if>		 		 
</#if>	

<#if order.buyerEmail??><br/>${order.buyerEmail}</#if>
</span>
<hr style="height:1px;border:none;border-top:1px double black;" />

<div style=" font-size:10px; font-family:Arial;">
 <p>(Name and address of the customer of the intra-Community supply, e-mail address if applicable )</p>
</div>

<div style=" font-size:13px; font-family:Arial;">
<p>
I as the customer hereby certify my receipt / the entry1) of the following object of an intra-Community supply
</p> 
</div>
<br/>
<#list order.items as item>
    ${item.quantityShipped} pieces of ${item.name}<br/>
</#list>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Quantity of the object of the supply)</p>
</div>
<br/>

<#list order.items as item>
    <#if titleMap[item.name]??><br/>${item.name}:${titleMap[item.name]}</#if>
</#list>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Standard commercial description – in the case of vehicles, including vehicle identification number)</p>
</div>
<br/>
in
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Month and year the object of the supply was received in the Member State of entry if the supplying trader transported or dispatched the object of the supply or if the customer dispatched the object of the supply)</p>
</div>
<br/>

${lastUpdateDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Month and year the transportation ended if the customer transported the object of the supply himself or herself)</p>
</div>
In / at1)
<br/>

<#if order.invoiceAddress??>
	<#if order.invoiceAddress.city??><br/>${order.invoiceAddress.city},</#if>
    <#if order.invoiceAddress.stateOrRegion??>${order.invoiceAddress.stateOrRegion}   </#if>
<#else>
	<#if order.shippingAddress.city??><br/>${order.shippingAddress.city},</#if>
    <#if order.shippingAddress.stateOrRegion??>${order.shippingAddress.stateOrRegion}   </#if>	 		 
</#if>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Member State and place of entry as part of the transport or dispatch of the object)</p>
</div>
<br/>

${sendDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Date of issue of the certificate)</p>
</div>
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Signature of the customer or of the authorised representative as well as the signatory’s name in capitals)</p>
</div>
<br/>
<div style=" font-size:10px; font-family:Arial;">
 <p>1)Delete as appropriate</p>
</div>
