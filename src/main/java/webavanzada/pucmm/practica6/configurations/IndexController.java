package webavanzada.pucmm.practica6.configurations;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@EnableRedisHttpSession
@Controller
public class IndexController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("serverPort", serverPort);

        Integer counter = (Integer) session.getAttribute("counter");

        if (counter == null) {
            counter = 0;
        }

        counter = counter + 1;

        session.setAttribute("counter", counter);

        model.addAttribute("counter", counter);
        model.addAttribute("sessionId", session.getId());

        return "index";
    }
}
