<div>
	<span style="font-family: 'Times New Roman', serif; line-height: 1.5;">Dear German Colleague,</span>
</div>
<div style="font-family: 'Times New Roman', serif;">
	<div style="color: #000;">
		<p>Good day!
			<br/>
		
		</p>
		<p style="background: yellow;">
			<b><span style="color:red">[P]</span>${subject!''}</b>
		</p>
		<p>
			<span>Please dispatch</span> <b><a href="http://192.81.128.219/springrain-erp/a/psi/fbaInbound?shipmentId=${fba.id}&country="><span style="font-size: 9.0pt;color: #004B91; background: white">${fba.shipmentName}</span></a></b> <span style="color: red">via DPD</span><span
				style="color: #1F497D"> </span><span>to<span style="color: #1F497D"> </span>${country}<span style="color: #1F497D"> </span>FBA.
			</span>
		</p>
		<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" style="border-collapse: collapse">
			<tbody>
				<tr style="height: 15.0pt">
					<td colspan="4" style="border: solid windowtext 1.0pt; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt;text-align: center;vertical-align: middle;">Shipment Id : ${fba.shipmentId!""}</td>
				</tr>
				<tr style="height: 15.0pt">
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">SKU</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">FNSKU</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Units</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Remark</p></td>
				</tr>
				<#list fba.items as item> 
					<tr style="height: 15.0pt; <#if change[item.sku]?? || manySkus[item.sku] ??>background: yellow;</#if>">
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.sku}</td>
						<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${fnsku[item.sku]!""}</td>
						<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.quantityShipped!""}</td>
						<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${change[item.sku]!""} <#if manySkus[item.sku]??> Attention：The item has more than one sku[${manySkus[item.sku]}]</#if></td>
					</tr>
				</#list>
				<tr style="height: 15.0pt;">
						<td nowrap="nowrap" valign="top" colspan="2" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">Total</td>
						<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${fba.quantityShipped!""}</td>
						<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"></td>
				</tr>
			</tbody>
		</table>
		<br/><br/>
		Best regards<br/>
		${user.name}(${user.email})
	</div>
</div>
