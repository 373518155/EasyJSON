package cn.snailpad.easyjson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwm
 * 2018/9/7 15:23
 */

/*
例子:
EasyJSONList easyJSONList = new EasyJSONList(1, 2, true, "abc", 3.14);
输出: [1, 2, true, "abc", 3.14]

EasyJSONList easyJSONList = new EasyJSONList(1, 2, true, "abc", 3.14, new EasyJSONList(2, 4, false, "def", 2.71));
输出: [1, 2, true, "abc", 3.14, [2, 4, false, "def", 2.71]]

EasyJSONList easyJSONList = new EasyJSONList(1, 2, true, "abc", 3.14, new EasyJSONList(2, 4, false, "def", 2.71), new EasyJSONMap("color", "red", "number", 999));
输出: [1, 2, true, "abc", 3.14, [2, 4, false, "def", 2.71], {"color":"red","number":999}]
 */


public class EasyJSONList {

    private static final String DOUBLE_QUOTATION_MARK = "\"";

    private List<Object> list = new ArrayList<>();
    public EasyJSONList(Object... args) {
        for (Object arg : args) {
            list.add(arg);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            String element;
            if (item instanceof String) {
                element = DOUBLE_QUOTATION_MARK + item + DOUBLE_QUOTATION_MARK;
            } else {
                element = String.valueOf(item);
            }

            if (first) {
                sb.append(element);
            } else {
                sb.append(", " + element);
            }
            first = false;
        }

        sb.append("]");

        return sb.toString();
    }
}
