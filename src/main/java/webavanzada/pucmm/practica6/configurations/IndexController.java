package webavanzada.pucmm.practica6.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("serverPort", serverPort);
        return "index";
    }
}
