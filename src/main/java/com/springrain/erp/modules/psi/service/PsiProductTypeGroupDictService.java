/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiProductTypeGroupDictDao;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;

/**
 * 类型分组Service
 * @author 
 * @version 2015-6-11
 */
@Component
@Transactional(readOnly = true)
public class PsiProductTypeGroupDictService extends BaseService {

	@Autowired
	private PsiProductTypeGroupDictDao dictDao;
	
	@Autowired
	private PsiInventoryFbaService fbaService;
	
	@Autowired
	private SystemService systemService;
	
	public PsiProductTypeGroupDict get(String id) {
		return dictDao.get(id);
	}
	
	public List<PsiProductTypeGroupDict> getAllList(){
		DetachedCriteria dc = dictDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		dc.addOrder(Order.asc("name"));
		return dictDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiProductTypeGroupDict groupDict) {
		dictDao.save(groupDict);
	}
	
	public Map<String,String> getProductLine(){
		String sql="SELECT id,NAME FROM psi_product_type_dict WHERE del_flag='0' ORDER BY create_time ASC ";
		List<Object[]> list=dictDao.findBySql(sql);
		Map<String,String> map=Maps.newLinkedHashMap();
		for (Object[] obj : list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	//获取系统中的所有产品线简称
	public List<String> getAllLineShotrName(){
		List<String> rs = Lists.newArrayList();
		String sql="SELECT NAME FROM psi_product_type_dict WHERE del_flag='0' ORDER BY NAME ";
		List<Object> list=dictDao.findBySql(sql);
		for (Object obj : list) {
			String name = obj.toString();
			name = name.substring(0, 1);	//取简称,如A/B/C/D/E
			rs.add(name);
		}
		return rs;
	}
	
	 public boolean isExistName(String groupName){
		 boolean flag=true;
		 String sql="select * from psi_product_type_dict where name='"+groupName+"' and del_flag='0' ";
		 if(dictDao.findBySql(sql).size()>0){
			 flag=false;
		 }
		 return flag;
	 }
    
	 public List<Dict> getProductType(String groupId){
		 List<Object[]> list;
		    if("unGrouped".equals(groupId)){
		    	String sql="SELECT t.id,t.label,t.value FROM sys_dict t " +
						"WHERE (t.id NOT IN(SELECT g.dict_id FROM psi_product_type_group g) OR t.id IN (SELECT g.dict_id FROM psi_product_type_group g WHERE g.id IS NULL OR g.`id`='')) AND  " +
						"   t.`del_flag`='0' AND t.`type`='product_type' ";
		    	list=dictDao.findBySql(sql);
		    }else{
		    	String sql="SELECT t.id,t.label,t.value FROM sys_dict t JOIN psi_product_type_group g ON t.id=g.`dict_id` " +
						"  WHERE t.`del_flag`='0' AND t.`type`='product_type' and g.id=:p1 ";
		    	list=dictDao.findBySql(sql,new Parameter(groupId));
		    }
			 
			 List<Dict> productTypeList=new ArrayList<Dict>();
			 for (Object[] objects : list) {
				 Dict dict=new Dict();
				 dict.setId(objects[0].toString());
				 dict.setLabel(objects[1].toString());
				 dict.setValue(objects[2].toString());
				 productTypeList.add(dict);
			 }
			 return productTypeList;
		}
	 
	   public List<Dict> getProductUnGroupedType(){
			String sql="SELECT t.id,t.label,t.value FROM sys_dict t " +
					"WHERE (t.id NOT IN(SELECT g.dict_id FROM psi_product_type_group g) OR t.id IN (SELECT g.dict_id FROM psi_product_type_group g WHERE g.id IS NULL OR g.`id`='')) AND  " +
					"   t.`del_flag`='0' AND t.`type`='product_type' ";
			 List<Object[]> list=dictDao.findBySql(sql);
			 List<Dict> productTypeList=new ArrayList<Dict>();
			 for (Object[] objects : list) {
				 Dict dict=new Dict();
				 dict.setId(objects[0].toString());
				 dict.setLabel(objects[1].toString());
				 dict.setValue(objects[2].toString());
				 productTypeList.add(dict);
			 }
			 return productTypeList;
		}
	   
	   /**
	    * 获得产品线对应的产品set
	    */
	   public Map<String,Set<Integer>> getLineProductIds(){
		   String sql="SELECT a.`id` AS productId,p.id FROM psi_product a  JOIN sys_dict t ON a.type=t.`value` AND t.`type`='product_type' AND t.`del_flag`='0'  AND a.`del_flag`='0' " +
		   		" JOIN psi_product_type_group g ON t.id=g.`dict_id`  JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'";
			 List<Object[]> list=dictDao.findBySql(sql);
			 Map<String,Set<Integer>>  res  = Maps.newHashMap();
			 for (Object[] objects : list) {
				Integer productId =Integer.parseInt(objects[0].toString());
				String  lineId = objects[1].toString();
				Set<Integer> productIds=null;
				if(res.get(lineId)==null){
					productIds=Sets.newHashSet();
				}else{
					productIds=res.get(lineId);
				}
				productIds.add(productId);
				res.put(lineId, productIds);
			 }
			return res; 
	   }
	   
	   
	public List<PsiProductTypeGroupDict> getAllGroupList(){
		String sql="SELECT t.id,t.label NAME,IFNULL	(d.id,0) parent_id  FROM sys_dict t " +
				" LEFT JOIN psi_product_type_group g ON g.`dict_id`=t.`id`" +
				" LEFT JOIN psi_product_type_dict d ON d.`id`=g.`id` AND d.`del_flag`='0'" +
				" WHERE t.type='product_type' AND t.del_flag='0' " +
				" UNION  SELECT t.`id`,t.name,t.parent_id  FROM psi_product_type_dict t WHERE t.del_flag='0'";
		 List<Object[]> list=dictDao.findBySql(sql);
		 List<PsiProductTypeGroupDict> groupDictList=new ArrayList<PsiProductTypeGroupDict>();
		 for (Object[] objects : list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(objects[0].toString());
			 dict.setName(objects[1].toString());
			 PsiProductTypeGroupDict dictParent=new PsiProductTypeGroupDict();
			 dictParent.setId(objects[2].toString());
			 dict.setParent(dictParent);
			 groupDictList.add(dict);
		 }
		 return groupDictList;
	}
	
	/**
	 *更新产品经理、产品类型 
	 */
	@Transactional(readOnly = false)
	public void updateManageRelation(){
		String sql="SELECT dict_id FROM psi_product_manage_group WHERE user_id IN (SELECT id FROM sys_user WHERE del_flag='1' )";
		List<String> list=dictDao.findBySql(sql);
		String deleteSql="delete from psi_product_manage_group where dict_id=:p1 ";
		if(list!=null&&list.size()>0){
			for (String dict_id : list) {
				this.dictDao.updateBySql(deleteSql, new Parameter(dict_id));
			}
		}
	}
	
	/**
	 *查询所有：产品经理、产品类型 
	 */
	public List<PsiProductTypeGroupDict> getAllManageGroupList(){
		List<User> users =systemService.findUserByPermission("psi:product:manager");//产品经理
		String sql="SELECT t.id,t.label NAME,IFNULL	(d.id,0) parent_id  FROM sys_dict t"+
		        " left join psi_product_manage_group g on g.dict_id=t.id "+
				" left join sys_user d on d.id=g.user_id "+
				" WHERE t.type='product_type' AND t.del_flag='0' "+
				" UNION  SELECT u.`id`,u.name,0 FROM sys_user u where u.del_flag='0' and u.id in :p1 ";
		 List<Object[]> list=dictDao.findBySql(sql,new Parameter(users));
		 List<PsiProductTypeGroupDict> groupDictList=new ArrayList<PsiProductTypeGroupDict>();
		 for (Object[] objects : list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(objects[0].toString());
			 dict.setName(objects[1].toString());
			 PsiProductTypeGroupDict dictParent=new PsiProductTypeGroupDict();
			 dictParent.setId(objects[2].toString());
			 dict.setParent(dictParent);
			 groupDictList.add(dict);
		 }
		 return groupDictList;
	}
	
	/**
	 *更新产品经理、产品类型 
	 */
	@Transactional(readOnly = false)
	public void updatePurchaseRelation(){
		String sql="SELECT dict_id FROM psi_product_purchase_group WHERE user_id IN (SELECT id FROM sys_user WHERE del_flag='1' )";
		List<String> list=dictDao.findBySql(sql);
		String deleteSql="delete from psi_product_purchase_group where dict_id=:p1 ";
		if(list!=null&&list.size()>0){
			for (String dict_id : list) {
				this.dictDao.updateBySql(deleteSql, new Parameter(dict_id));
			}
		}
	}
	
	/**
	 *查询所有：产品经理、产品类型 
	 */
	public List<PsiProductTypeGroupDict> getAllPurchaseGroupList(){
		List<User> users =systemService.findUserByPermission("psi:purchase:manager");
		String sql="SELECT t.id,t.label NAME,IFNULL	(d.id,0) parent_id  FROM sys_dict t"+
		        " left join psi_product_purchase_group g on g.dict_id=t.id "+
				" left join sys_user d on d.id=g.user_id "+
				" WHERE t.type='product_type' AND t.del_flag='0' "+
				" UNION  SELECT u.`id`,u.name,0 FROM sys_user u  where  u.del_flag='0' and u.id in :p1  ";
		 List<Object[]> list=dictDao.findBySql(sql,new Parameter(users));
		 List<PsiProductTypeGroupDict> groupDictList=new ArrayList<PsiProductTypeGroupDict>();
		 for (Object[] objects : list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(objects[0].toString());
			 dict.setName(objects[1].toString());
			 PsiProductTypeGroupDict dictParent=new PsiProductTypeGroupDict();
			 dictParent.setId(objects[2].toString());
			 dict.setParent(dictParent);
			 groupDictList.add(dict);
		 }
		 return groupDictList;
	}
	
	
	public List<PsiProductTypeGroupDict> getAllGroupTypeProductList(){
		String sql="SELECT concat(t.id,'_','T'),concat(t.brand,' ',t.model) NAME, g.id parentId,is_sale  FROM psi_product t " +
				" JOIN sys_dict g ON g.value=t.TYPE " +
				" WHERE g.type='product_type' AND t.del_flag='0' and g.del_flag='0' and t.type is not null ";
		 List<Object[]> list=dictDao.findBySql(sql);
		 List<PsiProductTypeGroupDict> groupDictList=new ArrayList<PsiProductTypeGroupDict>();
		 for (Object[] objects : list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(objects[0].toString());
			 dict.setName(objects[1].toString());
			 PsiProductTypeGroupDict dictParent=new PsiProductTypeGroupDict();
			 dictParent.setId(objects[2].toString());
			 dict.setParent(dictParent);
			 dict.setDelFlag(objects[3].toString());
			 groupDictList.add(dict);
		 }
		 return groupDictList;
	}
	
	public List<PsiProductTypeGroupDict> getAllGroupTypeProductList(String type){
		String sql="SELECT concat(t.id,'_','T'),concat(t.brand,' ',t.model) NAME, g.id parentId,is_sale  FROM psi_product t " +
				" JOIN sys_dict g ON g.value=t.TYPE " +
				" WHERE g.type='product_type' AND t.del_flag='0' and g.del_flag='0' and t.type is not null ";
		 List<Object[]> list=dictDao.findBySql(sql);
		 List<PsiProductTypeGroupDict> groupDictList=new ArrayList<PsiProductTypeGroupDict>();
		 Map<String,Integer> fbaStock=Maps.newHashMap();
		 if("1".equals(type)){
			 fbaStock=fbaService.getAllCountryFbaInventory();
		 }
		 for (Object[] objects : list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(objects[0].toString());
			 dict.setName(objects[1].toString());
			 PsiProductTypeGroupDict dictParent=new PsiProductTypeGroupDict();
			 dictParent.setId(objects[2].toString());
			 dict.setParent(dictParent);
			 dict.setDelFlag(objects[3].toString());
			 if("1".equals(type)){
				 if("0".equals(objects[3].toString())){//已淘汰
					 if(fbaStock!=null&&fbaStock.get(objects[1].toString())!=null&&fbaStock.get(objects[1].toString())>0){
						 groupDictList.add(dict); 
					 }
				 }else{
					 groupDictList.add(dict); 
				 }
			 }else{
				 groupDictList.add(dict); 
			 }
			
		 }
		 return groupDictList;
	}
	
	@Transactional(readOnly = false)
    public boolean insertRelation(String[] dict_ids,String id){
    	boolean flag=true;
    	String insertGenSeq = "insert into psi_product_type_group(dict_id,id) values(:p1,:p2) ";
    	String deleteSql="delete from psi_product_type_group where dict_id=:p1 ";
		try{
			for (String dict_id : dict_ids) {
				this.dictDao.updateBySql(deleteSql, new Parameter(dict_id));
				this.dictDao.updateBySql(insertGenSeq,new Parameter(dict_id,id));
			}
			//修改后更新当前月产品类型对应的产品线关系
			saveTypeLine(null);
		}catch(Exception ex){
			flag=false;
		}
		return flag;
    }
		
	/**
	 *插入产品经理、产品类型关系 
	 */
	@Transactional(readOnly = false)
    public boolean insertManageRelation(String[] dict_ids,String id){
    	boolean flag=true;
    	String insertGenSeq = "insert into psi_product_manage_group(dict_id,user_id) values(:p1,:p2) ";
    	String deleteSql="delete from psi_product_manage_group where dict_id=:p1 ";
		try{
			for (String dict_id : dict_ids) {
				this.dictDao.updateBySql(deleteSql, new Parameter(dict_id));
				this.dictDao.updateBySql(insertGenSeq,new Parameter(dict_id,id));
			}
		}catch(Exception ex){
			flag=false;
		}
		return flag;
    }
	
	/**
	 *插入采购经理、产品类型关系 
	 */
	@Transactional(readOnly = false)
    public boolean insertPurchaseRelation(String[] dict_ids,String id){
    	boolean flag=true;
    	String insertGenSeq = "insert into psi_product_purchase_group(dict_id,user_id) values(:p1,:p2) ";
    	String deleteSql="delete from psi_product_purchase_group where dict_id=:p1 ";
		try{
			for (String dict_id : dict_ids) {
				this.dictDao.updateBySql(deleteSql, new Parameter(dict_id));
				this.dictDao.updateBySql(insertGenSeq,new Parameter(dict_id,id));
			}
		}catch(Exception ex){
			flag=false;
		}
		return flag;
    }
	
	/**
	 *产品线删除
	 * 
	 */
	@Transactional(readOnly = false)
    public boolean deleteNode(String[] ids){
    	boolean flag=true;
    	String updateSql="update psi_product_type_dict set del_flag='1' where id=:p1 ";
    	String deleteSql="delete from psi_product_type_group where id=:p1 ";
    	String updateGroupUserSql="UPDATE psi_product_group_user AS a SET del_flag='1' WHERE a.`product_group_id`=:p1 ";
		try{
			for (String id : ids) {
				this.dictDao.updateBySql(deleteSql, new Parameter(id));
				this.dictDao.updateBySql(updateSql, new Parameter(id));
				//对产品线与用户关系表进行删除操作。
				this.dictDao.updateBySql(updateGroupUserSql, new Parameter(id));
			}
		}catch(Exception ex){
			flag=false;
		}
		return flag;
    }

	//产品类型对应的产品线map[type	line]
	public Map<String, String> getTypeLine(String month) {
		Map<String, String> rs = Maps.newHashMap();
//		String sql="SELECT t.`value`,p.`name` FROM sys_dict t,psi_product_type_dict p,`psi_product_type_group` g"+
//				" WHERE t.`id`=g.`dict_id` AND p.`id`=g.`id` AND t.`type`='product_type' AND t.`del_flag`='0'";
		if (StringUtils.isEmpty(month)) {
			String sql = "SELECT MAX(t.`month`) FROM `psi_product_type_line` t";
			List<Object> list = dictDao.findBySql(sql);
			if (list != null && list.size() > 0) {
				month = list.get(0).toString();
			}
		}
		String sql = "SELECT t.`type`,t.`line` FROM `psi_product_type_line` t WHERE t.`month`=:p1"; 
		List<Object[]> list=dictDao.findBySql(sql, new Parameter(month));
		 for (Object[] objects : list) {
			 rs.put(objects[0].toString().toLowerCase(), objects[1].toString());
		 }
		return rs;
	}
	
	//产品名称 lineId
    public Map<String,String> getLineByName(){
    	Map<String, String> rs = Maps.newHashMap();
    	String sql="SELECT d.name,p.id FROM ( "+
			" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+
			" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
			" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			" JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
			" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' ";
    	 List<Object[]> list=dictDao.findBySql(sql);
		 for (Object[] obj: list) {
			 rs.put(obj[0].toString(), obj[1].toString());
		 }
    	 return rs;
    }
    
  //产品名称 lineId
    public Map<String,String> getLineNameByName(){
    	Map<String, String> rs = Maps.newHashMap();
    	String sql="SELECT d.name,p.name lineName FROM ( "+
			" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+
			" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
			" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			" JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
			" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' ";
    	 List<Object[]> list=dictDao.findBySql(sql);
		 for (Object[] obj: list) {
			 rs.put(obj[0].toString(), obj[1].toString());
		 }
    	 return rs;
    }
    
    public Map<String,PsiProductTypeGroupDict> getLineByProductName(){
    	Map<String, PsiProductTypeGroupDict> rs = Maps.newHashMap();
    	String sql="SELECT d.name,p.name lineName,p.id lineId FROM ( "+
			" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+
			" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
			" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			" JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
			" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' ";
    	 List<Object[]> list=dictDao.findBySql(sql);
		 for (Object[] obj: list) {
			 PsiProductTypeGroupDict dict=new PsiProductTypeGroupDict();
			 dict.setId(obj[2].toString());
			 dict.setName(obj[1].toString());
			 rs.put(obj[0].toString(),dict);
		 }
    	 return rs;
    }
	
	//所有在售的产品类型
    public List<String> getAllProductTypeList(){
    	String sql="SELECT t.`TYPE` FROM psi_product t WHERE t.`del_flag`='0' GROUP BY t.`TYPE` ";
    	return dictDao.findBySql(sql);
    }
	
    /**
     * 保存当前产品类型对应的产品线关系
     * @param month yyyyMM
     */
    @Transactional(readOnly=false)
    public void saveTypeLine(String month){
    	if (StringUtils.isEmpty(month)) {
    		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
    		month = monthFormat.format(new Date());
		}
    	String sql = "INSERT INTO `psi_product_type_line`(TYPE,line,MONTH) SELECT t.`value`,LEFT(p.`name`,1) AS line,'"+month+"' FROM sys_dict t,psi_product_type_dict p,`psi_product_type_group` g "+
				" WHERE t.`id`=g.`dict_id` AND p.`id`=g.`id` AND t.`type`='product_type' AND t.`del_flag`='0' ORDER BY p.name "+
				" ON DUPLICATE KEY UPDATE line=VALUES(line)";
    	dictDao.updateBySql(sql, null);
    }
    
    public List<String> getProductNameByLineId(String lineId){
    	String sql="SELECT d.name FROM ( "+
			" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+
			" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
			" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			" JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
			" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' where p.id=:p1 ";
    	 List<String> list=dictDao.findBySql(sql,new Parameter(lineId));
    	 return list;
    }
    
    @Transactional(readOnly = false)
    public void insertManageRelation(String dictId,String userId){
    	String insertGenSeq = "insert into psi_product_manage_group(dict_id,user_id) values(:p1,:p2) ";
    	String deleteSql="delete from psi_product_manage_group where dict_id=:p1 ";
		this.dictDao.updateBySql(deleteSql, new Parameter(dictId));
		this.dictDao.updateBySql(insertGenSeq,new Parameter(dictId,userId));
    }
    
    @Transactional(readOnly = false)
    public void deleteManageRelation(String dictId){
    	String deleteSql="delete from psi_product_manage_group where dict_id=:p1 ";
		this.dictDao.updateBySql(deleteSql, new Parameter(dictId));
    }

	//负责的产品线_国家
	public Set<String> getCountryLines(String userId, List<String> countryList) {
		Set<String> rs = Sets.newHashSet();
		if (StringUtils.isEmpty(userId)) {
			return rs;
		}
		//运营产品线
		String sql = "SELECT d.`name`,t.`country` FROM `psi_product_group_user` t,psi_product_type_dict d"+
				" WHERE t.`product_group_id`=d.`id` AND d.`del_flag`='0' AND t.`del_flag`='0' AND t.`responsible` LIKE :p1 AND t.`product_group_id`!='0'"; 
		List<Object[]> list = dictDao.findBySql(sql, new Parameter("%" + userId + "%"));
		for (Object[] obj : list) {
			String line = obj[0].toString().substring(0, 1);//取简称,如A/B/C/D/E)
			String country = obj[1].toString();
			rs.add(line+"_"+country);	//取简称,如A/B/C/D/E)
		}
		//产品经理产品线
		sql = "SELECT d.`name`,1 FROM `psi_product_manage_group` t,psi_product_type_dict d"+
				" WHERE t.`dict_id`=d.`id` AND d.`del_flag`='0' AND t.`user_id` LIKE :p1";
		list = dictDao.findBySql(sql, new Parameter("%" + userId + "%"));
		for (Object[] obj : list) {
			String line = obj[0].toString().substring(0, 1);//取简称,如A/B/C/D/E)
			for (String country : countryList) {
				rs.add(line+"_"+country);
			}
		}
		//客服产品线
		sql = "SELECT d.`name`,t.`country` FROM `psi_product_group_customer` t,psi_product_type_dict d"+
				" WHERE t.`line_id`=d.`id` AND t.`del_flag`='0' AND d.`del_flag`='0' AND t.`del_flag`='0' AND t.`user_id`=:p1 ";
		list = dictDao.findBySql(sql, new Parameter(userId));
		for (Object[] obj : list) {
			String line = obj[0].toString().substring(0, 1);//取简称,如A/B/C/D/E)
			String country = obj[1].toString();
			rs.add(line+"_"+country);	//取简称,如A/B/C/D/E)
		}
		return rs;
	}

	//负责的产品线
	public List<String> getSalesLines(String userId) {
		List<String> rs = Lists.newArrayList();
		if (userId == null) {
			return rs;
		}
		//运营产品线
		String sql = "SELECT DISTINCT d.`name` FROM `psi_product_group_user` t,psi_product_type_dict d "+
				" WHERE t.`product_group_id`=d.`id` AND d.`del_flag`='0' AND t.`del_flag`='0' "+
				" AND t.`responsible` LIKE :p1 AND t.`product_group_id`!='0' ORDER BY d.`name`"; 
		List<Object> list = dictDao.findBySql(sql, new Parameter("%" + userId + "%"));
		for (Object obj : list) {
			String line = obj.toString().substring(0, 1);//取简称,如A/B/C/D/E)
			rs.add(line);	//取简称,如A/B/C/D/E)
		}
		return rs;
	}
	
	
	  public List<String> getProductByLineId(String lineId){
	    	String sql="SELECT CONCAT(d.brand,' ',d.model) from psi_product d "+
				" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				" JOIN psi_product_type_group g ON t.id=g.`dict_id` "+
				" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' where p.id=:p1 and d.del_flag='0' ";
	    	 return dictDao.findBySql(sql,new Parameter(lineId));
	    }
    
    public static void main(String[] args) {
    	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiProductTypeGroupDictService  service = applicationContext.getBean(PsiProductTypeGroupDictService.class);
		service.saveTypeLine(null);
		service.getTypeLine(null);
		applicationContext.close();
    }
}
