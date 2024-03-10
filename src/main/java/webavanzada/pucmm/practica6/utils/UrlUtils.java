package webavanzada.pucmm.practica6.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlUtils {

    public static String urlify(String input) {
        input = input.trim();
        input = input.toLowerCase();

        return input.replace(" ", "-");
    }

    public static String encode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }
}
