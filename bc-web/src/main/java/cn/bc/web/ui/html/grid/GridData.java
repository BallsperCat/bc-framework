package cn.bc.web.ui.html.grid;

import java.util.ArrayList;
import java.util.List;

import cn.bc.web.formater.ExportText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import cn.bc.core.util.StringUtils;
import cn.bc.web.formater.Formater;
import cn.bc.web.formater.LinkFormater;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Table;
import cn.bc.web.ui.html.Td;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Tr;

/**
 * 表格数据部分的html组件
 *
 * @author dragon
 */
public class GridData extends Div {
    private static final Log logger = LogFactory.getLog(GridData.class);
    /**
     * 表格数据行的默认样式
     */
    public final static String DEFAULT_ROW_CLASS = "ui-widget-content row";
    private List<? extends Object> data;
    private List<Column> columns = new ArrayList<Column>();
    private int pageNo;
    private int pageCount;
    private int totalCount;
    // 行的显示信息表达式
    private String rowLabelExpression;
    private ExpressionParser parser;

    public Object getValue(Object obj, String expression) {
        return getValue(obj, expression, parser);
    }

    /**
     * 获取对象指定表达式计算值的字符串表示
     *
     * @param obj
     * @param expression
     * @param parser
     * @return
     */
    public static Object getValue(Object obj, String expression,
                                  ExpressionParser parser) {
        if (parser == null)
            parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);
        EvaluationContext context = new StandardEvaluationContext(obj);
        try {
            return exp.getValue(context);
        } catch (EvaluationException e) {
            logger.warn(e.getMessage());
            return "";
        }
    }

    public static Object formatValue(Object context, Object cellValue,
                                     Formater<? extends Object> formater) {
        Object value;
        if (formater != null)
            value = formater.format(context, cellValue);
        else
            value = cellValue;
        return value;
    }

    public static Object formatValue2Label(Object context, Object cellValue,
                                           Formater<? extends Object> formater) {
        Object value;
        if (formater != null) {
            if (formater instanceof ExportText) {
                // 避免显示的是超链接的html
                value = ((ExportText) formater).getExportText(context, cellValue);
            } else {
                value = formater.format(context, cellValue);
            }
        } else {
            // value = (cellValue != null ? cellValue.toString() : "");
            value = cellValue;
        }
        // return value != null ? value : "";
        return value;
    }

    public GridData() {
        this.addClazz("data");
    }

    public GridData(int pageNo, int pageCount) {
        this();

        this.pageNo = pageNo;
        this.pageCount = pageCount;
    }

    public ExpressionParser getParser() {
        return parser;
    }

    public GridData setParser(ExpressionParser parser) {
        this.parser = parser;
        return this;
    }

    public String getRowLabelExpression() {
        return rowLabelExpression;
    }

    public GridData setRowLabelExpression(String rowLabelExpression) {
        this.rowLabelExpression = rowLabelExpression;
        return this;
    }

    public List<? extends Object> getData() {
        return data;
    }

    public GridData setData(List<? extends Object> data) {
        this.data = data;
        return this;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public GridData setColumns(List<Column> columns) {
        this.columns = columns;
        return this;
    }

    public int getPageNo() {
        return pageNo;
    }

    public GridData setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public int getPageCount() {
        return pageCount;
    }

    public GridData setPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public GridData setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public StringBuffer render(StringBuffer main) {
        buildData();

        return super.render(main);
    }

    protected void buildData() {
        // 分页信息
        if (this.pageNo > 0)
            this.setAttr("data-pageNo", this.pageNo + "");
        if (this.pageCount > 0)
            this.setAttr("data-pageCount", this.pageCount + "");
        if (this.totalCount > 0)
            this.setAttr("data-totalCount", this.totalCount + "");

        // 总体结构
        Component left = new Div().addClazz("left");
        Component right = new Div().addClazz("right");
        this.addChild(left).addChild(right);

        // 变量定义
        Component tr, td;
        Column column;
        int rc = 0;

        // 左table
        Component leftTable = new Table().addClazz("table")
                .setAttr("cellspacing", "0").setAttr("cellpadding", "0");
        left.addChild(leftTable);
        for (Object rowData : this.data) {
            // 行设置
            tr = new Tr().addClazz(DEFAULT_ROW_CLASS);
            leftTable.addChild(tr);
            if (rc % 2 == 0) {
                tr.addClazz("odd");// 奇数行
            } else {
                tr.addClazz("even");// 偶数行
            }

            // 添加行的特殊样式
            String specClass = this.getRowClass(this.data, rowData, rc, 0);
            if (specClass != null && specClass.length() > 0)
                tr.addClazz(specClass);

            // 循环添加行的单元格（第一列为id列忽略）
            column = Grid.getIDColumn(this.columns);
            td = new Td().addClazz("id");
            tr.addChild(td);

            // 单元格宽度
            if (column.getWidth() > 0) {
                td.addStyle("width", column.getWidth() + "px");
            }

            // 单元格内容的包装容器（相对定位的元素）
            // Component wrapper = new Div().addClazz("wrapper");
            // td.addChild(wrapper);

            // 单元格内容
            String rowLabel;
            if (this.getName() != null) {
                rowLabel = this.getName() + " - ";
            } else {
                rowLabel = "";
            }
            td.setAttr(
                    "data-name",
                    rowLabel
                            + getValue(
                            rowData,
                            getRowLabelExpression() != null ? getRowLabelExpression()
                                    : "id", null));// 行的标题
            td.setAttr("data-id", StringUtils.null2Empty(formatValue(rowData,
                    getValue(rowData, column.getValueExpression()),
                    column.getValueFormater())));// 行的id
            td.addChild(new Span().addClazz("ui-icon"));// 勾选标记符
            td.addChild(new Text(String.valueOf(rc + 1)));// 行号

            rc++;
        }

        // 右table
        List<Column> visibleColumns = Grid.getVisibleColumns(this.columns);
        List<HiddenColumn> hiddenColumns = Grid.getHiddenColumns(this.columns);
        Component rightTable = new Table().addClazz("table")
                .setAttr("cellspacing", "0").setAttr("cellpadding", "0");
        right.addChild(rightTable);
        int totalWidth = Grid.getDataTableWidth(visibleColumns);
        rightTable.addStyle("width", totalWidth + "px");
        rightTable.setAttr("originWidth", totalWidth + "");
        rc = 0;
        Object cellValue;
        JSONObject hiddenValues;
        for (Object rowData : this.data) {
            // 行设置
            tr = new Tr().addClazz(DEFAULT_ROW_CLASS);
            rightTable.addChild(tr);
            if (rc % 2 == 0) {
                tr.addClazz("odd");// 奇数行
            } else {
                tr.addClazz("even");// 偶数行
            }

            // 添加行的特殊样式
            String specClass = this.getRowClass(this.data, rowData, rc, 1);
            if (specClass != null && specClass.length() > 0)
                tr.addClazz(specClass);

            // 计算隐藏域的值
            hiddenValues = new JSONObject();
            for (HiddenColumn c : hiddenColumns) {
                try {
                    hiddenValues.put(c.getId(),
                            getValue(rowData, c.getValueExpression()));
                } catch (JSONException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            tr.setAttr("data-hidden", hiddenValues.toString());

            // 循环添加行的单元格（第一列为id列忽略）
            for (int i = 1; i < visibleColumns.size(); i++) {
                column = visibleColumns.get(i);
                td = new Td();
                tr.addChild(td);

                // 单元格样式
                if (i == 1) {
                    td.addClazz("first");// 首列样式
                } else if (i == visibleColumns.size() - 1) {
                    td.addClazz("last");// 最后列样式
                } else {
                    td.addClazz("middle");// 中间列样式
                }

                // 单元格宽度
                if (column.getWidth() > 0) {
                    td.addStyle("width", column.getWidth() + "px");
                }

                // 单元格内容的包装容器（相对定位的元素）
                // Component wrapper = new Div().addClazz("wrapper");
                // td.addChild(wrapper);

                // 单元格内容
                Object srcCellValue = getValue(rowData,
                        column.getValueExpression());
                cellValue = formatValue(rowData, srcCellValue,
                        column.getValueFormater());
                td.addChild(new Text(StringUtils.null2Empty(cellValue)))
                        .setAttr(
                                "data-value",
                                srcCellValue != null ? srcCellValue.toString()
                                        : "")// 原始值
                        .setAttr("data-column", column.getId());// 列标识
                if (column.isUseTitleFromLabel()) {
                    if (column.getValueFormater() instanceof ExportText) {
                        if (srcCellValue != null)
                            td.setTitle(((ExportText) column.getValueFormater()).getExportText(rowData, srcCellValue));
                    } else {
                        td.setTitle(StringUtils.null2Empty(cellValue));
                    }
                }
            }

            // 添加一列空列
            // tr.addChild(Grid.EMPTY_TD);

            rc++;
        }
    }

    /**
     * 获取数据行需要附加的特殊样式
     *
     * @param data     整个grid的数据
     * @param rowData  此行包含的数据
     * @param rowData2
     * @param index    行的索引号
     * @param type     0-左侧固定列的行,1-右侧数据列的行
     * @return 返回空将不附加特殊样式
     */
    public String getRowClass(List<? extends Object> data, Object rowData,
                              int index, int type) {
        return null;
    }
}
