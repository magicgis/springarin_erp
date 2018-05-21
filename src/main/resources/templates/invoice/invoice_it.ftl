<table width="650">
<tbody>
<tr>

<td valign="top" nowrap="" width="50%"><b><img src="logo_small.jpg"/></b></td>
<td valign="top" nowrap="" align="right" width="50%"><span style=" font-size:13px; font-family:Arial;">
<b>F&M Technology GmbH<br/>Chun Cui</b><br/>Montgolfierstraße 6<br/>04509 Wiedemar<br/>
Email: support@inateck.com<br/>
Telefono:+49 342-07673081<br/>Fax: +49 342-07673082</span></td>
</tr>
<tr>
<td nowrap="" width="420"><span style=" font-size:11px; font-family:Arial;">F&M Technology GmbH, Chun Cui, Montgolfierstraße 6, 04509 Wiedemar</span>
<span style=" font-size:16px; font-family:Arial;"><br/>
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
<td valign="top"><br/><span style=" font-size:16px; font-family:Arial;"><b>${rate_sn}</b></span></td>
<td valign="top" align="right"></td>
</tr>
<tr><td colspan="2" style=" font-size:18px; font-family:Arial;">Numero d'ordine: ${orderId}</td></tr>
<tr>
<td valign="top">
<span style=" font-size:18px; font-family:Arial;"><#if invoiceno?? && invoiceno != "">Fattura 
Nr.: ${invoiceno}</#if> </span></td>
<td valign="top" align="right"><span style=" font-size:18px; font-family:Arial;"><#if type!='2'>${buydate}</#if><#if type=='2'>${paytime}</#if></span></td></tr>
<tr>
<td colspan="2">
<span style=" font-size:13px; font-family:Arial;">

<strong>Gentili Signori,<br/></strong>
Vi ringraziamo cordialmente per il Vostro ordine e Vi mettiamo in conto quanto 
segue:</span></td>
</tr>
<tr>

<td align="right" colspan="2"><span style=" font-size:13px; font-family:Arial;"><b>numero cliente: ${customerno} / <#if customer_email??>${customer_email}</#if></b></span></td>
</tr>
</tbody></table>
<br/>
<table cellpadding="0" width="650" height="206">
<tbody>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>

<tr style="font-size:12px; font-family:Arial;">
<td align="left" width="68" ><b>Pos</b></td>
<td align="left" width="86" ><b>Nr.-Art</b></td>
<td align="left" width="146"  ><b>Descrizione</b></td>
<td align="right" width="51"  ><b>Quantitá</b></td>
<td align="right" width="73"  ><b>I.V.A.</b></td>
<td align="right" width="74" ><b>Prezzo</b></td>
<td align="right" width="136" ><b>Totale</b></td>
</tr>


<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<#list items as map>
	<tr style="font-size:10px; font-family:Arial;">
		<td align="left" width="68" >${map_index?if_exists+1}</td>
		<td align="left" width="86" >${map.id?if_exists}</td>
		<td align="left" width="146" >${map.name?if_exists}</td>
		<td align="right" width="51" >${map.num?if_exists}</td>
		<td align="right" width="73" >${map.rate?if_exists}</td>
		<td align="right" width="74" >${map.singlePrice?if_exists} ${BILLCUR}</td>
		<td align="right" width="136" ><#if type=='2'>-</#if><#if type=='4'>-</#if>${map.totlePrice?if_exists} ${BILLCUR}</td>
	</tr>
</#list>
<tr>
<td colspan="7" height="15" width="646"><hr size="1"/></td></tr>

<#if type!='3'>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">Totale (netto)</td>
<td nowrap="" align="right" height="16" width="136">
<#if type=='2'>-</#if><#if type=='4'>-</#if>${orderitem_totalprice} ${BILLCUR}</td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">

<td align="right" colspan="6" height="16" width="508">Costi di spedizione (netto)</td>
<td nowrap="" align="right" height="16" width="136"><#if type=='2'>-</#if><#if type=='4'>-</#if>${orderitem_shipprice} ${BILLCUR}</td></tr>
<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">
Totale-Netto</td>
<td nowrap="" align="right" height="16" width="136"><#if type=='2'>-</#if><#if type=='4'>-</#if>${order_totalprice} ${BILLCUR}</td></tr>
<tr>
<td colspan="4" height="2" width="357"></td><td colspan="3" height="2" width="287"><b><hr size="1"/></b></td></tr>
</#if>

<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508"><#if type!='3'>+</#if>I.V.A..</td>
<td nowrap="" align="right" height="13" width="136"><#if type=='3'>-</#if><#if type=='2'>-</#if><#if type=='4'>-</#if>${vat} ${BILLCUR}</td></tr>

<#if type!='3'>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508"><b>
importo da pagare </b></td>
<td nowrap="" align="right" height="13" width="136"><b><#if type=='2'>-</#if><#if type=='4'>-</#if>${totalprice} ${BILLCUR}</b></td></tr>
</#if>

<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:11px; font-family:Arial, Helvetica, sans-serif;">
<td valign="top" align="right" colspan="6" height="15" width="508">Importo I.V.A:</td>
<td nowrap="" align="right" height="15" width="136">
${rate}% <#if type=='3'>-</#if> <#if type=='2'>-</#if><#if type=='4'>-</#if>${vat} ${BILLCUR}
</td></tr> 

</tbody></table>

<#if invoiceno?? && invoiceno != "">
Metodo di pagamento: ${paymethod} <br/>
<#if deliveryDate??>Data di consegna:${deliveryDate}<br/></#if>
<#if remark??>osservazione:${remark}<br/></#if>
<p>
<span style=" font-size:13px; font-family:Arial;">Questa fattura é parte 
integrante del Suo ordine del ${buytime}.<br/><#if type!='2'>Il Suo pagamento é stato accreditato sul nostro il ${paytime}. Molte grazie. </#if><#if type=='2'>Riceverai un rimborso in ${paytime}. Molte grazie. </#if> </span></p>

<p style="margin-top: 0; margin-bottom: 0"><span style=" font-size:13px; font-family:Arial;">Cordiali saluti</span></p>
<p style="margin-top: 0; margin-bottom: 0"><span style=" font-size:13px; font-family:Arial;">Chun 
Cui<br/>
</span><small><br/>
UID: DE281354945<br/>
Valgono le nostre condizioni generali di vendita</small></p>
<p style="margin-top: 0; margin-bottom: 0"> </p>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="98%"  height="68">
  <tbody><tr>
    <td width="35%" style="vertical-align: top; border-bottom-style: solid; border-bottom-width: 1" height="36">
	<small>

    <span style=" font-size:12px; font-family:Arial;">Intestatario: F&M Technology GmbH <br/>

    Istituto di credito: Commerzbank <br/>
    BLZ: 86040000,
    Kontonummer: 110030400<br/>
    SWIFT: COBADEFFXXX,
    IBAN: DE62860400000110030400
    </span>
    </small></td>
  </tr>
  </tbody></table>
</#if>