package com.example.studysecurity.config; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import org.springframework.context.annotation.Bean; // @Bean 어노테이션을 사용하기 위해 import
import org.springframework.context.annotation.Configuration; // @Configuration 어노테이션을 사용하기 위해 import
import org.springframework.web.client.RestTemplate; // RestTemplate을 사용하기 위해 import

@Configuration // 이 클래스가 스프링의 설정(Configuration) 클래스임을 나타냄
public class RestTemplateConfig { // RestTemplate을 빈으로 등록하는 설정 클래스

    @Bean // 스프링 컨테이너에서 관리하는 빈(Bean)으로 등록
    public RestTemplate restTemplate() { // RestTemplate 객체를 생성하고 빈으로 등록하는 메서드
        return new RestTemplate(); // 새로운 RestTemplate 객체를 반환하여 HTTP 요청을 수행할 수 있도록 함
    }
}
