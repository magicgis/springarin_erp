<div style=" font-size:18px; font-family:Arial;">
<p>
Bestätigung über das Gelangen des Gegenstandes einer innergemeinschaftlichen Lieferung in einen anderen EU-Mitgliedsstaat (Gelangensbestätigung)</p> 
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
 <p>(Name und Anschrift des Abnehmers der innergemeinschaftlichen Lieferung, ggf. E-Mail-Adresse)</p>
</div>


<div style=" font-size:13px; font-family:Arial;">
<p>
Hiermit bestätige ich als Abnehmer, dass ich folgenden Gegenstand1) / dass folgender Gegenstand1) einer innergemeinschaftlichen Lieferung
</p> 
</div>
<br/>

<#list order.items as item>
    ${item.quantityShipped} Stück ${item.name}<br/>
</#list>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Menge des Gegenstands der Lieferung)</p>
</div>
<br/>

<#list order.items as item>
    <#if titleMap[item.name]??><br/>${item.name}:${titleMap[item.name]}</#if>
</#list>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(handelsübliche Bezeichnung, bei Fahrzeugen zusätzlich die Fahrzeug-Identifikationsnummer)</p>
</div>
<br/>
Im
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Monat und Jahr des Erhalts des Liefergegenstands im Mitgliedstaat, in den der Liefergegenstand gelangt ist, wenn der liefernde Unternehmer den Liefergegenstand befördert oder versendet hat oder wenn der Abnehmer den Liefergegenstand versendet hat)</p>
</div>
<br/>

${lastUpdateDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Monat und Jahr des Endes der Beförderung, wenn der Abnehmer den Liefergegenstand selbst befördert hat)</p>
</div>
In / nach1) 
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
 <p>(Mitgliedstaat und Ort, wohin der Liefergegenstand im Rahmen einer Beförderung oder Versendung gelangt ist)</p>
</div>
erhalten habe / gelangt ist1).
<br/>

${sendDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Datum der Ausstellung der Bestätigung)</p>
</div>
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Unterschrift des Abnehmers oder seines Vertretungsberechtigten sowie Name des Unterzeichnenden in Druckschrift)</p>
</div>
<br/>
<div style=" font-size:10px; font-family:Arial;">
 <p>1) Nichtzutreffendes streichen</p>
</div>
