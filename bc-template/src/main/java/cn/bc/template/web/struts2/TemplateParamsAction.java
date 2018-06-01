package cn.bc.template.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 模板参数视图Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateParamsAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String status = String.valueOf(BCConstants.STATUS_ENABLED);

  @Override
  public boolean isReadonly() {
    // 模板管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：模板管理员
    return !context.hasAnyRole(getText("key.role.bc.template"),
      getText("key.role.bc.admin"));
  }

  @Override
  protected OrderCondition getGridOrderCondition() {
    return new OrderCondition("a.status_").add("a.order_", Direction.Asc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    sql.append("select a.id,a.status_ as status,a.order_ as orderNo,a.name,a.config");
    sql.append(",c.actor_name as mname,a.modified_date");
    sql.append(" from bc_template_param a");
    sql.append(" inner join bc_identity_actor_history b on b.id=a.author_id");
    sql.append(" left join bc_identity_actor_history c on c.id=a.modifier_id");
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("status", rs[i++]);
        map.put("orderNo", rs[i++]);
        map.put("name", rs[i++]);
        map.put("config", rs[i++]);
        map.put("mname", rs[i++]);
        map.put("modified_date", rs[i++]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("a.id", "id"));
    columns.add(new TextColumn4MapKey("a.status_", "status",
      getText("template.status"), 40).setSortable(true)
      .setValueFormater(new KeyValueFormater(this.getStatuses())));
    columns.add(new TextColumn4MapKey("a.order_", "orderNo",
      getText("template.order"), 60).setSortable(true));
    columns.add(new TextColumn4MapKey("a.name", "name",
      getText("template.name")).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("a.modified_date", "modified_date",
      getText("template.finalUpdate"), 190)
      .setValueFormater(new AbstractFormater<Object>() {
        @Override
        public Object format(Object context, Object value) {
          if (value == null || "".equals(value.toString()))
            return null;
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return map.get("mname") +
            " (" + DateUtils.formatDateTime2Minute((Date) value) + "）";
        }
      }));

    return columns;
  }

  // 状态键值转换
  private Map<String, String> getStatuses() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
      getText("template.status.normal"));
    statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
      getText("template.status.disabled"));
    statuses.put("", getText("template.status.all"));
    return statuses;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['name']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"a.name"};
  }

  @Override
  protected String getFormActionName() {
    return "templateParam";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(600).setMinWidth(400)
      .setHeight(400).setMinHeight(300);
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    if (!this.isReadonly()) {
      // 新建按钮
      tb.addButton(this.getDefaultCreateToolbarButton());
      // 编辑按钮
      tb.addButton(this.getDefaultEditToolbarButton());
      // 删除按钮
      tb.addButton(this.getDefaultDeleteToolbarButton());
    } else {
      tb.addButton(this.getDefaultOpenToolbarButton());
    }

    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }

}
