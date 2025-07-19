package org.edward.pandora.common.http;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiLoader {
    private final Map<String, Object> instanceMap = new HashMap<>();
    private final Map<String, Method> methodMap = new HashMap<>();
    private final Map<String, String> methodInstanceMap = new HashMap<>();

    private static String generateMethodPath(Path classAnnotation, Path methodAnnotation) {
        return String.format("/%s/%s", classAnnotation.value(), methodAnnotation.value());
    }

    public void scan(String path) throws Exception {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(path)
                        .setScanners(Scanners.TypesAnnotated)
        );
        Set<Class<?>> pathClassSet = reflections.getTypesAnnotatedWith(Path.class);
        if(pathClassSet==null || pathClassSet.size()==0) {
            return;
        }
        for(Class<?> clazz : pathClassSet) {
            Path classAnnotation = clazz.getAnnotation(Path.class);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            this.setInstance(classAnnotation.value(), instance);
            Method[] methods = clazz.getDeclaredMethods();
            if(methods==null || methods.length==0) {
                continue;
            }
            for(Method method : methods) {
                Path methodAnnotation = method.getAnnotation(Path.class);
                if(methodAnnotation == null) {
                    continue;
                }
                String methodPath = generateMethodPath(classAnnotation, methodAnnotation);
                this.setMethod(methodPath, method);
                this.bindMethodToInstance(methodPath, classAnnotation.value());
            }
        }
    }

    private void setInstance(String path, Object instance) throws Exception {
        if(this.instanceMap.containsKey(path)) {
            return;
        }
        this.instanceMap.put(path, instance);
    }

    private void setMethod(String path, Method method) {
        if(this.methodMap.containsKey(path)) {
            return;
        }
        this.methodMap.put(path, method);
    }

    private void bindMethodToInstance(String methodPath, String instancePath) {
        if(this.methodInstanceMap.containsKey(methodPath)) {
            return;
        }
        this.methodInstanceMap.put(methodPath, instancePath);
    }

    public <T> T getBean(String path) {
        return (T) this.instanceMap.get(path);
    }

    public Object execute(String path, String json) throws Exception {
        Method method = this.methodMap.get(path);
        Parameter[] parameters = method.getParameters();
        if(parameters==null || parameters.length==0) {
            return method.invoke(this.getBean(this.methodInstanceMap.get(path)));
        }
        Object[] args = new Object[parameters.length];
        for(int i=0; i<parameters.length; i++) {
            Parameter parameter = parameters[i];
            if(parameter.isAnnotationPresent(JsonParam.class)) {
                args[i] = json;
            } else {
                args[i] = null;
            }
        }
        return method.invoke(this.getBean(this.methodInstanceMap.get(path)), args);
    }
}