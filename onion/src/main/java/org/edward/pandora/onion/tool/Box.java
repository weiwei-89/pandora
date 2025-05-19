package org.edward.pandora.onion.tool;

import org.edward.pandora.onion.bind.model.EnumInfo;
import org.edward.pandora.onion.bind.model.EnumInstance;

import java.lang.reflect.Field;
import java.util.Iterator;

public class Box {
    public static boolean isPrimitive(Object target) {
        if(target == null) {
            return false;
        }
        if(target instanceof String) {
            return true;
        }
        if(target instanceof Number) {
            return true;
        }
        if(target instanceof Boolean) {
            return true;
        }
        if(target instanceof Character) {
            return true;
        }
        return target.getClass().isPrimitive();
    }

    public static boolean isPrimitive(Iterable<?> objectList) {
        if(objectList == null) {
            return false;
        }
        if(getListCount(objectList) == 0) {
            return false;
        }
        return isPrimitive(objectList.iterator().next());
    }

    public static EnumInstance readEnumInstance(Class<?> targetClass) throws Exception {
        Field[] targetFields = targetClass.getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return null;
        }
        EnumInstance enumInstance = new EnumInstance(targetFields.length);
        for(Field targetField : targetFields) {
            if(!targetField.isEnumConstant()) {
                continue;
            }
            targetField.setAccessible(true);
            enumInstance.put(targetField.getName(), (Enum<?>)targetField.get(targetClass));
        }
        return enumInstance;
    }

    public static EnumInfo getEnumInfo(Enum<?> target) throws Exception {
        if(target == null) {
            return null;
        }
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return null;
        }
        EnumInfo enumInfo = new EnumInfo(targetFields.length);
        for(Field targetField : targetFields) {
            targetField.setAccessible(true);
            Object targetFieldValue = targetField.get(target);
            if(isPrimitive(targetFieldValue)) {
                enumInfo.put(targetField.getName(), String.valueOf(targetFieldValue));
            }
        }
        return enumInfo;
    }

    public static int getListCount(Iterable<?> objectList) {
        if(objectList == null) {
            return 0;
        }
        int count = 0;
        Iterator<?> iterator = objectList.iterator();
        while(iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }
}