package cn.bc.identity.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "BC_IDENTITY_ACTOR_DETAIL")
public class ActorDetail implements Serializable {
  private static final long serialVersionUID = 4863625345847183773L;
  private static Logger logger = LoggerFactory.getLogger(ActorDetail.class);
  /**
   * 性别:未定义
   */
  public static final int SEX_NONE = 0;
  /**
   * 性别:男
   */
  public static final int SEX_MAN = 1;
  /**
   * 性别:女
   */
  public static final int SEX_WOMAN = 2;

  private Long id;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return 创建时间
   */
  @Column(name = "CREATE_DATE")
  public Calendar getCreateDate() {
    return getCalendar("createDate");
  }

  public void setCreateDate(Calendar createDate) {
    set("createDate", createDate);
  }

  /**
   * @return 入职时间
   */
  @Column(name = "WORK_DATE")
  public Calendar getWorkDate() {
    return getCalendar("workDate");
  }

  public void setWorkDate(Calendar workDate) {
    set("workDate", workDate);
  }

  /**
   * @return 身份证
   */
  public String getCard() {
    return getString("card");
  }

  public void setCard(String card) {
    set("card", card);
  }

  /**
   * @return 备注
   */
  @Column(name = "COMMENT_")
  public String getComment() {
    return getString("comment");
  }

  public void setComment(String comment) {
    set("comment", comment);
  }

  /**
   * @return 性别(0 - 未设置, 1 - 男, 2 - 女)
   */
  @Column
  public int getSex() {
    Integer i = getInteger("sex");
    if (i == null)
      return 0;
    else
      return i.intValue();
  }

  public void setSex(int sex) {
    set("sex", new Integer(sex));
  }

  /**
   * @return 职务
   */
  @ManyToOne(fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "DUTY_ID", referencedColumnName = "ID")
  public Duty getDuty() {
    return (Duty) get("duty");
  }

  public void setDuty(Duty duty) {
    set("duty", duty);
  }

  @Transient
  private Map<String, Object> attrs;

  /**
   * @param key 键
   * @return 指定键的属性值
   */
  @Transient
  public Object get(String key) {
    return (attrs != null && attrs.containsKey(key)) ? attrs.get(key)
      : null;
  }

  public void set(String key, Object value) {
    if (logger.isDebugEnabled())
      logger.debug("key=" + key + ";value=" + value + ";valueType="
        + (value != null ? value.getClass() : "?"));
    if (key == null)
      throw new RuntimeException("key can't to be null");
    if (attrs == null)
      attrs = new HashMap<String, Object>();
    attrs.put(key, value);
  }

  @Transient
  public String getString(String key) {
    return (String) get(key);
  }

  @Transient
  public Boolean getBoolean(String key) {
    Object v = get(key);
    if (v != null) {
      return "true".equalsIgnoreCase(v.toString())
        || "yes".equalsIgnoreCase(v.toString())
        || "1".equalsIgnoreCase(v.toString());
    }
    return new Boolean(false);
  }

  @Transient
  public Integer getInteger(String key) {
    return (Integer) get(key);
  }

  @Transient
  public Long getLong(String key) {
    return (Long) get(key);
  }

  @Transient
  public Float getFloat(String key) {
    return (Float) get(key);
  }

  @Transient
  public Calendar getCalendar(String key) {
    return (Calendar) get(key);
  }

  @Transient
  public Date getDate(String key) {
    return (Date) get(key);
  }


  // extends for bc-business

  /**
   * @return iso
   */
  @Column
  public boolean isIso() {
    return getBoolean("iso").booleanValue();
  }

  public void setIso(boolean iso) {
    set("iso", new Boolean(iso));
  }
}
