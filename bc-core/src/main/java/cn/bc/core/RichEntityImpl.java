/**
 *
 */
package cn.bc.core;

import cn.bc.BCConstants;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 带UID和状态的基本实体接口的实现
 *
 * @author dragon
 */
@MappedSuperclass
public abstract class RichEntityImpl extends EntityImpl implements cn.bc.core.RichEntity<Long> {
  private static final long serialVersionUID = 7826313222480961654L;
  private String uid;
  private int status = BCConstants.STATUS_ENABLED;

  @Column(name = "UID_")
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
