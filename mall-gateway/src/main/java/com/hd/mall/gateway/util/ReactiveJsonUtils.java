package com.hd.mall.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ReactiveJsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // 初始化配置
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public static <T> Mono<String> toJsonMono(T object) {
        if (object == null) {
            return Mono.just("null");
        }
        
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(null))
                 .subscribeOn(Schedulers.boundedElastic())
                 .onErrorResume(e -> {
                     log.error("JSON序列化失败", e);
                     return Mono.empty();
                 });
    }
    
    public static <T> Mono<T> parseMono(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> objectMapper.readValue(json, clazz))
                 .subscribeOn(Schedulers.boundedElastic())
                 .onErrorResume(e -> {
                     log.error("JSON序列化失败. JSON: {}", json, e);
                     return Mono.empty();
                 });
    }


//    public static void main(String[] args) throws InterruptedException {
//        // 测试对象转JSON
//        User user = new User("张三", 25, LocalDateTime.now());
//        System.out.println("原始对象: " + user);
//
//        Mono<String> jsonMono = ReactiveJsonUtils.toJsonMono(user);
//        jsonMono.subscribe(json -> {
//            System.out.println("\n对象转JSON成功:");
//            System.out.println(json);
//        }, error -> {
//            System.err.println("对象转JSON失败: " + error.getMessage());
//        });
//
//        // 测试JSON转对象
//        String testJson = "{\"name\":\"李四\",\"age\":30,\"registerTime\":\"2023-05-15T10:30:45\"}";
//        System.out.println("\n测试JSON字符串: " + testJson);
//
//        Mono<User> userMono = ReactiveJsonUtils.parseMono(testJson, User.class);
//        userMono.subscribe(parsedUser -> {
//            System.out.println("\nJSON转对象成功:");
//            System.out.println(parsedUser);
//        }, error -> {
//            System.err.println("JSON转对象失败: " + error.getMessage());
//        });
//
//        // 测试错误情况
//        String invalidJson = "{invalid json}";
//        System.out.println("\n测试无效JSON字符串: " + invalidJson);
//
//        Mono<User> errorMono = ReactiveJsonUtils.parseMono(invalidJson, User.class);
//        errorMono.subscribe(parsedUser -> {
//            System.out.println("这行不应该被执行");
//        }, error -> {
//            System.err.println("错误处理(不应该看到这个，因为我们在工具类中已经处理了错误): " + error.getMessage());
//        }, () -> {
//            System.out.println("\n无效JSON按预期返回了空的Mono");
//        });
//
//        // 等待异步操作完成
//        TimeUnit.SECONDS.sleep(1);
//    }
//
//    // 测试用的POJO类
//    static class User {
//        private String name;
//        private int age;
//        private LocalDateTime registerTime;
//
//        // 必须有无参构造函数
//        public User() {
//        }
//
//        public User(String name, int age, LocalDateTime registerTime) {
//            this.name = name;
//            this.age = age;
//            this.registerTime = registerTime;
//        }
//
//        // 必须要有getter/setter方法
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public int getAge() {
//            return age;
//        }
//
//        public void setAge(int age) {
//            this.age = age;
//        }
//
//        public LocalDateTime getRegisterTime() {
//            return registerTime;
//        }
//
//        public void setRegisterTime(LocalDateTime registerTime) {
//            this.registerTime = registerTime;
//        }
//
//        @Override
//        public String toString() {
//            return "User{" +
//                    "name='" + name + '\'' +
//                    ", age=" + age +
//                    ", registerTime=" + registerTime +
//                    '}';
//        }
//    }
}