package cn.bc.sync.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.sync.domain.SyncBase;

import java.util.List;

/**
 * 同步Dao
 *
 * @author rongjih
 */
public interface SyncBaseDao extends CrudDao<SyncBase> {
  /**
   * 判断是否已有同步记录
   *
   * @param syncType 同步信息的类型
   * @param syncCode 同步信息的标识符
   * @return 如果曾经有相应的同步记录返回true，否则返回false
   */
  boolean hadSync(String syncType, String syncCode);

  /**
   * 获取信息的现有同步记录
   *
   * @param syncType 同步信息的类型
   * @param syncCode 同步信息的标识符
   * @return
   */
  SyncBase load(String syncType, String syncCode);

  /**
   * 判断该同步记录是否已生成过相应的事件单
   *
   * @param syncTo 判断事件单是否存在的表名
   * @param id     同步记录的id
   * @return
   */
  boolean hadGenerate(String syncTo, Long id);

  /**
   * 更新记录的状态值为新的值
   *
   * @param _ids     记录的id列表
   * @param toStatus 新的状态值
   * @return 实际更新的条目数
   */
  int updateStatus(Long[] _ids, int toStatus);

  /**
   * 将旧记录的状态更新为未处理
   *
   * @param syncType
   * @param syncCodes
   * @param statusNew
   * @return 实际更新的条目数
   */
  int updateStatus2New(String syncType, List<String> syncCodes);

  /**
   * 将其他未处理记录设置为已处理
   *
   * @param syncType
   * @param excludeSyncCodes
   * @return 实际更新的条目数
   */
  int updateNewStatus2Done4ExcludeCode(String syncType,
                                       List<String> excludeSyncCodes);
}
