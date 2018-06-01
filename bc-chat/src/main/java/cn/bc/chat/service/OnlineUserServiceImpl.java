package cn.bc.chat.service;

import cn.bc.chat.OnlineUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 在线用户助手
 *
 * @author dragon
 */
public class OnlineUserServiceImpl implements OnlineUserService {
  private static Log logger = LogFactory.getLog(OnlineUserServiceImpl.class);
  private Map<String, OnlineUser> onlineUsers = new LinkedHashMap<String, OnlineUser>();

  public List<OnlineUser> getAll() {
    List<OnlineUser> all = new ArrayList<OnlineUser>(onlineUsers.values());
    Collections.reverse(all);// 按时间逆序排序
    return all;
  }

  public void add(OnlineUser onlineUser) {
    if (onlineUser == null)
      return;

    if (logger.isDebugEnabled()) {
      logger.debug("添加上线用户：" + onlineUser.toString());
    }
    this.onlineUsers.remove(onlineUser.getSid());
    this.onlineUsers.put(onlineUser.getSid(), onlineUser);
  }

  public void remove(String sid) {
    if (sid == null || sid.length() == 0)
      return;

    OnlineUser onlineUser = onlineUsers.remove(sid);
    if (logger.isDebugEnabled() && onlineUser != null) {
      logger.debug("移除下线用户：" + onlineUser.toString());
    }
  }

  public OnlineUser get(String sid) {
    return onlineUsers.get(sid);
  }
}
