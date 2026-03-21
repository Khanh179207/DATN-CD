package poly.edu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sử dụng đường dẫn vật lý cố định để đảm bảo Spring Boot đọc được ngay cả khi đang chạy
        String userDir = System.getProperty("user.dir");
        Path uploadDir = Paths.get(userDir, "src/main/resources/static/uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath().replace("\\", "/");

        System.out.println(">>> WebConfig: Mapping /uploads/** to file:/" + uploadPath + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}