/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.bc.core.EntityImpl;

/**
 * 默认的带文档创建信息的迷你实体基类
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class FileEntity extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Calendar fileDate;// 文档创建时间
	private Actor author;// 创建人
	private String subject;// 标题

	// 所属组织的冗余信息，用于提高统计效率用
	private Long departId;// 所属部门id
	private String departName;
	private Long unitId;// 所属单位id
	private String unitName;

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Actor.class)
	@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
	public Actor getAuthor() {
		return author;
	}

	public void setAuthor(Actor author) {
		this.author = author;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "AUTHOR_NAME")
	public String getAuthorName() {
		if (this.author != null) {
			return this.author.getName();
		} else {
			return null;
		}
	}

	public void setAuthorName(String authorName) {
		// do nothing
	}

	@Column(name = "DEPART_ID")
	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	@Column(name = "DEPART_NAME")
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	@Column(name = "UNIT_ID")
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
}