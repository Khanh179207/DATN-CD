package poly.edu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.FollowDAO;
import poly.edu.dao.PostDAO;
import poly.edu.dto.ContactDTO;
import poly.edu.entity.Account;
import poly.edu.entity.Follow;
import poly.edu.entity.Post;

import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharePostService {

    private final FollowDAO followDAO;
    private final AccountDAO accountDAO;
    private final PostDAO postDAO;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Get a list of unique contacts (followers and following) for sharing.
     */
    public List<ContactDTO> getSharingContacts(Integer accountID) {
        // Get people I follow
        List<Follow> following = followDAO.findByFollower_AccountID(accountID);
        // Get people following me
        List<Follow> followers = followDAO.findByFollowee_AccountID(accountID);

        List<Account> contacts = new ArrayList<>();

        // Add followees (the people I follow)
        contacts.addAll(following.stream()
                .filter(f -> f.getStatus() == 1)
                .map(Follow::getFollowee)
                .collect(Collectors.toList()));

        // Add followers (the people following me)
        contacts.addAll(followers.stream()
                .filter(f -> f.getStatus() == 1)
                .map(Follow::getFollower)
                .collect(Collectors.toList()));

        // Deduplicate by AccountID and map to DTO
        return contacts.stream()
                .filter(a -> !a.getAccountID().equals(accountID)) // Don't include myself
                .collect(Collectors.toMap(
                        Account::getAccountID,
                        a -> a,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .map(a -> ContactDTO.builder()
                        .accountID(a.getAccountID())
                        .username(a.getUsername())
                        .avatar(a.getAvatar())
                        .email(a.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Send share post email asynchronously.
     */
    @Async
    public void sendSharePostEmail(String toEmail, Integer postID, String senderName, String customMessage) {
        try {
            // Log để debug (sẽ hiện trong console)
            System.out.println("=== ATTEMPTING TO SEND SHARE POST EMAIL ===");
            System.out.println("FROM: " + fromAddress);
            System.out.println("TO: " + toEmail);
            System.out.println("POST ID: " + postID);

            Post post = postDAO.findById(postID).orElseThrow(() -> new RuntimeException("Post not found"));
            
            MimeMessage msg = mailSender.createMimeMessage();
            // Cấu hình UTF-8 đồng nhất với EmailService
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAddress, "GoMet - Cộng đồng Ẩm thực");
            helper.setTo(toEmail);
            helper.setSubject(senderName + " đã chia sẻ một công thức nấu ăn với bạn!");
            
            String htmlContent = buildSharePostHtml(senderName, post, customMessage);
            helper.setText(htmlContent, true);

            mailSender.send(msg);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR IN SHARE POST EMAIL: " + e.getMessage());
            // Ném lỗi để Spring có thể track được nếu cần
            throw new RuntimeException("Failed to send share post email: " + e.getMessage(), e);
        }
    }

    private String buildSharePostHtml(String senderName, Post post, String customMessage) {
        String postLink = frontendUrl + "/post/" + post.getPostID();
        String messageBody = (customMessage != null && !customMessage.isEmpty()) 
                ? customMessage 
                : "Chào bạn, mình thấy công thức này rất hay và muốn chia sẻ với bạn!";
        
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>GoMet - Chia sẻ công thức</title>
        </head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr>
              <td align="center">
                <table width="560" cellpadding="0" cellspacing="0" 
                       style="background:#ffffff;border-radius:24px;overflow:hidden;
                              box-shadow:0 12px 40px rgba(0,0,0,0.1);">
                  <!-- Header -->
                  <tr>
                    <td style="background:#1C1917;padding:32px 40px;text-align:center;">
                      <h1 style="margin:0;color:#EA580C;font-size:26px;letter-spacing:3px;font-weight:900;text-transform:uppercase;">
                        GOMET.
                      </h1>
                      <p style="margin:8px 0 0;color:#A8A29E;font-size:12px;letter-spacing:1px;text-transform:uppercase;">
                        Cộng đồng Ẩm thực Tinh hoa
                      </p>
                    </td>
                  </tr>
                  
                  <!-- Body -->
                  <tr>
                    <td style="padding:40px;">
                      <p style="color:#57534E;font-size:16px;line-height:1.6;margin:0 0 24px;">
                        Chào bạn, <strong style="color:#1C1917;">%s</strong> vừa chia sẻ một bài viết thú vị với bạn:
                      </p>
                      
                      <!-- Message Box -->
                      <div style="background:#FFF7ED;border-left:4px solid #EA580C;padding:20px;border-radius:0 12px 12px 0;margin-bottom:32px;">
                        <p style="margin:0;color:#9A3412;font-size:15px;font-style:italic;line-height:1.6;">
                          "%s"
                        </p>
                      </div>

                      <!-- Post Card -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="background:#FAFAF9;border-radius:16px;overflow:hidden;border:1px solid #E7E5E4;">
                        <tr>
                          <td>
                            <img src="%s" alt="%s" style="width:100%%;max-height:280px;object-fit:cover;display:block;">
                          </td>
                        </tr>
                        <tr>
                          <td style="padding:24px;">
                            <h2 style="margin:0 0 12px;color:#1C1917;font-size:22px;font-weight:800;">%s</h2>
                            <p style="margin:0 0 20px;color:#78716C;font-size:14px;line-height:1.5;">%s</p>
                            <a href="%s" 
                               style="display:inline-block;background:#EA580C;color:#ffffff;text-decoration:none;
                                      font-size:14px;font-weight:700;padding:12px 28px;border-radius:10px;
                                      box-shadow:0 4px 12px rgba(234,88,12,0.2)">
                              Xem chi tiết công thức
                            </a>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  
                  <!-- Footer -->
                  <tr>
                    <td style="background:#F5F5F4;padding:24px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                      <p style="color:#A8A29E;font-size:12px;margin:0 0 8px;">
                        Bạn nhận được email này vì ai đó đã sử dụng tính năng chia sẻ tại GoMet.
                      </p>
                      <p style="color:#A8A29E;font-size:12px;margin:0;">
                        © 2026 GoMet Culinary · www.gomet.vn
                      </p>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """
        .formatted(
            senderName, 
            messageBody, 
            post.getMedia() != null ? post.getMedia() : "https://via.placeholder.com/600x400?text=GoMet+Recipe",
            post.getTitle(),
            post.getTitle(), 
            post.getDescription().length() > 150 ? post.getDescription().substring(0, 147) + "..." : post.getDescription(),
            postLink
        );
    }
}
