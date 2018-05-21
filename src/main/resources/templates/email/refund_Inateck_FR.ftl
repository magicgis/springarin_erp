<div style=" font-size:18px; font-family:Arial;">
<p>
Attestation de la réception d’un bien ayant fait l’objet d’une livraison intracommunau- taire dans un autre Etat membre de l’UE (attestation de réception)
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
 <p>(nom et adresse du destinataire de la livraison intracommunautaire, adresse e-mail si disponible)</p>
</div>



<div style=" font-size:13px; font-family:Arial;">
<p>
J’atteste par les présentes en qualité de destinataire que j’ai reçu1) le bien suivant / que le bien suivant ayant fait l’objet d’une livraison intracommunautaire est parvenu1)   
</p> 
</div>
<br/>

<#list order.items as item>
    ${item.quantityShipped} unités de ${item.name}<br/>
</#list>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(quantité du bien ayant fait l’objet de la livraison)</p>
</div>
<br/>

<#list order.items as item>
    <#if titleMap[item.name]??><br/>${item.name}:${titleMap[item.name]}</#if>
</#list>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(appellation commerciale ; pour les véhicules : en plus : numéro d'identification du véhicule)</p>
</div>
<br/>
en
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(le mois et l’année de la réception du bien objet de la livraison dans l’Etat membre dans lequel il est parvenu, lorsque l’entreprise qui a effectué la livraison a transporté ou expédié le bien objet de la livraison ou lorsque le destinataire a expédié le bien objet de la livraison)</p>
</div>
<br/>

${lastUpdateDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(le mois et l’année de la fin du transport lorsque le destinataire a lui-même transporté le bien objet de la livraison)</p>
</div>
a1) 
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
 <p>Etat membre et lieu où le bien objet de la livraison est parvenu dans le cadre d’un transport ou d’une expédition)</p>
</div>
<br/>

${sendDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(date d’établissement de l’attestation)</p>
</div>
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(signature du destinataire ou de son représentant et nom du soussigné en majuscules d’imprimerie)</p>
</div>
<br/>
<div style=" font-size:10px; font-family:Arial;">
 <p>1) Rayer la mention inutile</p>
</div>
