<div>
	<span style="font-family: 'Times New Roman', serif; line-height: 1.5;">Dear German Colleague,</span>
</div>
<div style="font-family: 'Times New Roman', serif;">
	<div style="color: #000;">
		<p>Good day!</p>
		<p>
			<span>
				Goods received doesn't match delivered quantity, please check. 
			</span>
		</p>
		<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" style="border-collapse: collapse">
			<tbody>
				<tr style="height: 15.0pt">
					<td colspan="5" style="border: solid windowtext 1.0pt; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt;text-align: center;vertical-align: middle;">Shipment Id : ${fba.shipmentId!""}</td>
				</tr>
				<tr style="height: 15.0pt">
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">SKU</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">FNSKU</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Units</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Received</p></td>
					<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Damage</p></td>
				</tr>
				<#list fba.items as item> 
					<#if item.quantityShipped != item.quantityReceived >
						<tr style="height: 15.0pt;">
							<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.sku}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${fnsku[item.sku]!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.quantityShipped!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.quantityReceived!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"></td>
						</tr>
					</#if>
				</#list>
			</tbody>
		</table>
		<br/><br/>
		Best regards<br/>
		${user.name}(${user.email})
	</div>
</div>
