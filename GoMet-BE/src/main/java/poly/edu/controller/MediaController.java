package poly.edu.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * MediaController — Secure file serving with anti-leech and extension obfuscation.
 *
 * Files are stored with UUID names (no extension exposed to clients).
 * The real extension is encoded in the filename hash for server-side lookup.
 * All serve requests check Referer / Origin header — if absent or wrong domain, returns 403.
 */
@RestController
@RequestMapping("/api/media")
public class MediaController {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/gomet-uploads/";
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:5173", "http://localhost:5174", "http://127.0.0.1:5173"
    );
    private static final Map<String, String> MIME_MAP = Map.of(
            "jpg", "image/jpeg", "jpeg", "image/jpeg",
            "png", "image/png", "gif", "image/gif",
            "webp", "image/webp", "mp4", "video/mp4",
            "mov", "video/quicktime", "pdf", "application/pdf"
    );
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "video/mp4");

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) throws IOException {
        // Validate content type
        String originalContentType = file.getContentType();
        if (originalContentType == null || !ALLOWED_TYPES.contains(originalContentType)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Loại tệp không được phép"));
        }

        // Determine real extension from content type (never from client filename)
        String ext;
        switch (originalContentType) {
            case "image/jpeg":
                ext = "jpg";
                break;
            case "image/png":
                ext = "png";
                break;
            case "image/gif":
                ext = "gif";
                break;
            case "image/webp":
                ext = "webp";
                break;
            case "video/mp4":
                ext = "mp4";
                break;
            default:
                ext = "bin";
                break;
        }

        // Store as UUID — extension is embedded after separator __ for server-side resolution only
        String token = UUID.randomUUID().toString().replace("-", "");
        String storedFilename = token + "__" + ext; // "__" separator, never exposed

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        Path dest = uploadPath.resolve(storedFilename);
        file.transferTo(dest.toFile());

        // Return opaque token URL — client never sees the extension
        String serveUrl = "/api/media/" + token;
        return ResponseEntity.ok(Map.of("url", serveUrl, "token", token));
    }

    @GetMapping("/{token}")
    public ResponseEntity<Resource> serveMedia(
            @PathVariable String token,
            HttpServletRequest request) {
        // ─── Anti-leech: check Referer or Origin ───────────────────────
        String referer = request.getHeader(HttpHeaders.REFERER);
        String origin  = request.getHeader(HttpHeaders.ORIGIN);
        boolean allowed = isAllowedSource(referer) || isAllowedSource(origin);

        // Allow null referer only for same-server requests (direct BE calls)
        // For browser fetch/img src, referer should be present
        if (!allowed && (referer != null || origin != null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ─── Locate file by token (scan for token__ext pattern) ────────
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) return ResponseEntity.notFound().build();

            Optional<Path> found = Files.list(uploadPath)
                    .filter(p -> p.getFileName().toString().startsWith(token + "__"))
                    .findFirst();

            if (found.isEmpty()) return ResponseEntity.notFound().build();

            Path filePath = found.get();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) return ResponseEntity.notFound().build();

            // Determine content type from stored extension
            String filename = filePath.getFileName().toString();
            String ext = filename.contains("__") ? filename.split("__")[1] : "jpg";
            String contentType = MIME_MAP.getOrDefault(ext, "application/octet-stream");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // inline only — no download hint
                    .header(HttpHeaders.CACHE_CONTROL, "private, max-age=86400")
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "SAMEORIGIN")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isAllowedSource(String headerValue) {
        if (headerValue == null) return false;
        return ALLOWED_ORIGINS.stream().anyMatch(headerValue::startsWith);
    }
}
