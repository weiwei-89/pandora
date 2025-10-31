package org.edward.pandora.test;

import org.edward.pandora.common.http.ApiLoader;

public class ApiLoaderTest {
    public static void main(String[] args) throws Exception {
        ApiLoader apiLoader = new ApiLoader("org.edward.pandora.test.controller");
        apiLoader.init();
        apiLoader.execute("/user/shoot", "9876543210", "{'user_name':'张三'}");
    }
}