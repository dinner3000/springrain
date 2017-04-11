package org.springrain.activity.entity;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springrain.frame.annotation.WhereSQL;
import org.springrain.frame.entity.BaseEntity;
/**
 * 活动参与人员个人信息
 * @copyright {@link weicms.net}
 * @author springrain<Auto generate>
 * @version  2017-03-13 16:47:49
 * @see org.springrain.activity.entity.ActivityMember
 */
@Table(name="t_activity_member")
public class ActivityMember  extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	//alias
	/*
	public static final String TABLE_ALIAS = "会员表";
	public static final String ALIAS_ID = "主键";
	public static final String ALIAS_OPENID = "微信ID";
	public static final String ALIAS_PORTRAIT = "头像";
	public static final String ALIAS_NICKNAME = "昵称";
	public static final String ALIAS_NAME = "姓名";
	public static final String ALIAS_TELEPHONE = "手机号";
	public static final String ALIAS_SITEID = "站点ID";
	public static final String ALIAS_BUINESSID = "业务ID";
	public static final String ALIAS_POINT = "积分";
	public static final String ALIAS_STAR = "星级";
	public static final String ALIAS_CREATETIME = "创建时间";
	public static final String ALIAS_CREATEPERSONID = "创建人ID";
    */
	//date formats
	//public static final String FORMAT_CREATETIME = DateUtils.DATETIME_FORMAT;
	
	//columns START
	/**
	 * 主键
	 */
	private java.lang.String id;
	/**
	 * 微信ID
	 */
	private java.lang.String openId;
	/**
	 * 头像
	 */
	private java.lang.String portrait;
	/**
	 * 昵称
	 */
	private java.lang.String nickname;
	/**
	 * 姓名
	 */
	private java.lang.String name;
	/**
	 * 手机号
	 */
	private java.lang.String telephone;
	/**
	 * 站点ID
	 */
	private java.lang.String siteId;
	/**
	 * 业务ID
	 */
	private java.lang.String buinessId;
	/**
	 * 积分
	 */
	private java.lang.Integer point;
	/**
	 * 星级
	 */
	private java.lang.String star;
	/**
	 * 创建时间
	 */
	private java.util.Date createTime;
	/**
	 * 创建人ID
	 */
	private java.lang.String createPersonID;
	//columns END 数据库字段结束
	
	//concstructor

	public ActivityMember(){
	}

	public ActivityMember(
		java.lang.String id
	){
		this.id = id;
	}

	//get and set
		/**
		 * 主键
		 */
	public void setId(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.id = value;
	}
	
	
	
	/**
	 * 主键
	 */
	@Id
     @WhereSQL(sql="id=:ActivityMember_id")
	public java.lang.String getId() {
		return this.id;
	}
		/**
		 * 微信ID
		 */
	public void setOpenId(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.openId = value;
	}
	
	
	
	/**
	 * 微信ID
	 */
     @WhereSQL(sql="openId=:ActivityMember_openId")
	public java.lang.String getOpenId() {
		return this.openId;
	}
		/**
		 * 头像
		 */
	public void setPortrait(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.portrait = value;
	}
	
	
	
	/**
	 * 头像
	 */
     @WhereSQL(sql="portrait=:ActivityMember_portrait")
	public java.lang.String getPortrait() {
		return this.portrait;
	}
		/**
		 * 昵称
		 */
	public void setNickname(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.nickname = value;
	}
	
	
	
	/**
	 * 昵称
	 */
     @WhereSQL(sql="nickname=:ActivityMember_nickname")
	public java.lang.String getNickname() {
		return this.nickname;
	}
		/**
		 * 姓名
		 */
	public void setName(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.name = value;
	}
	
	
	
	/**
	 * 姓名
	 */
     @WhereSQL(sql="name=:ActivityMember_name")
	public java.lang.String getName() {
		return this.name;
	}
		/**
		 * 手机号
		 */
	public void setTelephone(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.telephone = value;
	}
	
	
	
	/**
	 * 手机号
	 */
     @WhereSQL(sql="telephone=:ActivityMember_telephone")
	public java.lang.String getTelephone() {
		return this.telephone;
	}
		/**
		 * 站点ID
		 */
	public void setSiteId(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.siteId = value;
	}
	
	
	
	/**
	 * 站点ID
	 */
     @WhereSQL(sql="siteId=:ActivityMember_siteId")
	public java.lang.String getSiteId() {
		return this.siteId;
	}
		/**
		 * 业务ID
		 */
	public void setBuinessId(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.buinessId = value;
	}
	
	
	
	/**
	 * 业务ID
	 */
     @WhereSQL(sql="buinessId=:ActivityMember_buinessId")
	public java.lang.String getBuinessId() {
		return this.buinessId;
	}
		/**
		 * 积分
		 */
	public void setPoint(java.lang.Integer value) {
		this.point = value;
	}
	
	
	
	/**
	 * 积分
	 */
     @WhereSQL(sql="point=:ActivityMember_point")
	public java.lang.Integer getPoint() {
		return this.point;
	}
		/**
		 * 星级
		 */
	public void setStar(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.star = value;
	}
	
	
	
	/**
	 * 星级
	 */
     @WhereSQL(sql="star=:ActivityMember_star")
	public java.lang.String getStar() {
		return this.star;
	}
		/*
	public String getcreateTimeString() {
		return DateUtils.convertDate2String(FORMAT_CREATETIME, getcreateTime());
	}
	public void setcreateTimeString(String value) throws ParseException{
		setcreateTime(DateUtils.convertString2Date(FORMAT_CREATETIME,value));
	}*/
	
		/**
		 * 创建时间
		 */
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	
	
	/**
	 * 创建时间
	 */
     @WhereSQL(sql="createTime=:ActivityMember_createTime")
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
		/**
		 * 创建人ID
		 */
	public void setCreatePersonID(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.createPersonID = value;
	}
	
	
	
	/**
	 * 创建人ID
	 */
     @WhereSQL(sql="createPersonID=:ActivityMember_createPersonID")
	public java.lang.String getCreatePersonID() {
		return this.createPersonID;
	}
	
     @Override
	public String toString() {
		return new StringBuilder()
			.append("主键[").append(getId()).append("],")
			.append("微信ID[").append(getOpenId()).append("],")
			.append("头像[").append(getPortrait()).append("],")
			.append("昵称[").append(getNickname()).append("],")
			.append("姓名[").append(getName()).append("],")
			.append("手机号[").append(getTelephone()).append("],")
			.append("站点ID[").append(getSiteId()).append("],")
			.append("业务ID[").append(getBuinessId()).append("],")
			.append("积分[").append(getPoint()).append("],")
			.append("星级[").append(getStar()).append("],")
			.append("创建时间[").append(getCreateTime()).append("],")
			.append("创建人ID[").append(getCreatePersonID()).append("],")
			.toString();
	}
	
     @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
     @Override
	public boolean equals(Object obj) {
		if(obj instanceof ActivityMember == false){
			return false;
		} 
		if(this == obj){
			return true;
		} 
			
		ActivityMember other = (ActivityMember)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

	
