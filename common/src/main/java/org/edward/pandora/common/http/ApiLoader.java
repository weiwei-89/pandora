package org.edward.pandora.common.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ApiLoader {
    private final String path;
    private final List<ApiFunction> apiFunctionList = new ArrayList<>();
    private final Map<String, ApiFunction> apiFunctionMap = new HashMap<>();
    private final ObjectMapper objectMapper;

    public ApiLoader(String path) {
        this.path = path;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void init() throws Exception {
        this.scan();
        this.process();
    }

    private void scan() throws Exception {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(this.path)
                        .setScanners(Scanners.TypesAnnotated)
        );
        Set<Class<?>> pathClassSet = reflections.getTypesAnnotatedWith(Path.class);
        if(pathClassSet==null || pathClassSet.size()==0) {
            return;
        }
        for(Class<?> clazz : pathClassSet) {
            Path classAnnotation = clazz.getAnnotation(Path.class);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            ApiEntrance apiEntrance = new ApiEntrance(classAnnotation.value(), instance);
            Method[] methods = clazz.getDeclaredMethods();
            if(methods==null || methods.length==0) {
                continue;
            }
            for(Method method : methods) {
                Path methodAnnotation = method.getAnnotation(Path.class);
                if(methodAnnotation == null) {
                    continue;
                }
                ApiFunction apiFunction = new ApiFunction(methodAnnotation.value(), method, apiEntrance);
                this.apiFunctionList.add(apiFunction);
            }
        }
    }

    private void process() throws Exception {
        if(this.apiFunctionList.size() == 0) {
            return;
        }
        for(int i=0; i<this.apiFunctionList.size(); i++) {
            ApiFunction apiFunction = this.apiFunctionList.get(i);
            String fullPath = generateFullPath(apiFunction.getEntrance().getPath(), apiFunction.getPath());
            if(this.apiFunctionMap.containsKey(fullPath)) {
                throw new Exception(String.format("the full path is duplicated [%s]", fullPath));
            }
            this.apiFunctionMap.put(fullPath, apiFunction);
        }
    }

    private static String generateFullPath(String entrancePath, String functionPath) {
        return String.format("/%s/%s", entrancePath, functionPath);
    }

    public <T> T getBean(String path, Class<T> clazz) {
        return clazz.cast(this.apiFunctionMap.get(path).getEntrance().getInstance());
    }

    public Object execute(String path, String json) throws Exception {
        ApiFunction apiFunction = this.apiFunctionMap.get(path);
        Method method = apiFunction.getInstance();
        Parameter[] parameters = method.getParameters();
        if(parameters==null || parameters.length==0) {
            return method.invoke(apiFunction.getEntrance().getInstance());
        }
        Object[] argArray = new Object[parameters.length];
        for(int i=0; i<parameters.length; i++) {
            Parameter parameter = parameters[i];
            if(parameter.isAnnotationPresent(JsonParam.class)) {
                if(StringUtils.isBlank(json)) {
                    argArray[i] = null;
                    continue;
                }
                Class<?> parameterType = parameter.getType();
                if(parameterType == String.class) {
                    argArray[i] = json;
                } else {
                    argArray[i] = this.objectMapper.readValue(json, parameterType);
                }
            } else {
                argArray[i] = null;
            }
        }
        return method.invoke(apiFunction.getEntrance().getInstance(), argArray);
    }

    public Object execute(String path, Object... args) throws Exception {
        ApiFunction apiFunction = this.apiFunctionMap.get(path);
        Method method = apiFunction.getInstance();
        Parameter[] parameters = method.getParameters();
        if(parameters==null || parameters.length==0) {
            return method.invoke(apiFunction.getEntrance().getInstance());
        }
        Object[] argArray = new Object[parameters.length];
        for(int i=0; i<parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if(parameter.isAnnotationPresent(JsonParam.class)) {
                if(arg == null) {
                    argArray[i] = null;
                    continue;
                }
                Class<?> parameterType = parameter.getType();
                if(parameterType == String.class) {
                    argArray[i] = arg;
                } else {
                    argArray[i] = this.objectMapper.readValue(arg.toString(), parameterType);
                }
            } else {
                argArray[i] = arg;
            }
        }
        return method.invoke(apiFunction.getEntrance().getInstance(), argArray);
    }
}