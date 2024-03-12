package webavanzada.pucmm.practica6.configurations;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Value("${server.port}")
    private String serverPort;

    private final RedisTemplate<String, Integer> redisTemplate;

    public IndexController(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("serverPort", serverPort);

        Integer counter = redisTemplate.opsForValue().get("counter");

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
