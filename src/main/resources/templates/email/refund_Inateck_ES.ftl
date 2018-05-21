<div style=" font-size:18px; font-family:Arial;">
<p>
Confirmación de la recepción del objeto de una entrega intracomunitaria en otro Estado miembro de la UE 
(Gelangensbestätigung)
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
 <p>(Nombre y dirección del comprador de la entrega intracomunitaria, eventualmente su dirección de correo electrónico)</p>
</div>



<div style=" font-size:13px; font-family:Arial;">
<p>
Por la presente certifico, en calidad de comprador, que he recibido el siguiente objeto/que el siguiente objeto de una compra intracomunitaria ha llegado1)
</p> 
</div>
<br/>

<#list order.items as item>
    ${item.quantityShipped} piezas de ${item.name}<br/>
</#list>

<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Cantidad del objeto de la entrega)</p>
</div>
<br/>

<#list order.items as item>
    <#if titleMap[item.name]??><br/>${item.name}:${titleMap[item.name]}</#if>
</#list>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Denominación comercial habitual. Si se trata de un vehículo, adicionamente el número de identificación del vehículo)</p>
</div>
<br/>
en el mes/año
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Mes y año de recepción del objeto de la entrega en el Estado miembro al cual el objeto de la entrega llegó, cuando la empresa
suministradora ha transportado o despachado el objeto de la entrega, o cuando el comprador despachó el objeto de la
entrega)
</p>
</div>
<br/>

${lastUpdateDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(mes y año de finalización del transporte cuando el destinatario transportó el objeto de la entrega por si mismo)</p>
</div>
en / hacia1) 
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
 <p> ( País y el lugar a donde el objeto de la entrega llegó como parte de un transporte o de un envío )
</p>
</div>
<br/>

${sendDate}
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p>(Fecha de emisión de la confirmación)</p>
</div>
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<br/>
<hr style="height:1px;border:none;border-top:1px double black;" />
<div style=" font-size:10px; font-family:Arial;">
 <p> (Firma del comprador o de su representante autorizado1) junto al nombre del firmante en letra de imprenta)</p>
</div>
<br/>
<div style=" font-size:10px; font-family:Arial;">
 <p>1)Tachar lo que no interesa</p>
</div>
