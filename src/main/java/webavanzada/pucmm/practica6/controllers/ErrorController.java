package webavanzada.pucmm.practica6.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.ui.Model;

import java.util.Map;

@Controller()
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorAttributes errorAttributes;
    @PostMapping("/error")
    public String handleError(WebRequest webRequest, Model model) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        model.addAllAttributes(errorAttributes);
        return "error";
    }

}