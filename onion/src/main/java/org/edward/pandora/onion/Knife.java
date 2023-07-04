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
        if(target instanceof Iterable) {
            Iterable<?> targetList = (Iterable<?>) target;
            int targetCount = Box.getListCount(targetList);
            if(targetCount == 0) {
                return null;
            }
            List<Object> peelList = new ArrayList<>(targetCount);
            Iterator<?> targetIterator = targetList.iterator();
            while(targetIterator.hasNext()) {
                Object targetItem = targetIterator.next();
                if(Box.isPrimitive(targetItem)) {
                    peelList.add(String.valueOf(targetItem));
                } else {
                    Peel aPeel = new Peel();
                    this.peel(targetItem, aPeel);
                    peelList.add(aPeel);
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
                    Peel aPeel = new Peel();
                    this.peel(targetMapValue, aPeel);
                    peelMap.put(entry.getKey(), aPeel);
                }
            }
            return peelMap;
        } else {
            Peel peel = new Peel();
            this.peel(target, peel);
            return peel;
        }
    }

    private void peel(Object target, Peel peel) throws Exception {
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return;
        }
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
            } else if(targetFieldValue instanceof Iterable) {
                Iterable<?> targetList = (Iterable<?>) targetFieldValue;
                int targetCount = Box.getListCount(targetList);
                if(targetCount == 0) {
                    continue;
                }
                List<Object> peelList = new ArrayList<>(targetCount);
                Iterator<?> targetIterator = targetList.iterator();
                while(targetIterator.hasNext()) {
                    Object targetItem = targetIterator.next();
                    if(Box.isPrimitive(targetItem)) {
                        peelList.add(String.valueOf(targetItem));
                    } else {
                        Peel aPeel = new Peel();
                        this.peel(targetItem, aPeel);
                        peelList.add(aPeel);
                    }
                }
                peel.put(peelName, peelList);
            } else if(targetFieldValue instanceof Map) {
                Map<String, Object> targetMap = (Map<String, Object>) targetFieldValue;
                if(targetMap.isEmpty()) {
                    continue;
                }
                Peel peelMap = new Peel(targetMap.size());
                for(Map.Entry<String, Object> entry : targetMap.entrySet()) {
                    Object targetMapValue = entry.getValue();
                    if(Box.isPrimitive(targetMapValue)) {
                        peelMap.put(entry.getKey(), String.valueOf(targetMapValue));
                    } else {
                        Peel aPeel = new Peel();
                        this.peel(targetMapValue, aPeel);
                        peelMap.put(entry.getKey(), aPeel);
                    }
                }
                peel.put(peelName, peelMap);
            } else {
                Peel nextPeel = new Peel();
                this.peel(targetFieldValue, nextPeel);
                peel.put(peelName, nextPeel);
            }
        }
    }
}