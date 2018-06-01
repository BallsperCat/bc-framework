package cn.bc.orm.hibernate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HibernateUtils {

  /**
   * 剔除查询语句中的排序语句
   *
   * @param queryString 查询的语句
   * @return 如果存在排序语句，则将排序语句剔除，否则返回原查询语句
   */
  public static String removeOrderBy(String queryString) {
    Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
      Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(queryString);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "");
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * 剔除查询语句中的选择语句
   *
   * @param queryString 查询的语句
   * @return 如果存在选择语句，则将选择语句剔除，否则返回原查询语句
   */
  public static String removeSelect(String queryString) {
    queryString = queryString.replaceAll("[\\f\\n\\r\\t\\v]", " ");// 替换所有制表符、换页符、换行符、回车符为空格
    String regex = "^select .* from ";
    String[] ss = queryString.split(regex);
    if (ss.length > 0) {
      return "from " + ss[1];
    } else {
      return queryString;
    }
    // int beginPos = queryString.toLowerCase().indexOf("from ");
    // return beginPos != -1 ? queryString.substring(beginPos) :
    // queryString;
  }
}
