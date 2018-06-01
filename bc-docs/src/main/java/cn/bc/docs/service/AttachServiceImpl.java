package cn.bc.docs.service;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.dao.AttachHistoryDao;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.identity.web.SystemContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * 附件service接口的实现
 *
 * @author dragon
 */
public class AttachServiceImpl extends DefaultCrudService<Attach> implements
  AttachService {
  private static Logger logger = LoggerFactory.getLogger(AttachServiceImpl.class);
  private AttachHistoryDao attachHistoryDao;

  @Autowired
  public void setAttachHistoryDao(AttachHistoryDao attachHistoryDao) {
    this.attachHistoryDao = attachHistoryDao;
  }

  public List<Attach> findByPtype(String ptype) {
    return this.findByPtype(ptype, null);
  }

  public List<Attach> findByPtype(String ptype, String puid) {
    AndCondition and = new AndCondition();
    if (ptype != null && ptype.length() > 0)
      and.add(new EqualsCondition("ptype", ptype));
    if (puid != null && puid.length() > 0)
      and.add(new EqualsCondition("puid", puid));

    and.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
    and.add(new OrderCondition("fileDate", Direction.Desc));

    return this.createQuery().condition(and).list();
  }

  public Attach loadByPtype(String ptype, String puid) {
    AndCondition and = new AndCondition();
    if (ptype != null && ptype.length() > 0)
      and.add(new EqualsCondition("ptype", ptype));
    if (puid != null && puid.length() > 0)
      and.add(new EqualsCondition("puid", puid));

    and.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
    and.add(new OrderCondition("fileDate", Direction.Desc));

    List<Attach> list = this.createQuery().condition(and).list(1, 1);
    if (list != null && !list.isEmpty()) {
      return list.get(0);
    } else {
      return null;
    }
  }

  public List<Attach> doCopy(String fromPtype, String fromPuid,
                             String toPtype, String toPuid, boolean keepAuthorInfo) {
    // 附件的真正存储路径
    String dataPath = Attach.DATA_REAL_PATH;

    Calendar now = Calendar.getInstance();
    if (logger.isDebugEnabled()) {
      logger.debug("复制附件:");
      logger.debug("dataPath={}", dataPath);
      logger.debug("fromPtype={}", fromPtype);
      logger.debug("fromPuid={}", fromPuid);
      logger.debug("toPtype={}", toPtype);
      logger.debug("toPuid={}", toPuid);
      logger.debug("keepAuthorInfo={}", keepAuthorInfo);
    }

    // 查找要复制的附件
    AndCondition c = new AndCondition();
    c.add(new EqualsCondition("puid", fromPuid))
      .add(new EqualsCondition("status", BCConstants.STATUS_ENABLED))
      .add(new OrderCondition("fileDate", Direction.Desc));
    if (fromPtype != null && fromPtype.length() > 0)
      c.add(new EqualsCondition("ptype", fromPtype));
    List<Attach> olds = this.createQuery().condition(c).list();
    if (null == olds || olds.isEmpty())
      return null;

    // 循环每一个附件进行拷贝
    List<Attach> news = new ArrayList<Attach>();
    Attach _new;
    for (Attach old : olds) {
      // _new = old.copy(dataPath, toPtype, toPuid);// 复制物理附件
      _new = new Attach();
      BeanUtils.copyProperties(old, _new);
      _new.setId(null);
      _new.setPtype(toPtype);
      _new.setPuid(toPuid);
      if (!keepAuthorInfo) {
        _new.setFileDate(now);

        // 设置文件的作者信息
        _new.setAuthor(SystemContextHolder.get().getUserHistory());
        _new.setModifiedDate(null);
        _new.setModifier(null);
      }
      news.add(_new);

      // ==拷贝具体的附件
      String sourcePath = dataPath + "/" + old.getPath();
      // 新文件存储的相对路径（年月），避免超出目录内文件数的限制
      String subFolder = new SimpleDateFormat("yyyyMM").format(now.getTime());
      // 要保存的新物理文件
      String realFileDir;// 所保存文件所在的目录的绝对路径名
      String relativeFilePath;// 所保存文件的相对路径名
      String realFilePath;// 所保存文件的绝对路径名
      String newFileName = toPtype
        + (toPtype.length() > 0 ? "_" : "")
        + UUID.randomUUID().toString().replace("-", "")
        + "." + old.getFormat();// 不含路径的文件名
      relativeFilePath = subFolder + "/" + newFileName;
      realFileDir = dataPath + "/" + subFolder;
      realFilePath = realFileDir + "/" + newFileName;
      _new.setPath(relativeFilePath);

      // 构建新文件要保存到的目录
      File _fileDir = new File(realFileDir);
      if (!_fileDir.exists()) {
        logger.debug("mkdir={}", realFileDir);
        _fileDir.mkdirs();
      }

      // TODO 保存一个附件记录

      // 检测源文件是否存在
      File source = new File(sourcePath);
      if (!source.exists()) {
        logger.warn("源文件已不存在，忽略复制：sourcePath={}", sourcePath);
        break;
      }

      // 复制源文件
      try {
        FileCopyUtils.copy(source, new File(realFilePath));
        //FileUtils.copyFile(source, new File(realFilePath));
      } catch (Exception e) {
        logger.warn("复制源文件失败，忽略不作处理：srcFile={}, destFile={}, error={}", sourcePath, realFilePath, e.getMessage());
        logger.info(e.getMessage(), e);
      }
    }

    // 保存新复制的附件记录
    this.save(news);

    // 返回复制的新附件记录
    return news;
  }

  public AttachHistory saveHistory(AttachHistory history) {
    return this.attachHistoryDao.save(history);
  }
}