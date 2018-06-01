package cn.bc.acl.service;

import cn.bc.acl.domain.AccessActor;
import cn.bc.core.service.CrudService;

import java.util.List;

/**
 * 访问者Service接口
 *
 * @author lbj
 */
public interface AccessActorService extends CrudService<AccessActor> {


  /**
   * 获取某一访问对象中的某一访问者
   *
   * @param pid 访问对象id
   * @param aid 访问者id
   * @return
   */
  AccessActor load(Long pid, Long aid);

  /**
   * 获取某一访问对象中的全部访问者
   *
   * @param pid 访问对象id1
   * @return
   */
  List<AccessActor> findByPid(Long pid);


  /**
   * CRUD'D:删除对象
   *
   * @param accessActor 对象
   */
  void delete(AccessActor accessActor);

  /**
   * CRUD'D:删除对象
   *
   * @param accessActors 对象
   */
  void delete(List<AccessActor> accessActors);

  /**
   * 查找对象
   *
   * @param docId
   * @param docType
   * @param aid      用户id
   * @param wildcard 权限通配符
   * @return
   */
  AccessActor load(String docId, String docType, Long aid, String wildcard);
}