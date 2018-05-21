<table width="650">
<tbody>
<tr>

<td valign="top" nowrap="" width="50%"><b><img src="logo.jpg"/></b></td>
<td valign="top" nowrap="" align="right" width="50%"><span style="font-size:13px;" ><b>F&M Technology LLC<br/></b></span><br/><span style=" font-size:13px; font-family:MS Gothic;">日本<br/>〒196-0002<br/>
東京都昭島市拝島町三丁目10番3号</span></td>
</tr>
<tr>
<td nowrap="" width="420">
<span style=" font-size:11px; font-family:MS Gothic;">F&M Technology LLC ,東京都昭島市拝島町三丁目10番3号</span>
<span style=" font-size:16px; font-family:MS Gothic;">
<br/>
<br/>${customer_name}
<#if reciever_address1??><br/>${reciever_address1}</#if>
<#if reciever_address2??><br/>${reciever_address2}</#if>
<#if reciever_address3??><br/>${reciever_address3}</#if>
<br/><#if reciever_postcode??>${reciever_postcode}   </#if>
<#if reciever_city??>${reciever_city}   </#if>
<#if reciever_state!=''>${reciever_state}</#if>
<br/>${reciever_country}
<#if reciever_city??><#else><br/></#if>
<#if reciever_state ==''><br/></#if>
<#if reciever_postcode??><#else><br/></#if>
</span><br/></td>
<td valign="top" nowrap="" align="right"></td>
</tr>

<tr>
<td valign="top"><br/><span style=" font-size:16px; font-family:Arial;"><b>${rate_sn}</b></span><br/></td>
<td valign="top" align="right"></td>
</tr>
<tr><td colspan="2" style=" font-size:18px; font-family:Arial;">Order #: ${orderId}</td></tr>
<tr>
<td valign="top">
<span style=" font-size:18px; font-family:Arial;"><#if invoiceno?? && invoiceno != "">Invoice #: ${invoiceno}</#if> </span></td>
<td valign="top" align="right"><span style=" font-size:18px; font-family:Arial;">${buydate}</span></td>
</tr>
<tr>
<td colspan="2">
<span style=" font-size:13px; font-family:Arial;"><strong>Ladies and Gentlemen,<br/></strong>
thank you very much for your order. You will be charged as follows.</span></td>

</tr>

<tr>

<td align="right" colspan="2">
<span style=" font-size:13px; font-family:Arial;"><b>Customer-No.: 
${customerno} / <#if customer_email??>${customer_email}</#if></b></span></td>
</tr>
</tbody></table>
<table cellpadding="0" width="650" height="206">
<tbody>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<tr style="font-size:12px; font-family:Arial;">
<td align="left" width="68" height="12"><b>Pos</b></td>
<td align="left" width="86" height="12"><b>Art-#</b></td>
<td align="left" width="146" height="12"><b>Designation</b></td>
<td align="right" width="51" height="12"><b>#</b></td>
<td align="right" width="73" height="12"><b>VAT rate.</b></td>
<td align="right" width="74" height="12"><b>Price</b></td>
<td align="right" width="136" height="12"><b>Total</b></td></tr>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<#list items as map>
	<tr style="font-size:10px; font-family:Arial;">
		<td  align="left" width="68">${map_index?if_exists+1}</td>
		<td  align="left" width="86">${map.id?if_exists}</td>
		<td  align="left" width="146">${map.name?if_exists}</td>
		<td  align="right" width="51">${map.num?if_exists}</td>
		<td  align="right" width="73">${map.rate?if_exists}</td>
		<td  align="right" width="74">${map.singlePrice?if_exists} ${BILLCUR}</td>
		<td  align="right" width="136">${map.totlePrice?if_exists} ${BILLCUR}</td>
	</tr>
</#list>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">Total (netto)</td>
<td nowrap="" align="right" height="16" width="136">${orderitem_totalprice} ${BILLCUR}</td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">
forwarding expenses (netto)</td>
<td nowrap="" align="right" height="16" width="136">
${orderitem_shipprice} ${BILLCUR}</td></tr>
<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">Total-Netto</td>
<td nowrap="" align="right" height="16" width="136">${order_totalprice}<strong style="font-weight: 400"> ${BILLCUR}</strong></td></tr>
<tr>
<td colspan="4" height="2" width="357"></td>
<td colspan="3" height="2" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508">+VAT</td>
<td nowrap="" align="right" height="13" width="136">${vat} ${BILLCUR}</td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508"><b>
sum to be paid </b></td>
<td nowrap="" align="right" height="13" width="136"><b>
${totalprice} ${BILLCUR}</b></td></tr>
<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:11px; font-family:Arial, Helvetica, sans-serif;">
<td valign="top" align="right" colspan="6" height="15" width="508">VAT amount:</td>
<td nowrap="" align="right" height="15" width="136">${rate}%  ${vat} ${BILLCUR}</td></tr> 

</tbody></table>

<#if invoiceno?? && invoiceno != "">
Payment method: ${paymethod}<br/>

<p>
<span style=" font-size:13px; font-family:Arial;">This calculation is a 
component of your order from ${buytime}.<br/>Your payment was received by us on ${paytime}. Thank you.  </span></p>

<span style=" font-size:13px; font-family:Arial;">
<p style="margin-top: 0; margin-bottom: 0">
Yours sincerely</p>
<p style="margin-top: 0; margin-bottom: 0">Inateck Support Team</p>
</span>
</#if>
