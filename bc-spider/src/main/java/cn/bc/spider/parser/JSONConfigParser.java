package cn.bc.spider.parser;

import cn.bc.core.exception.CoreException;
import org.json.JSONObject;

/**
 * 基于json配置的数据解析器
 *
 * @author dragon
 */
public class JSONConfigParser implements Parser<Object> {
  private JSONObject config;

  public JSONConfigParser(JSONObject config) {
    this.config = config;
  }

  public Object parse(Object data) {
    throw new CoreException(
      "not implements methods: cn.bc.spider.parser.JSONConfigParser.parse");
  }
}
