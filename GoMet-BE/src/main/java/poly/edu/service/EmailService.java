package poly.edu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // ─── XÁC THỰC TÀI KHOẢN (OTP) ─────────────────────────────────────────────

    /**
     * Send a beautifully styled HTML email containing the 6-digit OTP code.
     */
    @Async
    public void sendOtpEmail(String toEmail, String toName, String otp) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAddress, "GoMet - Cộng đồng Ẩm thực");
            helper.setTo(toEmail);
            helper.setSubject("GoMet — Mã xác nhận tài khoản của bạn");
            helper.setText(buildHtml(toName, otp, toEmail), true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    private String buildHtml(String name, String otp, String email) {
        String verifyLink = frontendUrl + "/verify-email?email=" + email + "&token=" + otp;
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Xác nhận tài khoản GoMet</title>
        </head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr>
              <td align="center">
                <table width="520" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:20px;overflow:hidden;
                              box-shadow:0 8px 30px rgba(0,0,0,0.08);">
                  <tr>
                    <td style="background:#1C1917;padding:32px 40px;text-align:center;">
                      <h1 style="margin:0;color:#EA580C;font-size:28px;letter-spacing:4px;font-weight:900;">
                        GOMET.
                      </h1>
                      <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;text-transform:uppercase;">
                        Cộng đồng Ẩm thực Tinh hoa
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:40px 40px 20px;">
                      <p style="color:#57534E;font-size:16px;margin:0 0 8px;">Xin chào <strong style="color:#1C1917;">%s</strong>,</p>
                      <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 32px;">
                        Cảm ơn bạn đã tham gia <strong>GoMet</strong>! Vui lòng sử dụng mã xác nhận bên dưới 
                        để hoàn tất quá trình đăng ký. Mã này sẽ hết hạn trong <strong>10 phút</strong>.
                      </p>
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                        <tr>
                          <td align="center">
                            <a href="%s"
                               style="display:inline-block;background:#EA580C;color:#ffffff;text-decoration:none;
                                      font-size:15px;font-weight:800;letter-spacing:1px;padding:16px 40px;
                                      border-radius:12px;box-shadow:0 4px 15px rgba(234,88,12,0.35)">
                              ✅ &nbsp;Xác nhận Tài khoản
                            </a>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:12px;text-align:center;margin:0 0 8px;">
                        Hoặc nhập mã này theo cách thủ công trên trang xác thực:
                      </p>
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                        <tr>
                          <td align="center">
                            <div style="background:#FFF7ED;border:2px dashed #EA580C;border-radius:16px;
                                        padding:28px 40px;display:inline-block;">
                              <p style="margin:0 0 8px;color:#9A3412;font-size:12px;letter-spacing:3px;
                                         font-weight:700;text-transform:uppercase;">Mã xác nhận của bạn</p>
                              <p style="margin:0;color:#1C1917;font-size:48px;font-weight:900;
                                         letter-spacing:14px;font-variant-numeric:tabular-nums;">
                                %s
                              </p>
                            </div>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:13px;text-align:center;margin:0 0 32px;">
                        Nếu bạn không tạo tài khoản này, bạn có thể bỏ qua email một cách an toàn.
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                      <p style="color:#A8A29E;font-size:12px;margin:0;">
                        © 2026 GoMet - Culinary Community · Mọi quyền được bảo lưu.
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
                .formatted(name, verifyLink, otp);
    }

    // ─── KHÔI PHỤC MẬT KHẨU ───────────────────────────────────────────────────

    /**
     * Send a password-reset link email to the user.
     */
    @Async
    public void sendResetPasswordEmail(String toEmail, String toName, String rawToken) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAddress, "GoMet - Cộng đồng Ẩm thực");
            helper.setTo(toEmail);
            helper.setSubject("GoMet — Đặt lại mật khẩu của bạn");
            helper.setText(buildResetHtml(toName, rawToken), true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset-password email: " + e.getMessage(), e);
        }
    }

    private String buildResetHtml(String name, String rawToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Đặt lại mật khẩu GoMet</title>
        </head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr>
              <td align="center">
                <table width="520" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:20px;overflow:hidden;
                              box-shadow:0 8px 30px rgba(0,0,0,0.08);">
                  <tr>
                    <td style="background:#1C1917;padding:32px 40px;text-align:center;">
                      <h1 style="margin:0;color:#EA580C;font-size:28px;letter-spacing:4px;font-weight:900;">GOMET.</h1>
                      <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;text-transform:uppercase;">Cộng đồng Ẩm thực Tinh hoa</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:40px 40px 20px;">
                      <p style="color:#57534E;font-size:16px;margin:0 0 8px;">
                        Xin chào <strong style="color:#1C1917;">%s</strong>,
                      </p>
                      <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 32px;">
                        Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>GoMet</strong> của bạn.
                        Nhấp vào nút bên dưới để thiết lập mật khẩu mới. Đường liên kết này có hiệu lực trong vòng
                        <strong>30 phút</strong> và chỉ có thể sử dụng một lần.
                      </p>
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                        <tr>
                          <td align="center">
                            <a href="%s"
                               style="display:inline-block;background:#EA580C;color:#ffffff;text-decoration:none;
                                      font-size:15px;font-weight:800;letter-spacing:1px;padding:16px 40px;
                                      border-radius:12px;box-shadow:0 4px 15px rgba(234,88,12,0.35)">
                              🔐 &nbsp;Đặt Lại Mật Khẩu
                            </a>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:13px;text-align:center;margin:0 0 16px;">
                        Nếu bạn không yêu cầu đặt lại mật khẩu, bạn có thể bỏ qua email này.<br>
                        Mật khẩu của bạn sẽ <strong>không</strong> bị thay đổi.
                      </p>
                      <p style="color:#A8A29E;font-size:12px;text-align:center;margin:0 0 8px;word-break:break-all;">
                        Nút bấm không hoạt động? Vui lòng sao chép và dán liên kết sau vào trình duyệt:<br>
                        <span style="color:#EA580C;">%s</span>
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                      <p style="color:#A8A29E;font-size:12px;margin:0;">
                        © 2026 GoMet - Culinary Community · Mọi quyền được bảo lưu.
                      </p>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(name, resetLink, resetLink);
    }

    // ─── KẾT QUẢ KHIẾU NẠI (APPEAL DECISION) ───────────────────────────────────

    /**
     * Send appeal decision email (Approved or Rejected) in Vietnamese.
     */
    @Async
    public void sendAppealDecisionEmail(String toEmail, String status, String note) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAddress, "GoMet - Cộng đồng Ẩm thực");
            helper.setTo(toEmail);

            String subject;
            String htmlContent;

            String safeNote = (note != null && !note.trim().isEmpty()) ? note : "Không có ghi chú bổ sung.";

            if ("Approved".equalsIgnoreCase(status)) {
                subject = "Tin vui: Tài khoản của bạn đã được Mở khóa";
                htmlContent = buildAppealApprovedHtml(safeNote);
            } else {
                subject = "Phản hồi: Đơn khiếu nại tài khoản bị từ chối";
                htmlContent = buildAppealRejectedHtml(safeNote);
            }

            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(msg);

            System.out.println("=== SEND APPEAL EMAIL ===");
            System.out.println("TO: " + toEmail);
            System.out.println("STATUS: " + status);
            System.out.println("=========================");

        } catch (Exception e) {
            System.err.println("Error sending appeal email to " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildAppealApprovedHtml(String note) {
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head><meta charset="UTF-8"><title>Phê duyệt khiếu nại</title></head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:Arial,sans-serif;">
          <table width="100%%" style="background:#F9F5F0;padding:40px 0;"><tr><td align="center">
            <table width="520" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.08);">
              <tr><td style="background:#1C1917;padding:32px 40px;text-align:center;">
                <h1 style="margin:0;color:#EA580C;font-size:28px;font-weight:900;">GOMET.</h1>
                <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;text-transform:uppercase;">Cộng đồng Ẩm thực Tinh hoa</p>
              </td></tr>
              <tr><td style="padding:40px;">
                <div style="text-align:center;margin-bottom:24px;">
                  <div style="font-size:48px;margin-bottom:16px;">✅</div>
                  <h2 style="margin:0;color:#16A34A;font-size:22px;">Tài khoản đã được mở khóa!</h2>
                </div>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Xin chào,</p>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Ban quản trị đã xem xét đơn khiếu nại của bạn và quyết định <strong>Gỡ Cấm (Unban)</strong> cho tài khoản này.</p>
                
                <div style="background:#F0FDF4;border-left:4px solid #16A34A;padding:16px;margin:20px 0;border-radius:0 8px 8px 0;">
                  <p style="margin:0 0 5px;color:#166534;font-size:13px;font-weight:bold;text-transform:uppercase;">Lời nhắn từ Ban quản trị:</p>
                  <p style="margin:0;color:#15803D;font-size:14px;font-style:italic;">"%s"</p>
                </div>

                <p style="color:#57534E;font-size:15px;line-height:1.6;">Bạn có thể đăng nhập và tiếp tục trải nghiệm GoMet ngay bây giờ. Hãy cùng nhau xây dựng một cộng đồng ẩm thực văn minh nhé!</p>
              </td></tr>
              <tr>
                <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                  <p style="color:#A8A29E;font-size:12px;margin:0;">© 2026 GoMet - Culinary Community</p>
                </td>
              </tr>
            </table>
          </td></tr></table>
        </body></html>
        """.formatted(note);
    }

    private String buildAppealRejectedHtml(String note) {
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head><meta charset="UTF-8"><title>Từ chối khiếu nại</title></head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:Arial,sans-serif;">
          <table width="100%%" style="background:#F9F5F0;padding:40px 0;"><tr><td align="center">
            <table width="520" style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.08);">
              <tr><td style="background:#1C1917;padding:32px 40px;text-align:center;">
                <h1 style="margin:0;color:#EA580C;font-size:28px;font-weight:900;">GOMET.</h1>
                <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;text-transform:uppercase;">Cộng đồng Ẩm thực Tinh hoa</p>
              </td></tr>
              <tr><td style="padding:40px;">
                <div style="text-align:center;margin-bottom:24px;">
                  <div style="font-size:48px;margin-bottom:16px;">❌</div>
                  <h2 style="margin:0;color:#DC2626;font-size:22px;">Đơn khiếu nại bị từ chối</h2>
                </div>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Xin chào,</p>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Rất tiếc, sau khi xem xét kỹ lưỡng các vi phạm và lịch sử hoạt động, Ban quản trị quyết định <strong>Giữ nguyên hình phạt (Cấm)</strong> đối với tài khoản của bạn.</p>
                
                <div style="background:#FEF2F2;border-left:4px solid #EF4444;padding:16px;margin:20px 0;border-radius:0 8px 8px 0;">
                  <p style="margin:0 0 5px;color:#991B1B;font-size:13px;font-weight:bold;text-transform:uppercase;">Lý do từ chối:</p>
                  <p style="margin:0;color:#7F1D1D;font-size:14px;font-style:italic;">"%s"</p>
                </div>

                <p style="color:#57534E;font-size:15px;line-height:1.6;">Đây là quyết định cuối cùng từ hệ thống. Cảm ơn bạn đã quan tâm tới cộng đồng GoMet.</p>
              </td></tr>
              <tr>
                <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                  <p style="color:#A8A29E;font-size:12px;margin:0;">© 2026 GoMet - Culinary Community</p>
                </td>
              </tr>
            </table>
          </td></tr></table>
        </body></html>
        """.formatted(note);
    }
}