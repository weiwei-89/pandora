package org.edward.pandora.test;

import com.alibaba.fastjson2.JSON;
import org.edward.pandora.onion.Knife;
import org.edward.pandora.onion.bind.annotation.Cut;

import java.util.ArrayList;
import java.util.List;

public class OnionTest {
    public static void main(String[] args) throws Exception {
        Student lilei = new Student();
        lilei.setName("李磊");
        lilei.setAge(25);
        lilei.setGender(1);
        Student hanmeimei = new Student();
        hanmeimei.setName("韩梅梅");
        hanmeimei.setAge(24);
        hanmeimei.setGender(2);
        Classroom classroom = new Classroom();
        classroom.addStudent(lilei);
        classroom.addStudent(hanmeimei);
        Knife knife = Knife.build();
        Object result = knife.peel(classroom);
        System.out.println(JSON.toJSONString(result));
    }

    public static class Classroom {
        @Cut
        private List<Student> studentList = new ArrayList<>();

        public List<Student> getStudentList() {
            return studentList;
        }
        public void addStudent(Student student) {
            this.studentList.add(student);
        }
    }

    public static class Student {
        @Cut
        private String name;
        private int age;
        @Cut(convert=true,
                convertDefinition=GenderConvertor.class,
                convertKey="code",
                convertValue="desc")
        private int gender;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public int getGender() {
            return gender;
        }
        public void setGender(int gender) {
            this.gender = gender;
        }
    }

    public enum GenderConvertor {
        MALE(1, "男"),
        FEMALE(2, "女");

        private final int code;
        private final String desc;

        GenderConvertor(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }
}