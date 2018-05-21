<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
<!--
.headTd{border: solid #666666 1.0pt; border-top: none; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center}
.bodyTd{border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center}
.conSpan{font-size: 8.5pt; color: #333333};
-->
</style>
</head>
<body>
	<div>
		<span style="line-height: 1.5;">Hi all,</span>
	</div>
	<div>
		<div style="font: Verdana normal 14px; color: #000;">
			<div class="WordSection1">
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">Sendungsnummer：${inventoryOut.tranFbaNo}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">Name：${fbaName}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">Address：${address}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">TotalQuantity：${inventoryOut.totalQuantity}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">Skus insgesamt：${inventoryOut.items?size}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">PickUpDate：${inventoryOut.ladingDate!?date?string('yyyy-MM-dd')}</span></p>
				<p class="MsoNormal" style="margin-bottom: 12.0pt"><span lang="EN-US">TrackBarcode：${inventoryOut.trackBarcode}</span></p>
				
				<table  border="0" cellspacing="0"cellpadding="0" style="border-collapse: collapse;width:800px" id="ex0">
					<tbody>
						<tr>
							<td colspan="10"class="headTd"><p  align="center" style="text-align: center"><b><span class="conSpan">Out Bound Detailed List：${inventoryOut.billNo}</span></b></p></td>
						</tr>
						<tr>
							<td	class="bodyTd"><span class="conSpan">No.</span></td>
							<td	class="bodyTd"><span class="conSpan">SKU</span></td>
							<td	class="bodyTd"><span class="conSpan">FbaQuantity</span></td>
							<td	class="bodyTd"><span class="conSpan">OutboundQuantity</span></td>
							<td	class="bodyTd"><span class="conSpan">Fnsku</span></td>
						</tr>
						<#list inventoryOut.viewItems as bItem>
						<tr>
							<td class="bodyTd">	<span class="conSpan">${bItem_index+1}</span></td>
							<td class="bodyTd"><span class="conSpan">${bItem.sku}</span></td>
							<td class="bodyTd"><span class="conSpan">${skuMap[bItem.sku]}</span></td>
							<td class="bodyTd"><span class="conSpan">${bItem.quantity}</span></td>
							<td class="bodyTd"><span class="conSpan">${bItem.remark}</span></td>
						</tr>
						</#list>
					</tbody>
				</table>
				
			</div>
		</div>
	</div>
	<style></style>

</body>
</html>
