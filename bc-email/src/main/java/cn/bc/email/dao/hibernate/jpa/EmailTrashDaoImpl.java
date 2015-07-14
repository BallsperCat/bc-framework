package cn.bc.email.dao.hibernate.jpa;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.email.dao.EmailTrashDao;
import cn.bc.email.domain.EmailTrash;
import cn.bc.orm.jpa.JpaCrudDao;

import java.util.List;

/**
 * 邮件垃圾箱Dao接口的实现
 *
 * @author lbj
 */
public class EmailTrashDaoImpl extends JpaCrudDao<EmailTrash> implements EmailTrashDao {
	public List<EmailTrash> find4resumableByOwnerId(Long ownerId) {
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("owner.id", ownerId));
		condition.add(new EqualsCondition("status", EmailTrash.STATUS_RESUMABLE));
		return this.createQuery().condition(condition).list();
	}
}