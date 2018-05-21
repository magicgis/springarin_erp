<table width="650">
<tbody>
<tr>

<td valign="top" nowrap="" width="60%"><b><img src="logo_small.jpg"/></b></td>
<td valign="top" nowrap="" align="right" width="50%"><span style=" font-size:13px; font-family:Arial;">
<b>F&M Technology GmbH<br/>Chun Cui</b><br/>Montgolfierstraße 6<br/>04509 Wiedemar<br/>
Courrier électronique : support@inateck.com<br/>
Téléphone : +49 342-07673081<br/>Télécopie : +49 342-07673082</span></td>
</tr>
<tr>
<td nowrap=""><span style=" font-size:11px; font-family:Arial;">F&M Technology GmbH, Chun Cui,Montgolfierstraße 6, 04509 Wiedemar</span>
<span style=" font-size:16px; font-family:Arial;"><br/>
<br/>${customer_name}
<#if reciever_address1??><br/>${reciever_address1}</#if>
<#if reciever_address2??><br/>${reciever_address2}</#if>
<#if reciever_address3??><br/>${reciever_address3}</#if>
<#if reciever_postcode??><br/>${reciever_postcode}   </#if>
<#if reciever_city??>${reciever_city}</#if>
<#if reciever_state!=''>${reciever_state}</#if>
<br/>${reciever_country}

</span></td>
<td valign="top" nowrap="" align="right"></td>
</tr>
<#if rate_sn??>
<tr>
<td valign="top"><br/><span style="font-size:16px; font-family:Arial;"><b>${rate_sn}</b></span><br/></td>
<td valign="top" align="right"></td>
</tr>
</#if>

<tr><td colspan="2" style=" font-size:18px; font-family:Arial;">Numéro de la commande : ${orderId}</td></tr>
<tr>
<td valign="top">
<span style=" font-size:18px; font-family:Arial;"><#if invoiceno?? && invoiceno != "">Numéro de la facture : ${invoiceno}</#if> </span></td>
<td valign="top" align="right"><span style=" font-size:18px; font-family:Arial;"><#if type!='2'>${buydate}</#if><#if type=='2'>${paytime}</#if></span></td>
</tr>
<tr>
<td colspan="2">
<span style=" font-size:13px; font-family:Arial;"><strong><br/>Madame/Monsieur,<br/></strong>
Nous vous remercions de votre achat et le calcul se fait comme suit :</span></td>
</tr>
<tr>

<td align="right" colspan="2"><span style=" font-size:13px; font-family:Arial;"><b>Numéro du client : ${customerno} / <#if customer_email??>${customer_email}</#if></b></span></td>
</tr>
</tbody></table>
<table cellpadding="0" width="650" height="206">
<tbody>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<tr style="font-size:12px; font-family:Arial;">
<th align="left" width="68" ><b>Position</b></th>
<th align="left" width="86" ><b>Numéro d'article</b></th>
<th align="left" width="146"  ><b>Désignation</b></th>
<th align="right" width="51"  ><b>Quantité</b></th>
<th align="right" width="73"  ><b>Taux de TVA</b></th>
<th align="right" width="74" ><b>Prix Unitaire</b></th>
<th align="right" width="136" ><b>Prix Total</b></th>
</tr>
<tr>
<td colspan="7" height="15" width="646">
<hr size="1"/>
</td></tr>
<#list items as map>
	<tr style="font-size:10px; font-family:Arial;">
		<td align="left" width="68">${map_index?if_exists+1}</td>
		<td align="left" width="86">${map.id?if_exists}</td>
		<td align="left" width="146">${map.name?if_exists}</td>
		<td align="right" width="51" >${map.num?if_exists}</td>
		<td align="right" width="73">${map.rate?if_exists}</td>
		<td align="right" width="74">${map.singlePrice?if_exists}${BILLCUR}</td>
		<td align="right" width="136"><#if type=='2'>-</#if><#if type=='4'>-</#if>${map.totlePrice?if_exists}${BILLCUR}</td>
	</tr>
</#list>
<tr>
<td colspan="7" height="15" width="646"><hr size="1"/></td></tr>

<#if type!='3'>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">Prix total (net)</td>
<td nowrap="" align="right" height="16" width="136">
<#if type=='2'>-</#if><#if type=='4'>-</#if>${orderitem_totalprice} ${BILLCUR}</td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">Frais d'expédition 
(net)</td>
<td nowrap="" align="right" height="16" width="136"><#if type=='2'>-</#if><#if type=='4'>-</#if>${orderitem_shipprice} ${BILLCUR}</td></tr>
<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="16" width="508">
Total-Net</td>
<td nowrap="" align="right" height="16" width="136"><#if type=='2'>-</#if><#if type=='4'>-</#if>${order_totalprice}<strong style="font-weight: 400"> ${BILLCUR}</strong></td></tr>
<tr><td colspan="4" height="2" width="357"></td><td colspan="3" height="2" width="287"><b><hr size="1"/></b></td></tr>
</#if>

<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508"><#if type!='3'>+</#if>TVA.</td>
<td nowrap="" align="right" height="13" width="136"><#if type=='3'>-</#if><#if type=='2'>-</#if><#if type=='4'>-</#if>${vat} ${BILLCUR}</td></tr>

<#if type!='3'>
<tr style="font-size:13px; font-family:Arial, Helvetica, sans-serif;">
<td align="right" colspan="6" height="13" width="508"><b>Montant à 
régler</b></td>
<td nowrap="" align="right" height="13" width="136"><b><#if type=='2'>-</#if><#if type=='4'>-</#if>${totalprice} ${BILLCUR}</b></td></tr>
</#if>

<tr>
<td colspan="4" height="4" width="357"></td>
<td colspan="3" height="4" width="287"><b><hr size="1"/></b></td></tr>
<tr style="font-size:11px; font-family:Arial, Helvetica, sans-serif;">
<td valign="top" align="right" colspan="6" height="15" width="508">Montant TVA :</td>
<td nowrap="" align="right" height="15" width="136">
${rate}%   <#if type=='3'>-</#if> <#if type=='2'>-</#if><#if type=='4'>-</#if>${vat} ${BILLCUR}</td></tr> 

</tbody></table>

<#if invoiceno?? && invoiceno != "">
Moyen de paiement :
${paymethod} <br/>
<#if deliveryDate??>date de livraison:${deliveryDate}<br/></#if>
<#if remark??>remarque:${remark}<br/></#if>
<p>
<span style=" font-size:13px; font-family:Arial;">Cette facture fait partie de 
votre commande du ${buytime}.<br/><#if type!='2'>Nous avons reçu votre paiement le ${paytime}. Merci beaucoup.</#if><#if type=='2'>Vous recevrez un remboursement au ${paytime}. Merci beaucoup.</#if>   </span></p>
<p style="margin-top: 0; margin-bottom: 0"><span style=" font-size:13px; font-family:Arial;">Veuillez agréer mes salutations distinguées.</span></p>
<p style="margin-top: 0; margin-bottom: 0"><span style=" font-size:13px; font-family:Arial;">Chun Cui<br/></span>
<font face="Arial"><small><br/>
No.:DE281354945<br/>
</small></font></p>
<p style="margin-top: 0; margin-bottom: 0"> </p>
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="98%" height="68">
  <tbody><tr>
    <td width="35%" style="vertical-align: top; border-bottom-style: solid; border-bottom-width: 1" height="36">
	<small>
    <span style=" font-size:12px; font-family:Arial;">Détenteur de compte: F&M Technology GmbH<br/>

    Banque: Commerzbank <br/>
    BLZ: 86040000,
    Kontonummer: 110030400<br/>
    SWIFT: COBADEFFXXX,
    IBAN: DE62860400000110030400
    </span>
    </small></td>
  </tr>
  </tbody></table>
 </#if> 