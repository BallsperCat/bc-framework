package cn.bc.identity.service;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.test.AbstractEntityCrudTest;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ActorHistoryServiceImplTest extends
	AbstractEntityCrudTest<Long, ActorHistory> {
	ActorService actorService;
	ActorHistoryService actorHistoryService;

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
		this.crudOperations = actorHistoryService;// 赋值基类的crud操作对象
	}

	@Autowired
	public void setActorService(
		@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Override
	protected ActorHistory createInstance(String config) {
		ActorHistory actorHistory = new ActorHistory();

		//生成一个Actor
		Actor actor = createActor();
		this.actorService.save(actor);

		// 补充一些必填域的设置
		actorHistory.setCreateDate(Calendar.getInstance());
		actorHistory.setActorType(actor.getType());
		actorHistory.setActorId(actor.getId());
		actorHistory.setName("actorName");

		return actorHistory;
	}

	private Actor createActor() {
		Actor actor = new Actor();

		// 补充一些必填域的设置
		actor.setType(Actor.TYPE_USER);
		actor.setInner(false);
		actor.setStatus(BCConstants.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode("test");
		actor.setName("测试");

		return actor;
	}
}
