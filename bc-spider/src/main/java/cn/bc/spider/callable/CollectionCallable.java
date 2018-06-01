package cn.bc.spider.callable;

import cn.bc.core.util.JsonUtils;

import java.util.Collection;

/**
 * 解析响应为集合的网络请求(JsonArray转Collection)
 *
 * @author dragon
 */
public class CollectionCallable extends BaseCallable<Collection<Object>> {
  @Override
  public Collection<Object> parseResponse() throws Exception {
    return JsonUtils.toCollection(getResponseText());
  }
}