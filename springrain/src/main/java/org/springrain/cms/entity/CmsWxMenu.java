package org.springrain.cms.entity;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springrain.frame.annotation.WhereSQL;
import org.springrain.frame.entity.BaseEntity;
/**
 * TODO 在此加入类描述
 * @copyright {@link weicms.net}
 * @author springrain<Auto generate>
 * @version  2017-02-06 17:23:12
 * @see org.springrain.cms.base.entity.CmsWxMenu
 */
@Table(name="cms_wx_menu")
public class CmsWxMenu  extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	//alias
	/*
	public static final String TABLE_ALIAS = "CmsWxMenu";
	public static final String ALIAS_ID = "id";
	public static final String ALIAS_NAME = "菜单名称";
	public static final String ALIAS_TYPE = "菜单类型";
	public static final String ALIAS_KEYWORD = "消息内容";
	public static final String ALIAS_URL = "跳转地址";
	public static final String ALIAS_PID = "上级菜单id";
	public static final String ALIAS_CREATEDATE = "创建时间";
    */
	//date formats
	//public static final String FORMAT_CREATEDATE = DateUtils.DATETIME_FORMAT;
	
	//columns START
	/**
	 * id
	 */
	private java.lang.String id;
	/**
	 * 菜单名称
	 */
	private java.lang.String name;
	/**
	 * 菜单类型
	 */
	private java.lang.String type;
	/**
	 * 消息内容
	 */
	private java.lang.String keyword;
	/**
	 * 跳转地址
	 */
	private java.lang.String url;
	/**
	 * 上级菜单id
	 */
	private java.lang.String pid;
	/**
	 * 创建时间
	 */
	private java.util.Date createDate;
	/**
	 * 站点id
	 */
	private java.lang.String siteId;
	//columns END 数据库字段结束
	private List<CmsWxMenu> childMenuList;
	
	//concstructor

	public CmsWxMenu(){
	}

	public CmsWxMenu(
		java.lang.String id
	){
		this.id = id;
	}

	//get and set
	public void setId(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.id = value;
	}
	
	@Id
     @WhereSQL(sql="id=:CmsWxMenu_id")
	public java.lang.String getId() {
		return this.id;
	}
	public void setName(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.name = value;
	}
	
     @WhereSQL(sql="name=:CmsWxMenu_name")
	public java.lang.String getName() {
		return this.name;
	}
	public void setType(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.type = value;
	}
	
     @WhereSQL(sql="type=:CmsWxMenu_type")
	public java.lang.String getType() {
		return this.type;
	}
	public void setKeyword(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.keyword = value;
	}
	
     @WhereSQL(sql="keyword=:CmsWxMenu_keyword")
	public java.lang.String getKeyword() {
		return this.keyword;
	}
	public void setUrl(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.url = value;
	}
	
     @WhereSQL(sql="url=:CmsWxMenu_url")
	public java.lang.String getUrl() {
		return this.url;
	}
	public void setPid(java.lang.String value) {
		    if(StringUtils.isNotBlank(value)){
			 value=value.trim();
			}
		this.pid = value;
	}
	
     @WhereSQL(sql="pid=:CmsWxMenu_pid")
	public java.lang.String getPid() {
		return this.pid;
	}
		/*
	public String getcreateDateString() {
		return DateUtils.convertDate2String(FORMAT_CREATEDATE, getcreateDate());
	}
	public void setcreateDateString(String value) throws ParseException{
		setcreateDate(DateUtils.convertString2Date(FORMAT_CREATEDATE,value));
	}*/
	
	public void setCreateDate(java.util.Date value) {
		this.createDate = value;
	}
	
     @WhereSQL(sql="createDate=:CmsWxMenu_createDate")
	public java.util.Date getCreateDate() {
		return this.createDate;
	}
     @WhereSQL(sql="siteId=:CmsWxMenu_siteId")
	public java.lang.String getSiteId() {
		return siteId;
	}
	 
	public void setSiteId(java.lang.String siteId) {
		this.siteId = siteId;
	}
	 @Transient
	public List<CmsWxMenu> getChildMenuList() {
		return childMenuList;
	}
	public void setChildMenuList(List<CmsWxMenu> childMenuList) {
		this.childMenuList = childMenuList;
	}

	public String toString() {
		return new StringBuffer()
			.append("id[").append(getId()).append("],")
			.append("菜单名称[").append(getName()).append("],")
			.append("菜单类型[").append(getType()).append("],")
			.append("消息内容[").append(getKeyword()).append("],")
			.append("跳转地址[").append(getUrl()).append("],")
			.append("上级菜单id[").append(getPid()).append("],")
			.append("创建时间[").append(getCreateDate()).append("],")
			.append("站点id[").append(getSiteId()).append("],")
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CmsWxMenu == false) return false;
		if(this == obj) return true;
		CmsWxMenu other = (CmsWxMenu)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}
}

	
