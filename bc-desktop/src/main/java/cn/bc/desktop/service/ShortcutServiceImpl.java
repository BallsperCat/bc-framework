package cn.bc.desktop.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.desktop.dao.ShortcutDao;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * 桌面快捷方式service接口的实现
 *
 * @author dragon
 */
public class ShortcutServiceImpl extends DefaultCrudService<Shortcut> implements
  ShortcutService {
  private ShortcutDao shortcutDao;

  @Autowired
  public void setShortcutDao(ShortcutDao shortcutDao) {
    this.shortcutDao = shortcutDao;
    this.setCrudDao(shortcutDao);
  }

  public List<Shortcut> findCommon() {
    return this.shortcutDao.findByActor(null, false, false);
  }

  public List<Shortcut> findByActor(Long actorId) {
    return this.shortcutDao.findByActor(actorId, true, true);
  }

  public List<Shortcut> findByActor(Long actorId, boolean includeAncestor,
                                    boolean includeCommon) {
    return this.shortcutDao.findByActor(actorId, includeAncestor,
      includeCommon);
  }

  public List<Shortcut> findByActor(Long actorId,
                                    Set<Actor> ancestorOrganizations, Set<Role> roles,
                                    Set<Resource> modules) {
    return this.shortcutDao.findByActor(actorId, true, true,
      ancestorOrganizations, roles, modules);
  }
}
