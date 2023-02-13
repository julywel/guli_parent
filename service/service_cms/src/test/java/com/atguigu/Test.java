package com.atguigu;

import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class Test {

    public static int result(int n){
        if(n == 1){
            return 1;
        }
        int first = 1;
        //计算括号中的第一位
        for(int i = 2; i <= n; i++) {
            first += 2*(i-1);
        }
        System.out.println(first);
        int result = first;
        //计算接下来最后结果
        for(int i = 1 ; i < n ; i++) {
            first += 2;
            result += first;
        }
        return result;
    }

    @org.junit.jupiter.api.Test
    public void test() {
        int result = result(4);
        System.out.println(result);
    }
}
