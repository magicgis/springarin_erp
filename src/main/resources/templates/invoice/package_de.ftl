<#list orderList as order>
<#if order_index%3=0>
<table width="680" style="page-break-before:always">
<#else>
<table width="680">
 </#if>	
<tbody>
   <tr>
      <td>
        <table> 
			<tbody>
			<tr>
			<td nowrap="" width="200">
				<span style=" font-size:18px; font-family:Arial;">
                 ${order.shippingAddress.name}
				<#if order.shippingAddress.street??><#if order.shippingAddress.street!=""><br/>${order.shippingAddress.street}</#if></#if>
				<#if order.shippingAddress.street1??><#if order.shippingAddress.street1!=""><br/>${order.shippingAddress.street1}</#if></#if>
				<#if order.shippingAddress.street2??><#if order.shippingAddress.street2!=""><br/>${order.shippingAddress.street2}</#if></#if>
				<#if order.shippingAddress.postalCode??><#if order.shippingAddress.postalCode!=""><br/>${order.shippingAddress.postalCode}</#if></#if>
				<#if order.shippingAddress.cityName??><#if order.shippingAddress.cityName!=""><br/>${order.shippingAddress.cityName}   </#if></#if>
				<#if order.shippingAddress.stateOrProvince??><#if order.shippingAddress.stateOrProvince!=""><br/>${order.shippingAddress.stateOrProvince}</#if></#if>
				<#if order.shippingAddress.countryCode??><#if order.shippingAddress.countryCode!=""><br/>${order.shippingAddress.countryCode}</#if></#if>
				<#if order.shippingAddress.phone??><#if order.shippingAddress.phone!=""&&order.shippingAddress.phone!="Invalid Request"><br/>${order.shippingAddress.phone}</#if></#if>
				</span>
			</td>
			</tr>
			<tr>
			<td valign="top">
			<span style=" font-size:16px; font-family:Arial;">Rechnung Nr.: ${order.showBillNo} <#if order.trackNumber??>${order.supplier}:${order.trackNumber}</#if></span>
			</td>
			
			</tr>
			</tbody>
		</table>
      </td>
      
      <td>
        
       <table cellpadding="0" width="400" height="206">
			<tbody>
			<tr><td valign="top" align="right" colspan='3'><span style=" font-size:18px; font-family:Arial;">${currentDate}</span></td></tr>
			<tr>
			<td colspan="3" height="15" width="400">
			<hr size="1" />
			</td>
			</tr>
			<tr style="font-size:12px; font-family:Arial;">
			<td align="left" width="30"><b>Pos</b></td>
			<td align="left" width="320"><b>Bezeichnung</b></td>
			<td align="right" width="30"><b>Anz</b></td>
			</tr>
			<tr>
			<td colspan="3" height="15" width="400">
			<hr size="1"/>
			</td></tr>
			<#list order.items as map>
				<tr style="font-size:13px; font-family:Arial;">
					<td align="left" width="30" >${map_index?if_exists+1}</td>
					<td align="left" width="300"  >${map.title?if_exists}
					   <#if map.sku??>
					       <#if map.sku?contains("_nearly new")||map.sku?contains("_Nearly New")||map.sku?contains("_used")||map.sku?contains("_Defective")||map.sku?contains("_defective")||map.sku?contains("_Unsellable")||map.sku?contains("_unsellable")>
					        <b><span style=" font-size:20px;color:red;">[${map.sku}]</span></b>
					       <#else>
					          [${map.sku}]
					      </#if>
					   </#if>
					</td>
					<td align="right" width="30"  >${map.quantityShipped?if_exists}</td>
				</tr>
			</#list>
			<tr><td colspan="3" height="15" width="400"><hr size="1"/></td></tr>
			<tr><td align="left" width="400">
					<#if order.remark??>remark:${order.remark}</#if><br/>
					<#if order.orderType=='2'>
					Hinweis: eine kehrt zurück
					</#if>
				</td>
            </tr>
			</tbody>
        </table>
      </td>

   </tr>
</tbody>   
</table>
<br/><br/><br/><br/>
</#list>

