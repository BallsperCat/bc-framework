/**
 *
 */
package cn.bc.email.domain;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 邮件垃圾箱
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_EMAIL_TRASH")
public class EmailTrash extends EntityImpl {
  private static final long serialVersionUID = 1L;
  /**
   * 状态：可恢复
   */
  public static final int STATUS_RESUMABLE = 0;
  /**
   * 状态：已删除
   */
  public static final int STATUS_DELETED = 1;

  /**
   * 来源：发件箱
   */
  public static final int SOURCE_SEND = 1;
  /**
   * 来源：收件箱
   */
  public static final int SOURCE_TO = 2;

  private int status;// 状态
  private Email email;// 邮件
  private Actor owner;// 所有者
  private int source;//来源
  private Calendar handleDate;// 操作时间

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public Email getEmail() {
    return email;
  }

  public void setEmail(Email email) {
    this.email = email;
  }

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
  public Actor getOwner() {
    return owner;
  }

  public void setOwner(Actor reader) {
    this.owner = reader;
  }

  @Column(name = "HANDLE_DATE")
  public Calendar getHandleDate() {
    return handleDate;
  }

  public void setHandleDate(Calendar handleDate) {
    this.handleDate = handleDate;
  }

  @Column(name = "SRC")
  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }


}