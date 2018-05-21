package com.springrain.erp.common.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticsSupplier {
	/**
	 * 网址包含$$直接物流号替换
	 * 网址包含##物流号按"-"分割顺序替换
	 * @return
	 */
	public static Map<String,String> getWebSite(){
		Map<String,String> map=new HashMap<String,String>();
		map.put("HU", "http://www.hnacargo.com/Portal/AwbSearch.aspx?supplier=HU&trackId=$$");
		map.put("PO", "http://www.polaraircargo.com/TrackPhase2/WebForm1.aspx?pe=##&se=");
		map.put("UPS_AE", "http://www.ups.com/actrack/track/submit?loc=en_US&track.x=30&track.y=10&awbNum=$$");
		map.put("CA", "http://www.airchinacargo.com/en/search_order.php?supplier=CA&trackId=$$");
		map.put("CX", "http://www.cathaypacificcargo.com/%E7%AE%A1%E7%90%86%E6%82%A8%E7%9A%84%E8%B4%A7%E4%BB%B6/%E8%B4%A7%E4%BB%B6%E8%BF%BD%E8%B8%AA/tabid/110/SingleAWBNo/$$/language/zh-CN/Default.aspx?supplier=CX&trackId=$$");
		map.put("MU", "http://cargo2.ce-air.com/MU/index.aspx?PRE=##&AWBNO=");
		map.put("UPS", "http://wwwapps.ups.com/WebTracking/processInputRequest?HTMLVersion=5.0&loc=en_DE&Requester=UPSHome&track.x=36&track.y=13&tracknum=$$");
		map.put("OOCL", "http://www.oocl.com/schi/Pages/default.aspx?supplier=OOCL&trackId=$$");
		map.put("MSC", "https://www.msc.com/chn?supplier=MSC&trackId=$$");
		map.put("EVENGREEN", "http://www.shipmentlink.com/servlet/TDB1_CargoTracking.do?SEL=s_cntr&NO=$$");
		map.put("MAERSK", "http://my.maerskline.com/trackingapp/zeroresult?searchNumber=$$");
		map.put("KLINE", "http://ecom.kline.com/tracking?supplier=KLINE&trackId=$$");
		map.put("ANL", "http://www.anl.com.au/ebusiness/tracking/search?SearchBy=Container&Reference=$$");
		map.put("CHINA SHIPPING", "http://www.cscl.com.cn/?supplier=CHINA SHIPPING&trackId=$$");
		map.put("COSCO", "http://ebusiness.coscon.com/NewEBWeb/public/cargoTracking/cargoTracking.xhtml?CARGO_TRACKING_NUMBER_TYPE=CONTAINER&CARGO_TRACKING_NUMBER=$$&REDIRECT=1&uid=");
		map.put("HAMBURG-SUD", "https://ecom.hamburgsud.com/ecom/en/ecommerce_portal/track_trace/track__trace/ep_trackandtrace.xhtml?lang=EN&supplier=HAMBURG-SUD&trackId=$$");
		map.put("APL", "http://homeport.apl.com/gentrack/trackingMain.do?trackInput01=$$");
		map.put("DHL", "https://activetracing.dhl.com/DatPublic/search.do?search=consignmentId&autoSearch=true&l=DE&at=package&a=$$");
		map.put("USPS", "https://www.usps.com/search.htm?q=$$");
		map.put("DPD", "https://tracking.dpd.de/parcelstatus?locale=en_D2&query=$$");
		map.put("CZ", "http://tang.cs-air.com/EN/WebFace/Tang.WebFace.Cargo/AgentAwbBrower.aspx?awbPrefix=##&awbNo=");
		map.put("OZ", "https://www.asianacargo.com/Cn?supplier=OZ&trackId=$$");
		map.put("MOL", "http://web.molpower.com/Tracking/Main/Home?trackId=$$");
		map.put("FEDEX", "https://www.fedex.com/apps/fedextrack/?action=track&cntry_code=cn&trackingnumber=$$");
		map.put("UPS_LTL", "http://ltl.upsfreight.com/shipping/tracking/TrackingDetail.aspx?TrackProNumber=$$");
		map.put("YANGMING", "http://www.yangming.com/e-service/Track_Trace/mul_ctnr.aspx?rdolType=CT&str=$$,");
		map.put("UASC","http://uasconline.uasc.net/Home?supplier=UASC&trackId=$$");//UACU3571361
		map.put("NH","https://shar.ana.co.jp/eACROSS/shipmentStatusSearch.do?dispatch=retrieveMAWBSearchResult&guestEntry=shipmentStatus&mawbPrefix1=##&mawbSuffix1=");//205-65258642
		map.put("RU", "http://airbridgecargo.com/tracking/?firstnakl=##&awb=");//580-08857520
		map.put("SQ", "http://www.siacargo.com/?trackId=$$");//618-99399963
		map.put("BR", "http://www.brcargo.com/ec_web/?Parm2=191&Parm3=?TNT_FLAG=Y&AWB_CODE=##&MAWB_NUMBER=");//695-39336290
		map.put("5X", "http://www.ups.com/aircargo/using/services/actracking/actracking.html?trackId=$$");//406-87383505
		map.put("DHL-FREE", "https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=en&rfn=&extendedSearch=true&idc=$$");//520199031651
		map.put("CMA", "http://www.cma-cgm.com/ebusiness/tracking/search?SearchBy=Container&Reference=$$");//ECMU1543458
		map.put("AC", "http://cargotools.aircanada.ca/TrackAndTraceInput.asp?trackId=$$");//014-37882342
		map.put("CI", "https://cargo.china-airlines.com/CCNet/pgFreightStatus/cgoTrack.aspx?AWBPrefix=##&AWBNumber=");//297-30808433
		map.put("EK", "http://www.skycargo.com/english/index.aspx?trackId=$$");//176-61024304
		map.put("BFS","https://www.bangkokflightservices.com/m/tt/tt_web.php?stype=web&h_prefix=HWB&h_sn=&m_prefix=##&m_sn=");//900-02319870
		map.put("AA", "https://www.aacargo.com/index.html?trackId=$$");//001-83454825
		map.put("JL", "http://www.jal.co.jp/jalcargo/?trackId=$$");//131-81764782
		map.put("CK", "http://cargo2.ce-air.com/MU/index.aspx?trackId=$$");//112-16435053
		map.put("DHL_EX", "http://www.cn.dhl.com/zh/express/tracking.html?brand=DHL&AWB=$$");//2437462156
		map.put("PGW", "http://www.pgwing.cn");//PGW2016111201
		map.put("KL", "https://afklcargo.com/WW/en/local/app/tnt/trackntrace.jsp#/tntdetails?awbids=$$");//074-52283803
		map.put("LH","http://tracking.lhcargo.com/trackit/awb.trackit");//020-59742352
		map.put("HYUNDAI","http://www.hmm.co.kr/cms/business/ebiz/trackTrace/trackTrace/index.jsp?type=2&is_quick=Y&userNation=ebiz&number=$$");//TRHU1305277
		map.put("MCC","https://my.mcc.com.sg/tracking/search?searchNumber=$$");//MSKU2071919
		map.put("CV", "http://www.cargolux.com/?trackId=$$");//172-24541974 
		
		map.put("NYK", "https://www.nykline.com/ecom/CUP_HOM_3000.do?redir=Y");//TRLU7456197
		map.put("WANHAI", "http://www.wanhai.com/views/Main.xhtml");//WHLU0538300 
		return map;
	}
	
	
	public static List<String> getLogisticsSupplierByType(String type) {
		List<String> list=new ArrayList<String>();
		if("1".equals(type)){//空运
			list.add("HU");
			list.add("PO");
			list.add("UPS_AE");
			list.add("CA");
			list.add("CX");
			list.add("MU");
			list.add("CZ");
			list.add("OZ");
			list.add("NH");
			list.add("RU");
			list.add("SQ");
			list.add("BR");
			list.add("5X");
			list.add("CI");
			list.add("EK");
			list.add("AC");
			list.add("BFS");
			list.add("AA");
			list.add("JL");
			list.add("KL");
			list.add("LH");
			list.add("CK");
			list.add("CV");
		}else if("2".equals(type)){//快递
			list.add("UPS");
			list.add("FEDEX");
			list.add("DHL_EX");
		}else if("3".equals(type)){//海运
			list.add("OOCL");
			list.add("MSC");
			list.add("EVENGREEN");
			list.add("MAERSK");
			list.add("KLINE");
			list.add("ANL");
			list.add("CHINA SHIPPING");
			list.add("COSCO");
			list.add("HAMBURG-SUD");
			list.add("APL");
			list.add("MOL");
			list.add("YANGMING");
			list.add("UASC");
			list.add("CMA");
			list.add("HYUNDAI");
			list.add("MCC");
			list.add("NYK");
			list.add("WANHAI");
		}else if("4".equals(type)){
			list.add("DHL");
			list.add("DHL-FREE");
			list.add("UPS");
			list.add("USPS");
			list.add("DPD");
			list.add("UPS_LTL");
			list.add("OTHER");
		}else if("5".equals(type)){
			list.add("PGW");
		}
		return list;
	}
	
	

}
