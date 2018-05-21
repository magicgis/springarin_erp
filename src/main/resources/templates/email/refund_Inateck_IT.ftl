<div style=" font-size:18px; font-family:Arial;">
<p>
Conferma di ricezione di merci oggetto di una cessione intracomunitaria
Fornitura di un altro Stato membro dell'UE
(Gelangenbestätigung)
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
 <p>(Nome e indirizzo del ricevente della cessione intracomunitaria, possibilmente l'indirizzo e-mail) </p>
</div>



<div style=" font-size:13px; font-family:Arial;">
<p>
Certifico, in qualità di acquirente, che la seguente merce relativa ad una cessione intracomunitaria è stata ricevuta / è stata consegnata1) 
</p> 
</div>
<br/>

<#list order.items as item>
    ${item.quantityShipped} pezzi di ${item.name} <br/>
</#list>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Quantità dell'oggetto della fornitura) </p>
</div>
<br/>

<#list order.items as item>
    <#if titleMap[item.name]??><br/>${item.name}:${titleMap[item.name]}</#if>
</#list>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(nome commerciale. In caso di veicoli, indicare anche il numero di identificazione del veicolo) </p>
</div>
<br/>
nel mese/anno 
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p> (Mese e anno di ricevimento della merce consegnata nello Stato membro, se la fornitura è stata trasportata a cura della ditta che ha trasportato o inviato l'oggetto della fornitura o nel caso in cui il cliente ha curato il trasporto della merce in consegna) </p>
</div>
<br/>

${lastUpdateDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Mese e anno di fine trasporto se il destinatario ha esso stesso curato la consegna della merce)</p>
</div>
a / fino a1) 
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
 <p>(Stato membro e luogo in cui l'oggetto della fornitura è arrivato come parte di un trasporto o di spedizione)</p>
</div>
<br/>

${sendDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Data di rilascio della presente dichiarazione) </p>
</div>
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p> (Firma dell'acquirente o del suo mandatario e il nome del firmatario stampatello) 1) </p>
</div>
<br/>
<div style=" font-size:10px; font-family:Arial;">
 <p>1) cancellare la voce che non interessa</p>
</div>
