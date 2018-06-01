/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.condition.Condition;

import java.util.List;


/**
 * is not null条件
 *
 * @author dragon
 */
public class IsNotNullCondition implements Condition {
  protected String name;

  public IsNotNullCondition(String name) {
    this.name = name;
  }

  public String getExpression() {
    return getExpression(null);
  }

  public String getExpression(String alias) {
    String e = this.name;
    if (alias != null && alias.length() > 0)
      e = alias + "." + e;
    return e + " is not null";
  }

  public List<Object> getValues() {
    return null;
  }
}
