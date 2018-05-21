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
				<#if order.shippingAddress.street??><br/>${order.shippingAddress.street}</#if>
				<#if order.shippingAddress.street1??><br/>${order.shippingAddress.street1}</#if>
				<#if order.shippingAddress.street2??><br/>${order.shippingAddress.street2}</#if>
				<#if order.shippingAddress.postalCode??><br/>${order.shippingAddress.postalCode}</#if>
				<#if order.shippingAddress.cityName??><br/>${order.shippingAddress.cityName}   </#if>
				<#if order.shippingAddress.stateOrProvince??><br/>${order.shippingAddress.stateOrProvince}   </#if>
				<#if order.shippingAddress.countryCode??><br/>${order.shippingAddress.countryCode}</#if>
				<#if order.shippingAddress.phone??><#if order.shippingAddress.phone!=""&&order.shippingAddress.phone!="Invalid Request"><br/>${order.shippingAddress.phone}</#if></#if>
				</span>
			</td>
			</tr>
			<tr>
			<td valign="top">
			<span style=" font-size:16px; font-family:Arial;">Bill NO.: ${order.showBillNo}</span>
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
			<td align="left" width="30"><b>No.</b></td>
			<td align="left" width="320"><b>ProductName</b></td>
			<td align="right" width="30"><b>Quantity</b></td>
			</tr>
			<tr>
			<td colspan="3" height="15" width="400">
			<hr size="1"/>
			</td></tr>
			<#list order.items as map>
				<tr style="font-size:13px; font-family:Arial;">
					<td align="left" width="30" >${map_index?if_exists+1}</td>
					<td align="left" width="300"  >${map.title?if_exists}[${map.sku?if_exists}]</td>
					<td align="right" width="30"  >${map.quantityShipped?if_exists}</td>
				</tr>
			</#list>
			<tr><td colspan="3" height="15" width="400"><hr size="1"/></td></tr>
			<tr><td align="left" width="400">
					<#if order.remark??>Remark:${order.remark}</#if><br/>
					<#if order.orderType=='2'>
				
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

