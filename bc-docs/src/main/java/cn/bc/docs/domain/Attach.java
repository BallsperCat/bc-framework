/**
 *
 */
package cn.bc.docs.domain;

import cn.bc.BCConstants;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.FileEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 附件
 * <p>
 * 记录文档与其相关附件之间的关系
 * </p>
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_DOCS_ATTACH")
public class Attach extends FileEntityImpl {
  private static final long serialVersionUID = 1L;

  /**
   * 附件存储的绝对路径，开头带"/"，末尾不要带"/"
   */
  public static String DATA_REAL_PATH = "/bcdata";
  /**
   * 附件存储的相对于应用部署目录下的相对路径，开头及末尾不要带"/"
   */
  public static String DATA_SUB_PATH = "uploads";

  private String puid;// 所关联文档的UID
  private String ptype;// 所关联文档的分类
  private String format;// 附件类型：如png、doc、mp3等
  private String path;// 物理文件保存的相对路径（相对于全局配置的app.data.realPath或app.data.subPath目录下的子路径，如"2011/bulletin/xxxx.doc"）
  private long size;// 文件的大小(单位为byte)
  private long count;// 文件的下载次数
  private int status = BCConstants.STATUS_ENABLED;// 详见Entity中的STATUS_常数
  private String subject;// 标题
  private String icon;// 扩展字段

  /**
   * path的值是相对于app.data.realPath目录下的路径还是相对于app.data.subPath目录下的路径：
   * false：相对于app.data.realPath目录下的路径， true：相对于app.data.subPath目录下的路径
   */
  private boolean appPath = false;//

  public void setDataRealPath(String dataRealPath) {
    DATA_REAL_PATH = dataRealPath;
  }

  public void setDataSubPath(String dataSubPath) {
    DATA_SUB_PATH = dataSubPath;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Column(name = "COUNT_")
  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public boolean isAppPath() {
    return appPath;
  }

  public void setAppPath(boolean appPath) {
    this.appPath = appPath;
  }

  @Column(name = "SIZE_")
  public long getSize() {
    return size;
  }

  /**
   * 获取附件大小的易读格式，如10Bytes、10.2KB、20.3MB
   *
   * @return
   */
  @Transient
  public String getSizeInfo() {
    return StringUtils.formatSize(this.getSize());
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getPtype() {
    return ptype;
  }

  public void setPtype(String ptype) {
    this.ptype = ptype;
  }

  public String getPuid() {
    return puid;
  }

  public void setPuid(String euid) {
    this.puid = euid;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}