package com.springrain.erp.modules.psi.entity;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_product_group_photo")
public class PsiProductGroupPhoto {
	 private 		Integer  				id;                 // id
	   private 		String                  lineId;             // 产品线id
	   private 		String 					userId;             // 摄影师id组(逗号分隔)
	   private 		User 					createUser;         // 创建人
	   private 		Date 					createTime;         // 创建时间
	   private 		String 					delFlag;            // 删除状态
	   private      String                  name;
	   
	   @Transient
	   public String getName() {
		  return name;
	   }
	
	   public void setName(String name) {
		  this.name = name;
	   }

	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
		
		public String getLineId() {
			return lineId;
		}

		public void setLineId(String lineId) {
			this.lineId = lineId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		@Transient
		public String getUserIdComma(){
			return ","+this.userId+",";
		}

		@ManyToOne()
		@JoinColumn(name="create_user")
		@NotFound(action = NotFoundAction.IGNORE)
		public User getCreateUser() {
			return createUser;
		}
		
		public void setCreateUser(User createUser) {
			this.createUser = createUser;
		}
		
		public Date getCreateTime() {
			return createTime;
		}
		
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		

		public String getDelFlag() {
			return delFlag;
		}
		
		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}
		
		public PsiProductGroupPhoto(){}

		public PsiProductGroupPhoto(Integer  id,String lineId, String userId,User createUser, Date createTime, String delFlag) {
			super();
			this.id=id;
			this.lineId = lineId;
			this.userId = userId;
			this.createUser = createUser;
			this.createTime = createTime;
			this.delFlag=delFlag;
		}
		
}
