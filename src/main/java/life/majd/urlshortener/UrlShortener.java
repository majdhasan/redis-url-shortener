package life.majd.urlshortener;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/url")
public class UrlShortener {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/{id}")
    public String getUrl(@PathVariable String id) {
        String url = stringRedisTemplate.opsForValue().get(id);
        if (url != null) {
            System.out.println(url + " has been retrieved from Redis");
            return url;
        }
        System.out.println("ID: " + id + " does not exist in the Redis");
        return "Not found";
    }

    @PostMapping
    public String createUrl(@RequestBody String url) {
        UrlValidator urlValidator = new UrlValidator(
                new String[]{"http", "https"}
        );
        if (urlValidator.isValid(url)) {
            String key = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            System.out.println("New Entry was saved to Redis: " + key + " --> " + url);
            stringRedisTemplate.opsForValue().set(key, url);
            return key;
        }
        throw new RuntimeException("URL is Invalid" + url);

    }
}
