package com.springrain.erp.modules.sys.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;

/***
 *生成公共序列号类，按每天循环 
 * 
 * 
 */
@Repository
public class GenerateSequenceInvoiceDao extends BaseDao<Object>{
	
	
	/**
	 *获取流水号 INVOICE_EU  INVOICE_US  INVOICE_JP  INVOICE_OTHER
	 */
	public  String  genInvoiceSequence(String seqCodeName,Integer iniSeqLength,String suffix){
		   String flag=suffix;
		   
		   String querySeqSql = "SELECT current_seq,seq_length,seq_from,updateTime,seq_max FROM amazoninfo_generate_invoice_sequence WHERE seq_codeName=:p1 ";
		   String updateSeqSql = "UPDATE amazoninfo_generate_invoice_sequence SET current_seq=:p1,updateTime='"+ this.getSystemTime("yyyy/MM/dd HH:mm:ss")+ "' WHERE seq_codeName=:p2 ";
		   String returnSeq ="";
		
		   List<Object[]> list= this.findBySql(querySeqSql,new Parameter(seqCodeName));
		   if(list!=null&&list.size()>0){
			   Object[] object=  list.get(0);
				int curSeq = Integer.parseInt(object[0]==null?"":object[0].toString());
				int seqLength = Integer.parseInt(object[1]==null?"":object[1].toString());
				String updateTime = object[3]==null?"":object[3].toString();
				int newCurSeq = curSeq + 1;
				querySeqSql = "SELECT current_seq,updateTime FROM amazoninfo_generate_invoice_sequence WHERE seq_codeName=:p1";
				List<Object[]> list1=this.findBySql(querySeqSql,new Parameter(seqCodeName));
				
				returnSeq = flag+String.format("%0" + seqLength + "d", newCurSeq );
				
				if(list1!=null&&list1.size()>0){
					String curUpdateTime = list1.get(0)[1].toString();
				// 判断初始的更新日期是否与当前更新日期相同，如果不同就说明有其他人做了操作，则递归使用本方法，重新获取连番。
			        if(this.formatDateString(updateTime, "yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss").equals(this.formatDateString(curUpdateTime, "yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss"))) {
						this.updateBySql(updateSeqSql, new Parameter(newCurSeq,seqCodeName));
					}else{
						returnSeq = genInvoiceSequence(seqCodeName,iniSeqLength,suffix);
					}
				}
			}else{
				StringBuilder toSeq=new StringBuilder("");
				for(int j=0;j<iniSeqLength;j++){
					toSeq.append("9");
				}
				String tmpSeq= flag+ String.format("%0" + iniSeqLength + "d", 1 );	
				returnSeq = tmpSeq;
				insertGenSequence(seqCodeName,1,1,iniSeqLength,Integer.parseInt(toSeq.toString()));
			}
		
			return returnSeq;
	}
	
	/**
	 * 根据指定的日期格式将系统时间进行转换，并返回。
	 * 
	 * 
	 * @param timeFormat
	 *            时间格式 例如： "yyyy-MM-dd   HH:mm:ss" "yyyy/MM/dd" etc..
	 * @return 系统时间
	 * @since 2012/08/03 by LEE
	 */
	public  String getSystemTime(String timeFormat) {
		//Locale systime = Locale.CHINA;
		//SimpleDateFormat timeformat = new SimpleDateFormat(timeFormat, systime);
		SimpleDateFormat timeformat = new SimpleDateFormat(timeFormat);
		return timeformat.format(new Date());
	}
	
	/**
	 * 转换格式时间, 从inputFormat转ouputFormat
	 * 
	 * @param s
	 *            对象
	 * @return
	 */
	public  String formatDateString(Object s, String inputFormat,	String ouputFormat) {
		String str = nullToEmpty(s);
		if ("".equals(str))
			return str;

		try {
			return (new SimpleDateFormat(ouputFormat)).format(new SimpleDateFormat(inputFormat).parse(str));
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * NULL转空串
	 * 
	 * 
	 * @param s
	 * @return
	 */
	public  String nullToEmpty(String s) {
		return (s == null || "null".equalsIgnoreCase(s)) ? "" : s.trim();
	}
	
	/**
	 * NULL转空串
	 * 
	 * 
	 * @param s
	 *            对象
	 * @return
	 */
	public  String nullToEmpty(Object s) {
		return s == null ? "" : (String.valueOf(s)).trim();
	}
	
	/***
	 *添加需要生成流水号的数据 
	 * seqCodeName必须唯一流水号前缀
	 * seqFrom：从几开始
	 * seqCur： 当前数
	 * seqLength序列号位数
	 */
	public boolean insertGenSequence(String seqCodeName,int seqFrom,int seqCur,int seqLength,int maxSeq){
		boolean flag = true;
		String insertGenSeq = "insert into amazoninfo_generate_invoice_sequence(seq_codeName,current_seq,seq_length,seq_from,seq_max,updateTime) values(:p1,:p2,:p3,:p4,:p5,SYSDATE()) ";
		try{
			this.updateBySql(insertGenSeq,new Parameter(seqCodeName,seqCur,seqLength,seqFrom,maxSeq));
		}catch(Exception ex){
			flag=false;
		}
		return flag;
		
	}
}
