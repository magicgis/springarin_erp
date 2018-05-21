<div>
	<span style="font-family: 'Times New Roman', serif; line-height: 1.5;">Dear German Colleague,</span>
</div>
<div style="font-family: 'Times New Roman', serif;">
	<div style="color: #000;">
		<p>Good day!
			<br/>
		</p>
		<p>
			This is the shipping plan for next week. 
		</p>
		<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" style="border-collapse: collapse">
			<tbody>
				<#list fbas as fba> 
					<tr style="height: 15.0pt">
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; background: yellow; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Ship Date/<br/> Ship Order</p></td>
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; background: yellow; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Job No. (Model)</p></td>
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">SKU</p></td>
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">FNSKU</p></td>
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Units</p></td>
						<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-left: none; background: #C6D9F1; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><p class="MsoNormal">Remark</p></td>
					</tr>
					<#list fba.items as item> 
						<tr style="height: 15.0pt; <#if (change[fba.id?c] ?? && change[fba.id?c][item.sku] ??) || manySkus[item.sku] ??>background: yellow;</#if>">
							<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"></td>
							<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"></td>
							<td nowrap="nowrap" valign="top" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.sku}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${fnsku[item.sku]!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${item.quantityShipped!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${change[fba.id?c][item.sku]!""} <#if manySkus[item.sku]??> Attention：The item has more than one sku[${manySkus[item.sku]}]</#if></td>
						</tr>
					</#list>
					<tr style="height: 15.0pt;">
							<td nowrap="nowrap" valign="top" colspan="4" style="border: solid windowtext 1.0pt; border-top: none; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">Total</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt">${fba.quantityShipped!""}</td>
							<td nowrap="nowrap" valign="top" style="border-top: none; border-left: none; border-bottom: solid windowtext 1.0pt; border-right: solid windowtext 1.0pt; padding: 0cm 5.4pt 0cm 5.4pt; height: 15.0pt"><b>Boxes: ${boxes[fba.id?c]!""}</b></td>
					</tr>
				</#list>
			</tbody>
		</table>
		<br/><br/>
		If any question or problem, please let me know freely. Thanks.
		<br/><br/>
		Best regards<br/>
		${user.name}(${user.email})
	</div>
</div>
