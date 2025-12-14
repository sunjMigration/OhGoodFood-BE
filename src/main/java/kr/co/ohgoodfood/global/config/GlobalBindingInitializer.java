package kr.co.ohgoodfood.global.config;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import kr.co.ohgoodfood.util.StringEscapeEditor;

@ControllerAdvice
public class GlobalBindingInitializer {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 모든 String 파라미터에 escape 필터 적용
        binder.registerCustomEditor(String.class, new StringEscapeEditor());
    }
}