package org.edward.pandora.onion;

import org.edward.pandora.onion.bind.annotation.Cut;
import org.edward.pandora.onion.bind.model.EnumInfo;
import org.edward.pandora.onion.bind.model.EnumInstance;
import org.edward.pandora.onion.bind.model.Peel;
import org.edward.pandora.onion.tool.Box;
import org.edward.pandora.onion.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class KnifeV2 {
    private static final Logger logger = LoggerFactory.getLogger(KnifeV2.class);
    private final Map<Class<?>, ConvertFunction<Object, Environment, Value>> handlers = new HashMap<>();
    private final Environment env = new Environment();

    private KnifeV2() {
        this.registerHandlers();
        this.initEnvironment();
    }

    public static KnifeV2 build() {
        return new KnifeV2();
    }

    private void registerHandlers() {
        this.register(Object.class, this::handleObject);
        this.register(String.class, this::handleString);
        this.register(Number.class, this::handleNumber);
        this.register(Boolean.class, this::handleBoolean);
        this.register(Character.class, this::handleCharacter);
        this.register(Iterable.class, this::handleIterable);
    }

    private <T> void register(Class<T> targetType, ConvertFunction<T, Environment, Value> handler) {
        this.handlers.put(
                targetType,
                (target, env) -> {
                    logger.info("invoke function for {}", targetType.getName());
                    return handler.apply(targetType.cast(target), env);
                }
        );
    }

    private void initEnvironment() {
//        this.handlers.forEach((k, v) -> {
//            this.env.addClazz(k);
//        });
        // TODO 添加对象类型时需要注意顺序，Object类型需要放到最后
        // TODO 这里列出的类型和上边注册处理器时列出的类型列表重复了，待优化
        this.env.addClazz(String.class);
        this.env.addClazz(Number.class);
        this.env.addClazz(Boolean.class);
        this.env.addClazz(Character.class);
        this.env.addClazz(Iterable.class);
        this.env.addClazz(Object.class);
    }

    public Value peel(Object target) throws Exception {
        return this.peel(target, this.env);
    }

    private Value peel(Object target, Environment env) throws Exception {
        ConvertFunction<Object, Environment, Value> handler = this.lookupHandler(target.getClass());
        if(handler == null) {
            throw new ConvertException(String.format("unsupported target:\n%s", target.getClass()));
        }
        return handler.apply(target, env);
    }

    private ConvertFunction<Object, Environment, Value> lookupHandler(Class<?> targetType) {
        List<Class<?>> clazzList = this.env.getClazzList();
        for(Class<?> clazz : clazzList) {
            if(clazz.isAssignableFrom(targetType)) {
                return this.handlers.get(clazz);
            }
        }
        return this.handlers.get(Object.class);
    }

    private Value handleObject(Object target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        Field[] targetFields = target.getClass().getDeclaredFields();
        if(targetFields==null || targetFields.length==0) {
            return NullValue.INSTANCE;
        }
        MapValue peel = new MapValue(targetFields.length);
        for(Field targetField : targetFields) {
            if(!targetField.isAnnotationPresent(Cut.class)) {
                continue;
            }
            Cut targetCut = targetField.getAnnotation(Cut.class);
            if(!targetCut.available()) {
                continue;
            }
            String peelName = targetCut.tag();
            if(peelName==null || "".equals(peelName)) {
                peelName = targetField.getName();
            }
            targetField.setAccessible(true);
            Object targetFieldValue = targetField.get(target);
            if(targetFieldValue == null) {
                peel.set(peelName, NullValue.INSTANCE);
                continue;
            }
            peel.set(peelName, this.peel(targetFieldValue, env));



//            if(Box.isPrimitive(targetFieldValue)) {
//                if(targetCut.ignoreEmptyString()) {
//                    if(targetFieldValue instanceof String && "".equals(targetFieldValue)) {
//                        continue;
//                    } else {
//                        peel.put(peelName, targetFieldValue);
//                    }
//                } else {
//                    peel.put(peelName, targetFieldValue);
//                }
//                if(targetCut.convert()) {
//                    List<EnumInfo> enumInfoList = null;
//                    if(this.convertDefinitionCache.containsKey(targetCut.convertDefinition().getName())) {
//                        enumInfoList = this.convertDefinitionCache.get(targetCut.convertDefinition().getName());
//                    } else {
//                        EnumInstance enumInstance = Box.readEnumInstance(targetCut.convertDefinition());
//                        if(enumInstance!=null && !enumInstance.isEmpty()) {
//                            enumInfoList = new ArrayList<>(enumInstance.size());
//                            for(Map.Entry<String, Enum<?>> entry : enumInstance.entrySet()) {
//                                EnumInfo enumInfo = Box.getEnumInfo(entry.getValue());
//                                if(enumInfo!=null && !enumInfo.isEmpty()) {
//                                    enumInfoList.add(enumInfo);
//                                }
//                            }
//                        }
//                        this.convertDefinitionCache.put(targetCut.convertDefinition().getName(), enumInfoList);
//                    }
//                    if(enumInfoList!=null && enumInfoList.size()>0) {
//                        for(int i=0; i<enumInfoList.size(); i++) {
//                            if(enumInfoList.get(i).get(targetCut.convertKey())
//                                    .equals(String.valueOf(targetFieldValue))) {
//                                peel.put(peelName, enumInfoList.get(i).get(targetCut.convertValue()));
//                                break;
//                            }
//                        }
//                    }
//                }
//            } else {
//                peel.put(peelName, this.peel(targetFieldValue));
//            }
        }
        return peel;
    }

    private Value handleString(String target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        if("".equals(target)) {
            return EmptyValue.INSTANCE;
        }
        return new StringValue(target);
    }

    private Value handleNumber(Number target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        return new NumberValue(target);
    }

    private Value handleBoolean(Boolean target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        return new BooleanValue(target);
    }

    private Value handleCharacter(Character target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        return new CharacterValue(target);
    }

    private Value handleIterable(Iterable<?> target, Environment env) throws Exception {
        if(target == null) {
            return NullValue.INSTANCE;
        }
        int targetCount = Box.getListCount(target);
        if(targetCount == 0) {
            return EmptyValue.INSTANCE;
        }
        ListValue peelList = new ListValue(targetCount);
        Iterator<?> targetIterator = target.iterator();
        while(targetIterator.hasNext()) {
            Object targetListItem = targetIterator.next();
            peelList.add(this.peel(targetListItem, env));
        }
        return peelList;
    }
}