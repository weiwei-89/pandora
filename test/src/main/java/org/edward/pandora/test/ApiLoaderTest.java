package org.edward.pandora.test;

import org.edward.pandora.common.http.ApiLoader;

public class ApiLoaderTest {
    public static void main(String[] args) throws Exception {
        ApiLoader apiLoader = new ApiLoader();
        apiLoader.scan("org.edward.pandora.test.controller");
//        UserService userService = apiLoader.getBean("user");
//        userService.hello();
        apiLoader.execute("/user/shoot", "666");
    }
}