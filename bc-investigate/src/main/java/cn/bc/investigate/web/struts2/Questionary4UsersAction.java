/**
 *
 */
package cn.bc.investigate.web.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.*;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.domain.Questionary;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 调查问卷 Action
 *
 * @author zxr
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Questionary4UsersAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String status = String.valueOf(Questionary.STATUS_ISSUE);
  // public String status = String.valueOf(Questionary.STATUS_ISSUE) + ","
  // + String.valueOf(Questionary.STATUS_END);
  // public String userId = String.valueOf("");
  public String isResponse = "false";

  @Override
  public boolean isReadonly() {
    // 网上考试管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.question.exam"),
      getText("key.role.bc.admin"));
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    // 默认排序方向：创建日期
    return new OrderCondition("q.file_date", Direction.Desc);

  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    sql.append("select q.id,q.status_,q.subject,q.start_date,q.end_date");
    sql.append(",(select score from bc_ivg_respond where pid = q.id and author_id ="
      + this.getUserId() + ") score");
    sql.append(",(select count(*) from bc_ivg_question where pid = q.id) count,q.permitted");
    sql.append(",(select count(*) from bc_ivg_respond where pid = q.id) answerNumber");
    sql.append(",iss.actor_name issuer,q.issue_date,q.pigeonhole_date,pig.actor_name pigeonholer");
    sql.append(",q.file_date,ad.actor_name author");
    sql.append(" from bc_ivg_questionary q");
    sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ad on ad.id=q.author_id");
    sql.append(" left join BC_IDENTITY_ACTOR_HISTORY iss on iss.id=q.issuer_id");
    sql.append(" left join BC_IDENTITY_ACTOR_HISTORY pig on pig.id=q.pigeonholer_id");
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("status_", rs[i++]);
        map.put("subject", rs[i++]);
        map.put("start_date", rs[i++]);
        map.put("end_date", rs[i++]);
        map.put("score", rs[i++]);
        map.put("count", rs[i++]);
        map.put("permitted", rs[i++]);
        map.put("answerNumber", rs[i++]);
        map.put("issuer", rs[i++]);
        map.put("issue_date", rs[i++]);
        map.put("pigeonhole_date", rs[i++]);
        map.put("pigeonholer", rs[i++]);
        map.put("file_date", rs[i++]);
        map.put("author", rs[i++]);

        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("q.id", "id"));
    columns.add(new TextColumn4MapKey("q.status_", "score",
      getText("questionary.respondStatus"), 60).setSortable(true)
      .setValueFormater(new RespondStausFormater(getBSStatuses())));
    columns.add(new TextColumn4MapKey("q.status_", "status_",
      getText("questionary.status"), 60).setSortable(true)
      .setValueFormater(new KeyValueFormater(getBSStatuses())));
    columns.add(new TextColumn4MapKey("q.subject", "subject",
      getText("questionary.subject"), 250).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("q.start_date", "start_date",
      getText("questionary.Deadline"), 180)
      .setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
        @Override
        public Date getToDate(Object context, Object value) {
          @SuppressWarnings("rawtypes")
          Map contract = (Map) context;
          return (Date) contract.get("end_date");
        }
      }));
    columns.add(new TextColumn4MapKey("iss.actor_name", "score",
      getText("questionary.score"), 50).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("iss.actor_name", "count",
      getText("questionary.count"), 50).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("iss.actor_name", "answerNumber",
      getText("questionary.answerNumber"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("q.permitted", "permitted",
      getText("questionary.permitted"), 130).setSortable(true)
      .setUseTitleFromLabel(true)
      .setValueFormater(new BooleanFormater()));
    columns.add(new TextColumn4MapKey("iss.actor_name", "issuer",
      getText("questionary.issuer"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("q.issue_date", "issue_date",
      getText("questionary.issueDate"), 140).setSortable(true)
      .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
    columns.add(new TextColumn4MapKey("pig.actor_name", "pigeonholer",
      getText("questionary.pigeonholer"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("q.pigeonhole_date", "file_date",
      getText("questionary.pigeonholeDate"), 140).setSortable(true)
      .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
    columns.add(new TextColumn4MapKey("ad.actor_name", "author",
      getText("questionary.author"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("q.file_date", "file_date",
      getText("questionary.fileDate"), 140).setSortable(true)
      .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));

    return columns;
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"ac.actor_name", "q.subject",};
  }

  @Override
  protected String getFormActionName() {
    return "questionary4User";
  }

  @Override
  protected Condition getGridSpecalCondition() {
    AndCondition andCondition = new AndCondition();
    AndCondition andTimeCondition = new AndCondition();
    OrCondition orCondition = new OrCondition();
    SystemContext context = (SystemContext) this.getContext();
    // 保存的用户id键值集合
    List<Object> ids = new ArrayList<Object>();
    ids.add(context.getUserHistory().getId());
    // 根据集合数量，生成的占位符字符串
    String qlStr = "?";
    // 作答与全部的状态条件
    if (isResponse.equals("true")
      || (isResponse == null || isResponse.length() == 0)) {
      status = String.valueOf(Questionary.STATUS_ISSUE) + ","
        + String.valueOf(Questionary.STATUS_END);
    }

    // 状态条件
    Condition statusCondition = null;
    if (status != null && status.length() > 0) {
      String[] ss = status.split(",");
      if (ss.length == 1) {
        statusCondition = new EqualsCondition("q.status_", new Integer(
          ss[0]));
      } else {
        statusCondition = new InCondition("q.status_",
          StringUtils.stringArray2IntegerArray(ss));
      }
    }
    // 并且当前时间小于等于试卷的结束时间
    // 结束时间
    Condition endTimeCondition = null;
    endTimeCondition = new GreaterThanOrEqualsCondition("q.end_date",
      Calendar.getInstance());

    // 开始时间
    Condition stratTimeCondition = null;// 当前时间
    stratTimeCondition = new LessThanOrEqualsCondition("q.start_date",
      Calendar.getInstance());

    // 已作答
    if (isResponse.equals("true")) {
      andCondition.add(statusCondition, new QlCondition(
        "q.id in (select pid from bc_ivg_respond where author_id ="
          + qlStr + ")", ids));
    }
    // 未作答
    if (isResponse.equals("false")) {
      // 试卷不在作答表
      andCondition.add(statusCondition, new QlCondition(
        "q.id not in (select pid from bc_ivg_respond where author_id ="
          + qlStr + ")", ids));
      andCondition.add(endTimeCondition, stratTimeCondition);
    }

    // 全部
    if (isResponse == null || isResponse.length() == 0) {
      andCondition.add(
        statusCondition,
        orCondition.add(
          andTimeCondition.add(endTimeCondition,
            stratTimeCondition),
          new QlCondition(
            "q.id in (select pid from bc_ivg_respond where author_id ="
              + qlStr + ")", ids)).setAddBracket(
          true));
    }
    // SystemContext context = (SystemContext) this.getContext();

    // 用户是否有该试卷的考试权限
    // 保存的用户id键值集合
    List<Object> actorIds = new ArrayList<Object>();
    actorIds.add(context.getUser().getId());
    Long[] aids = context.getAttr(SystemContext.KEY_ANCESTORS);
    for (Long id : aids) {
      actorIds.add(id);
    }
    // 根据集合数量，生成的占位符字符串
    String qlStr4Actor = "";
    for (int i = 0; i < actorIds.size(); i++) {
      if (i + 1 != actorIds.size()) {
        qlStr4Actor += "?,";
      } else {
        qlStr4Actor += "?";
      }
    }
    andCondition
      .add(new OrCondition(
        // 如果不配置使用人，则所有人都有权限使用
        new QlCondition(
          "q.id not in(select r.qid from  bc_ivg_questionary_actor r)",
          new Object[]{}),
        // 试卷的作答人
        new QlCondition(
          "q.id in (select r.qid from  bc_ivg_questionary_actor r where r.aid in ("
            + qlStr4Actor + "))", actorIds))
        .setAddBracket(true));

    return andCondition;
  }

  /**
   * 获取用户ID
   *
   * @return
   */
  public Long getUserId() {
    SystemContext context = (SystemContext) this.getContext();
    return context.getUserHistory().getId();
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    super.extendGridExtrasData(json);

    // 状态条件
    if (this.status != null && this.status.trim().length() > 0) {
      json.put("status", status);
    }
    // // 用户条件
    // if (this.userId != null && this.userId.trim().length() > 0) {
    // json.put("userId", userId);
    // }
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
      .setHeight(400).setMinHeight(300).setHelp("wangshangkaoshi");
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['subject']";
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();
    // 查看按钮
    tb.addButton(this.getDefaultOpenToolbarButton());

    // // 如果是管理员,可以看到状态按钮组
    tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
      this.getResponseValue(), "isResponse", 1,
      getText("title.click2changeResponseStatus")));

    // tb.addButton(Toolbar.getDefaultEmptyToolbarButton());
    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }

  /**
   * 状态值转换列表：待发布|已发布|已归档|全部
   *
   * @return
   */
  protected Map<String, String> getBSStatuses() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put(String.valueOf(Questionary.STATUS_DRAFT),
      getText("questionary.release.wait"));
    statuses.put(String.valueOf(Questionary.STATUS_ISSUE),
      getText("questionary.release.already"));
    statuses.put(String.valueOf(Questionary.STATUS_END),
      getText("questionary.release.end"));
    statuses.put("", getText("questionary.release.all"));
    return statuses;
  }

  /**
   * 状态值转换列表：待发布|已发布|已归档|全部
   *
   * @return
   */
  protected Map<String, String> getResponseValue() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put("true", getText("questionary.response.true"));
    statuses.put("false", getText("questionary.response.false"));
    statuses.put("", getText("questionary.release.all"));
    return statuses;
  }

  @Override
  protected String getGridDblRowMethod() {
    // 如果作答表里有用户的记录则证明该用户已经作答过

    // 强制为只读表单
    return "bc.questionary4UserView.dblclick";
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getContextPath() + "/bc/questionary4User/view.js";
  }

  // ==高级搜索代码开始==
  @Override
  protected boolean useAdvanceSearch() {
    return true;
  }

  // ==高级搜索代码结束==

}
