package cn.snailpad.easyjson;


import cn.snailpad.easyjson.json.JSONArray;
import cn.snailpad.easyjson.json.JSONObject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/*
参考  http://www.json.org/
JSON的两种操作:
1.parse 2.generate


JSON的两种数据结构:
1.对象：无序的，由name/value pairs构成
2.数组：有序的，由各种value构成

value的类型:
1.string
2.number
3.object
4.array
5.true
6.false
7.null

 */




public class EasyJSONBase {
    public static final int JSON_TYPE_INVALID = 0;  // 无效的JSON类型
    public static final int JSON_TYPE_OBJECT = 1;
    public static final int JSON_TYPE_ARRAY = 2;


    private static class FieldInfo {
        public String fieldName;   // 字段名
        public String fieldNameAnnotated;  // 被 FieldName 注解重新指定的字段名
        public Field field;

        public FieldInfo(String fieldName, String fieldNameAnnotated, Field field) {
            this.fieldName = fieldName;
            this.fieldNameAnnotated = fieldNameAnnotated;
            this.field = field;
        }

        @Override
        public String toString() {
            return String.format("fieldName[%s], fieldNameAnnotated[%s], field[%s]",
                    fieldName, fieldNameAnnotated, field);
        }
    }

    protected Object json;  // 内部表示的JSON，可能是JSONObject或JSONArray

    protected int jsonType = JSON_TYPE_INVALID;

    public int getJsonType() {
        return jsonType;
    }


    /**
     * 判断给定的字符串是否为JSON格式的字符串(支持JSON对象和数组)
     * @param jsonString
     * @return
     */
    public static boolean isJSONString(String jsonString) {
        if (jsonString == null || jsonString.length() < 1) {
            return false;
        }
        try {
            int jsonType = guessJSONType(jsonString);  // 需要先确定类型
            if (jsonType == JSON_TYPE_OBJECT) {
                JSONObject jsonObject = new JSONObject(jsonString);
            } else if (jsonType == JSON_TYPE_ARRAY) {
                JSONArray jsonArray = new JSONArray(jsonString);
            } else {
                return false;
            }

        } catch (EasyJSONException e) {
            // e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int guessJSONType(String jsonString) {
        if (jsonString == null) {
            return JSON_TYPE_INVALID;
        }

        int len = jsonString.length();
        for (int i = 0; i < len; ++i) {
            char ch = jsonString.charAt(i);
            if (ch == '{') {
                return JSON_TYPE_OBJECT;
            }
            if (ch == '[') {
                return JSON_TYPE_ARRAY;
            }
        }

        return JSON_TYPE_INVALID;
    }


    public static EasyJSONBase parse(String jsonString) {
        int jsonType = guessJSONType(jsonString);
        // SLog.info("jsonType[%s]", jsonType);
        if (jsonType == JSON_TYPE_OBJECT) {
            return new EasyJSONObject(jsonString);
        } else if (jsonType == JSON_TYPE_ARRAY) {
            return new EasyJSONArray(jsonString);
        }

        return null;
    }


    /**
     * 将path分割成各个name，例如
     * name1.name2[0][2].name3[1]  分拆成  name1, name2, [0], [2], name[3], [1]
     * [2].name1[1]
     * @param path
     * @return
     */
    private static List<String> splitPath(String path) {
        path = path.trim();
        String[] snippets = path.split("\\."); // 用点号来分隔不同的层级
        List<String> nameList = new ArrayList<>();


        for (String snippet : snippets) {
            // SLog.info("snippet[%s]", snippet);
            int len = snippet.length();
            int i = 0;
            int j = 0;
            String name;
            char beginChar = snippet.charAt(0);
            while (true) {
                char ch = snippet.charAt(j);

                if (beginChar == '[') {  // 是数组索引
                    if (ch == ']') {
                        ++j;  // 跳到中括号的下一个
                        name = snippet.substring(i, j);
                        nameList.add(name);
                        i = j;
                        if (i >= len) {
                            break;
                        }
                        beginChar = snippet.charAt(i);
                    }
                } else {  // 是对象字段
                    if (j + 1 == len) {
                        name = snippet.substring(i, j + 1);
                        nameList.add(name);
                        break;
                    }
                    if (ch == '[') {
                        name = snippet.substring(i, j);
                        nameList.add(name);
                        i = j;

                        if (i >= len) {
                            break;
                        }
                        beginChar = snippet.charAt(i);
                    }
                }
                ++j;
            }
        }

        return nameList;
    }


    public Object get(String path) throws EasyJSONException {
        List<String> nameList = splitPath(path);

        Object value = json;  // 赋初始值，从本层开始
        for (String name : nameList) {
            name = name.trim();
            if (name.startsWith("[")) {  // 以中括号开始，表明是个Array
                // 去除开始和结束的中括号
                String indexStr = name.substring(1, name.length() - 1);
                indexStr = indexStr.trim();
                // SLog.info("indexStr[%s]", indexStr);

                int index = Integer.parseInt(indexStr);  // 得出Array的索引

                value = ((JSONArray) value).get(index);
            } else {  // 否则，表明是个Object
                value = ((JSONObject)value).get(name);
            }
        }

        String valueType = "UNKNOWN";
        if (value.equals(JSONObject.NULL)) {
            valueType = "NULL";
        } else if (value instanceof Boolean) {
            valueType = "BOOLEAN";
        } else if (value instanceof String) {
            valueType = "STRING";
        } else if (value instanceof Double || value instanceof Float) {
            valueType = "DOUBLE";
        } else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            valueType = "INTEGER";
        } else if (value instanceof JSONObject) {
            valueType = "OBJECT";
            value = new EasyJSONObject((JSONObject) value);
        } else if (value instanceof JSONArray) {
            valueType = "ARRAY";
            value = new EasyJSONArray((JSONArray) value);
        }
        // SLog.info("valueType[%s]", valueType);

        return value;
    }

    public boolean getBoolean(String path) throws EasyJSONException {
        return (boolean) get(path);
    }

    public int getInt(String path) throws EasyJSONException {
        return (int) get(path);
    }

    public double getDouble(String path) throws EasyJSONException {
        Object result = get(path);
        if (result instanceof Integer || result instanceof Long) {
            return Double.valueOf(result.toString());
        }
        return (double) result;
    }


    public String getString(String path) throws EasyJSONException {
        Object result = get(path);
        if (result.equals(JSONObject.NULL)) { // 如果那个字段的值是null，直接返回null
            return null;
        }
        return (String) result;
    }

    public EasyJSONArray getArray(String path) throws EasyJSONException {
        Object result = get(path);
        if (result.equals(JSONObject.NULL)) { // 如果那个字段的值是null，直接返回null
            return null;
        }
        return (EasyJSONArray) result;
    }


    public EasyJSONObject getObject(String path) throws EasyJSONException {
        Object result = get(path);
        if (result.equals(JSONObject.NULL)) { // 如果那个字段的值是null，直接返回null
            return null;
        }
        return (EasyJSONObject) result;
    }


    public static String jsonEncode(Object object) throws EasyJSONException, IllegalAccessException {
        Object result = jsonEncodeInternal(object);
        if (result == null) {
            return null;
        }
        return result.toString();
    }


    private static Object jsonEncodeInternal(Object object) throws EasyJSONException, IllegalAccessException {
        if (object == null) {
            return null;
        }

        Class clazz = object.getClass();
        // String className = clazz.getName();
        // SLog.info("className[%s]", className);

        if (object instanceof EasyJSONObject) {
            EasyJSONObject result = EasyJSONObject.generate();

            EasyJSONObject easyJSONObject = (EasyJSONObject) object;
            for (Map.Entry<String, Object> entry : easyJSONObject.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();

                result.set(name, jsonEncodeInternal(value));
            }
            return result;
        } else if (object instanceof EasyJSONArray || object instanceof JSONArray) {
            EasyJSONArray result = EasyJSONArray.generate();

            EasyJSONArray easyJSONArray = null;
            if (object instanceof EasyJSONArray) {
                easyJSONArray = (EasyJSONArray) object;
            } else if (object instanceof JSONArray) {
                easyJSONArray = new EasyJSONArray((JSONArray) object);
            }

            for (Object elem : easyJSONArray) {
                result.append(jsonEncodeInternal(elem));
            }

            return result;
        } else if (clazz.isArray()) {
            EasyJSONArray result = EasyJSONArray.generate();

            int length = Array.getLength(object);
            for (int i = 0; i < length; ++i) {
                Object elem = Array.get(object, i);
                result.append(jsonEncodeInternal(elem));
            }
            return result;
        } else if (object instanceof Collection) {
            EasyJSONArray result = EasyJSONArray.generate();

            Collection collection = (Collection) object;
            for (Object elem : collection) {
                result.append(jsonEncodeInternal(elem));
            }
            return result;
        } else if (object instanceof Map) {
            EasyJSONObject result = EasyJSONObject.generate();

            Map<Object, Object> map = (Map<Object, Object>) object;
            for (Map.Entry<Object, Object> entry: map.entrySet()){
                Object name = entry.getKey();
                Object value = entry.getValue();

                result.set(name.toString(), value);
            }
            return result;
        } else if (isPrimitiveType(object)) { // 如果是基本类型，直接返回
            return object;
        } else {
            EasyJSONObject result = EasyJSONObject.generate();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                String name = field.getName();

                /*
                参考
                Android Studio2.0以上反射$change属性问题
                https://stackoverflow.com/questions/34647546/a-weird-field-appear-in-android-studio

                也可以处理this$0、this$1这种隐含的内部类成员
                Using Inner Classes
                http://www.oursland.net/tutorials/java/innerclasses/
                Non-static inner classes have a hidden reference to the enclosing class instance.
                You used "this$0" or "this$1", where the trailing number indicated how many levels up the enclosing hierarchy you wanted to access.
                 */
                if (field.isSynthetic()) {
                    // SLog.info("field[%s] is synthetic", field.getName());
                    continue;
                }

                FieldName fieldName = field.getAnnotation(FieldName.class);
                if (fieldName != null) { // 如果有注解，则读取注解指定的名称
                    name = fieldName.value();
                }
                Object value = field.get(object);
                // SLog.info("field[%s], value[%s]", name, value);
                result.set(name, jsonEncodeInternal(value));
            }

            return result;
        }
    }



    public static Object jsonDecode(Class clazz, String jsonString) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return jsonDecodeInternal(clazz, jsonString, null);
    }

    /**
     * 如果jsonString表示的是对象，那么clazz就是这种对象的类
     * 如果jsonString表示的是数组，那么clazz就是这个数组元素的类
     * @param clazz
     * @param jsonString
     * @param enclosingObject  对于内部类来说，表示包装类对象
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static Object jsonDecodeInternal(Class clazz, String jsonString, Object enclosingObject) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int jsonType = guessJSONType(jsonString);
        if (jsonType == JSON_TYPE_OBJECT) {  // 如果表示的是对象

            /*
            参考
            Java反射获取内部类的实例  https://blog.csdn.net/ldstartnow/article/details/52782420
             */
            Object object;
            if (enclosingObject != null) {
                Constructor constructor = clazz.getDeclaredConstructor(enclosingObject.getClass());
                object = constructor.newInstance(enclosingObject);
            } else {
                Constructor constructor = clazz.getDeclaredConstructor();
                object = constructor.newInstance();
            }


            // 遍历获取各个字段的信息
            List<FieldInfo> fieldInfoList = new ArrayList<>();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                String name = field.getName();
                String nameAnnotated = null;

                FieldName fieldName = field.getAnnotation(FieldName.class);
                if (fieldName != null) {
                    nameAnnotated = fieldName.value();
                }

                // SLog.info("name[%s], nameAnnotated[%s], value[%s], type[%s]", name, nameAnnotated, field.get(object), field.getType());

                FieldInfo fieldInfo = new FieldInfo(name, nameAnnotated, field);
                fieldInfoList.add(fieldInfo);
            }

            // 遍历JSON数据，进行赋值
            EasyJSONObject easyJSONObject = (EasyJSONObject) EasyJSONObject.parse(jsonString);
            for (Map.Entry<String, Object> entry : easyJSONObject.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                // SLog.info("name[%s], value[%s]", name, value);

                // 查找字段
                FieldInfo fieldInfo = null;
                // 优化找fieldNameAnnotated
                for (FieldInfo item : fieldInfoList) {
                    if (name.equals(item.fieldNameAnnotated)) {
                        fieldInfo = item;
                        break;
                    }
                }

                // 如果找不到，再找fieldName
                if (fieldInfo == null) {
                    for (FieldInfo item : fieldInfoList) {
                        if (name.equals(item.fieldName)) {
                            fieldInfo = item;
                            break;
                        }
                    }
                }

                if (fieldInfo == null) {  // 如果还是找不到，则忽略
                    continue;
                }

                Field field = fieldInfo.field;
                Class fieldType = field.getType();
                field.setAccessible(true);

                // SLog.info("fieldInfo[%s], fieldType[%s]", fieldInfo, fieldType);
                if (fieldType.isArray()) { // 如果是数组类型
                    // SLog.info("IS ARRAY, class[%s]", value.getClass());
                    if (!(value instanceof JSONArray)) {  // 如果不是数组
                        continue;
                    }
                    EasyJSONArray easyJSONArray = new EasyJSONArray((JSONArray) value);
                    int arrayLength = easyJSONArray.length(); // 获取数组的长度
                    // SLog.info("arrayLength[%d]", arrayLength);
                    Class componentType = fieldType.getComponentType();  // 获取数组的元素类型
                    // SLog.info("componentType[%s]", componentType);
                    Object fieldArray = Array.newInstance(componentType, arrayLength);  // 开辟数组存储空间

                    // 复制数组数据
                    int index = 0;
                    for (Object arrayElem : easyJSONArray) {
                        // SLog.info("arrayElem[%s][%s]", arrayElem.toString(), arrayElem.getClass());
                        if (isPrimitiveType(componentType)) {
                            Array.set(fieldArray, index, arrayElem);
                        } else {
                            if (componentType.getName().startsWith(clazz.getName())) { // 是内部类
                                Array.set(fieldArray, index, jsonDecodeInternal(componentType, arrayElem.toString(), object));
                            } else {
                                Array.set(fieldArray, index, jsonDecodeInternal(componentType, arrayElem.toString(), null));
                            }
                        }

                        ++index;
                    }

                    // 将数组数据设置到对象中
                    field.set(object, fieldArray);
                } else if (isPrimitiveType(fieldType)){
                    // SLog.info("NOT ARRAY");
                    // SLog.info("value[%s], class[%s]", value, value.getClass().getName());
                    if (JSONObject.NULL.equals(value)) {  // 对JSONObject.NULL的特殊处理
                        field.set(object, null);
                    } else {
                        field.set(object, value);
                    }
                } else {
                    // SLog.info("clazz[%s], fieldType[%s]", clazz.getName(), fieldType.getName());

                    // 判断是否是内部类
                    if (fieldType.getName().startsWith(clazz.getName())) { // 是内部类
                        field.set(object, EasyJSONBase.jsonDecodeInternal(fieldType, value.toString(), object));
                    } else {
                        field.set(object, EasyJSONBase.jsonDecodeInternal(fieldType, value.toString(), null));
                    }
                }
            }

            return object;
        } else if (jsonType == JSON_TYPE_ARRAY) {
            EasyJSONArray easyJSONArray = (EasyJSONArray) EasyJSONArray.parse(jsonString);
            int length = easyJSONArray.length();

            Object result = Array.newInstance(clazz, length);
            int idx = 0;
            for (Object elem : easyJSONArray) {
                Object elemDecoded = jsonDecodeInternal(clazz, elem.toString(), null);
                Array.set(result, idx, elemDecoded);
                ++idx;
            }

            return result;
        }

        return null;
    }


    /**
     * 通过对象判断是否为基本类型
     * @param object
     * @return
     */
    private static boolean isPrimitiveType(Object object) {
        return object instanceof Byte || object instanceof Short ||
                object instanceof Integer || object instanceof Long ||
                object instanceof Float || object instanceof Double ||
                object instanceof Boolean || object instanceof String;
    }


    /**
     * 通过Class判断是否为基本类型
     * @param clazz
     * @return
     */
    public static boolean isPrimitiveType(Class clazz) {
        return clazz.getPackage() == null ||  // int, float这些getPackage()为空
                clazz == Byte.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class ||
                clazz == Boolean.class || clazz == String.class;
    }


    @Override
    public String toString() {
        if (json == null) {
            return "null";
        }

        if (jsonType == JSON_TYPE_OBJECT) {
            JSONObject jsonObject = (JSONObject) json;
            return jsonObject.toString();
        } else if (jsonType == JSON_TYPE_ARRAY) {
            JSONArray jsonArray = (JSONArray) json;
            return jsonArray.toString();
        }

        return "null";
    }

    /**
     * 以格式化的形式输出json字符串
     * @param indentSpaces 缩进空格数
     * @return
     */
    public String toString(int indentSpaces) {
        if (json == null) {
            return "null";
        }

        String jsonString = "null";
        try {
            if (jsonType == JSON_TYPE_OBJECT) {
                JSONObject jsonObject = (JSONObject) json;
                jsonString = jsonObject.toString(indentSpaces);
            } else if (jsonType == JSON_TYPE_ARRAY) {
                JSONArray jsonArray = (JSONArray) json;
                jsonString = jsonArray.toString(indentSpaces);
            }
        } catch (EasyJSONException e) {

        }

        return jsonString;
    }
}



/*
EasyJSONObject和EasyJOSNArray都支持path路径的访问
关于路径path的说明，以下面的JSON为例(通过测试)

{
	"code": 200,
	"message": "success",
	"valid": true,
	"pi": 3.14,
	"data": {
		"phone": 10086,
		"addr": "China",
		"score": {
			"math": 95,
			"music": 86
		},
		"bool_list": [false, true, false]
	},
	"int_list": [1, 2, 3, 4],
	"string_list": ["Tom", "Peter", "Jack"],
	"hybrid_list": [1, {
		"http_not_found": 404
	}, "toyota"],
	"object_list": [{
		"lang": "java",
		"type": "static"
	}, {
		"lang": "javascript",
		"type": "dynamic"
	}],
	"multi_dim": [
		[0, 1],
		[2, 3]
	]
}


------------------------------------------------------------------
路径                 |值
------------------------------------------------------------------
code                |int(200)
------------------------------------------------------------------
data                |EasyJSONObject对象
------------------------------------------------------------------
data.score.math     |int(95)
------------------------------------------------------------------
int_list            |EasyJSONArray对象
------------------------------------------------------------------
int_list[2]         |int(3)
------------------------------------------------------------------
object_list[0]      |EasyJSONObject对象
------------------------------------------------------------------
object_list[0].lang |string("java")
------------------------------------------------------------------
multi_dim[0][1]     |int(3)
------------------------------------------------------------------



对于EasyJSONArray，还支持通过index访问其中的元素(通过测试)
[
	200,
	"success",
	true,
	3.14, [1, {
		"http_not_found": 404
	}, "toyota"],
	{
		"math": 95,
		"music": 86
	},
	[{
		"lang": "java",
		"type": "static"
	}, {
		"lang": "javascript",
		"type": "dynamic"
	}],
	[
		[0, 1],
		[2, 3]
	]
]


------------------------------------------------------------------
索引                 |值
------------------------------------------------------------------
0                   |int(200)
------------------------------------------------------------------
1                   |string("success")
------------------------------------------------------------------
4                   |EasyJSONArray对象
------------------------------------------------------------------
5                   |EasyJSONObject对象
------------------------------------------------------------------



 */