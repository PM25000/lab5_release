package utils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DButil {
    public static void toObject(ResultSet resultSet , Object object) {

        Class<?> c = object.getClass();

        Map<String, Object> map=new HashMap<>();

        Field[] fields=c.getDeclaredFields();//获取属性数组

        for(int i=0;i<fields.length;i++){
            try {
                System.out.println(fields[i].getName());
                map.put(fields[i].getName(), resultSet.getObject(fields[i].getName()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Method[] methods = c.getMethods();//获取对象方法
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String name = method.getName();
                System.out.println(name);
                name = name.substring(3, 4).toLowerCase() + name.substring(4, name.length());//获取属性名
                System.out.println(name);
                if (map.containsKey(name)) {
                    try {
                        method.invoke(object, map.get(name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }




}
