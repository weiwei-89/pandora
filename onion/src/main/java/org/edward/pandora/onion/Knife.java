package org.edward.pandora.onion;

import org.edward.pandora.onion.bind.annotation.Cut;
import org.edward.pandora.onion.bind.model.EnumInfo;
import org.edward.pandora.onion.bind.model.EnumInstance;
import org.edward.pandora.onion.bind.model.Peel;
import org.edward.pandora.onion.tool.Box;

import java.lang.reflect.Field;
import java.util.*;

public class Knife {
    private final Map<String, List<EnumInfo>> convertDefinitionCache = new HashMap<>();

    private Knife() {

    }

    public static Knife build() {
        return new Knife();
    }

    public Object peel(Object target) throws Exception {
        if(target == null) {
            return null;
        }
        if(Box.isPrimitive(target)) {
            return String.valueOf(target);
        } else {
            if(target instanceof Iterable) {
                Iterable<?> targetList = (Iterable<?>) target;
                int targetCount = Box.getListCount(targetList);
                if(targetCount == 0) {
                    return null;
                }
                List<Object> peelList = new ArrayList<>(targetCount);
                Iterator<?> targetIterator = targetList.iterator();
                while(targetIterator.hasNext()) {
                    Object targetListItem = targetIterator.next();
                    if(Box.isPrimitive(targetListItem)) {
                        peelList.add(String.valueOf(targetListItem));
                    } else {
                        peelList.add(this.peel(targetListItem));
                    }
                }
                return peelList;
            } else if(target instanceof Map) {
                Map<String, Object> targetMap = (Map<String, Object>) target;
                if(targetMap.isEmpty()) {
                    return null;
                }
                Peel peelMap = new Peel(targetMap.size());
                for(Map.Entry<String, Object> entry : targetMap.entrySet()) {
                    Object targetMapValue = entry.getValue();
                    if(Box.isPrimitive(targetMapValue)) {
                        peelMap.put(entry.getKey(), String.valueOf(targetMapValue));
                    } else {
                        peelMap.put(entry.getKey(), this.peel(targetMapValue));
                    }
                }
                return peelMap;
            } else {
                return this.extract(target);
            }
        }
    }

    private Peel extract(Object target) throws Exception {
        if(target == null) {
            return null;
        }
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return null;
        }
        Peel peel = new Peel(targetFields.length);
        for(Field targetField : targetFields) {
            if(!targetField.isAnnotationPresent(Cut.class)) {
                continue;
            }
            Cut targetCut = targetField.getAnnotation(Cut.class);
            if(!targetCut.available()) {
                continue;
            }
            targetField.setAccessible(true);
            Object targetFieldValue = targetField.get(target);
            if(targetFieldValue == null) {
                continue;
            }
            String peelName = targetCut.tag();
            if(peelName==null || "".equals(peelName)) {
                peelName = targetField.getName();
            }
            if(Box.isPrimitive(targetFieldValue)) {
                if(targetCut.ignoreEmptyString()) {
                    if(targetFieldValue instanceof String && "".equals(targetFieldValue)) {
                        continue;
                    } else {
                        peel.put(peelName, targetFieldValue);
                    }
                } else {
                    peel.put(peelName, targetFieldValue);
                }
                if(targetCut.convert()) {
                    List<EnumInfo> enumInfoList = null;
                    if(this.convertDefinitionCache.containsKey(targetCut.convertDefinition().getName())) {
                        enumInfoList = this.convertDefinitionCache.get(targetCut.convertDefinition().getName());
                    } else {
                        EnumInstance enumInstance = Box.readEnumInstance(targetCut.convertDefinition());
                        if(enumInstance!=null && !enumInstance.isEmpty()) {
                            enumInfoList = new ArrayList<>(enumInstance.size());
                            for(Map.Entry<String, Enum> entry : enumInstance.entrySet()) {
                                EnumInfo enumInfo = Box.getEnumInfo(entry.getValue());
                                if(enumInfo!=null && !enumInfo.isEmpty()) {
                                    enumInfoList.add(enumInfo);
                                }
                            }
                        }
                        this.convertDefinitionCache.put(targetCut.convertDefinition().getName(), enumInfoList);
                    }
                    if(enumInfoList!=null && enumInfoList.size()>0) {
                        for(int i=0; i<enumInfoList.size(); i++) {
                            if(enumInfoList.get(i).get(targetCut.convertKey())
                                    .equals(String.valueOf(targetFieldValue))) {
                                peel.put(peelName, enumInfoList.get(i).get(targetCut.convertValue()));
                                break;
                            }
                        }
                    }
                }
            } else {
                peel.put(peelName, this.peel(targetFieldValue));
            }
        }
        return peel;
    }
}