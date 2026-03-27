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

  /**
   * Send a beautifully styled HTML email containing the 6-digit OTP code.
   */
  @Async
  public void sendOtpEmail(String toEmail, String toName, String otp) {
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

      helper.setFrom(fromAddress, "GoMet - Culinary Community");
      helper.setTo(toEmail);
      helper.setSubject("GoMet — Your Verification Code");
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
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>GoMet Verification</title>
        </head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr>
              <td align="center">
                <table width="520" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:20px;overflow:hidden;
                              box-shadow:0 8px 30px rgba(0,0,0,0.08);">
                  <!-- Header banner -->
                  <tr>
                    <td style="background:#1C1917;padding:32px 40px;text-align:center;">
                      <h1 style="margin:0;color:#EA580C;font-size:28px;letter-spacing:4px;font-weight:900;">
                        GOMET.
                      </h1>
                      <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;">
                        CULINARY COMMUNITY
                      </p>
                    </td>
                  </tr>
                  <!-- Body -->
                  <tr>
                    <td style="padding:40px 40px 20px;">
                      <p style="color:#57534E;font-size:16px;margin:0 0 8px;">Hi <strong style="color:#1C1917;">%s</strong>,</p>
                      <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 32px;">
                        Thanks for joining <strong>GoMet</strong>! Use the verification code below
                        to complete your registration. The code expires in <strong>10 minutes</strong>.
                      </p>
                      <!-- Verify button -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                        <tr>
                          <td align="center">
                            <a href="%s"
                               style="display:inline-block;background:#EA580C;color:#ffffff;text-decoration:none;
                                      font-size:15px;font-weight:800;letter-spacing:1px;padding:16px 40px;
                                      border-radius:12px;box-shadow:0 4px 15px rgba(234,88,12,0.35)">
                              ✅ &nbsp;Verify My Account
                            </a>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:12px;text-align:center;margin:0 0 8px;">
                        Or enter the code manually on the verification page:
                      </p>
                      <!-- OTP box -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                        <tr>
                          <td align="center">
                            <div style="background:#FFF7ED;border:2px dashed #EA580C;border-radius:16px;
                                        padding:28px 40px;display:inline-block;">
                              <p style="margin:0 0 8px;color:#9A3412;font-size:12px;letter-spacing:3px;
                                         font-weight:700;text-transform:uppercase;">Your Verification Code</p>
                              <p style="margin:0;color:#1C1917;font-size:48px;font-weight:900;
                                         letter-spacing:14px;font-variant-numeric:tabular-nums;">
                                %s
                              </p>
                            </div>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:13px;text-align:center;margin:0 0 32px;">
                        If you didn't create an account, you can safely ignore this email.
                      </p>
                    </td>
                  </tr>
                  <!-- Footer -->
                  <tr>
                    <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                      <p style="color:#A8A29E;font-size:12px;margin:0;">
                        © 2026 GoMet Culinary Community · All rights reserved
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

  // ─── RESET PASSWORD EMAIL ─────────────────────────────────────────────────

  /**
   * Send a password-reset link email to the user.
   * The rawToken is embedded in the link — it is NEVER stored in the database.
   */
  @Async
  public void sendResetPasswordEmail(String toEmail, String toName, String rawToken) {
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

      helper.setFrom(fromAddress, "GoMet - Culinary Community");
      helper.setTo(toEmail);
      helper.setSubject("GoMet — Reset Your Password");
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
        <html lang="en">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>GoMet Password Reset</title>
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
                      <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;letter-spacing:1px;">CULINARY COMMUNITY</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:40px 40px 20px;">
                      <p style="color:#57534E;font-size:16px;margin:0 0 8px;">
                        Hi <strong style="color:#1C1917;">%s</strong>,
                      </p>
                      <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 32px;">
                        We received a request to reset your <strong>GoMet</strong> password.
                        Click the button below to set a new password. This link is valid for
                        <strong>30 minutes</strong> and can only be used once.
                      </p>
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                        <tr>
                          <td align="center">
                            <a href="%s"
                               style="display:inline-block;background:#EA580C;color:#ffffff;text-decoration:none;
                                      font-size:15px;font-weight:800;letter-spacing:1px;padding:16px 40px;
                                      border-radius:12px;box-shadow:0 4px 15px rgba(234,88,12,0.35)">
                              🔐 &nbsp;Reset My Password
                            </a>
                          </td>
                        </tr>
                      </table>
                      <p style="color:#A8A29E;font-size:13px;text-align:center;margin:0 0 16px;">
                        If you didn't request a password reset, you can safely ignore this email.<br>
                        Your password will <strong>not</strong> be changed.
                      </p>
                      <p style="color:#A8A29E;font-size:12px;text-align:center;margin:0 0 8px;word-break:break-all;">
                        Link not working? Copy and paste:<br>
                        <span style="color:#EA580C;">%s</span>
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                      <p style="color:#A8A29E;font-size:12px;margin:0;">
                        © 2026 GoMet Culinary Community · All rights reserved
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

  // ─── APPEAL DECISION EMAIL ─────────────────────────────────────────────────

  /**
   * Send appeal decision email (Approved or Rejected) in Vietnamese.
   */
    // ─── APPEAL DECISION EMAIL ─────────────────────────────────────────────────

    /**
     * Send appeal decision email (Approved or Rejected) in Vietnamese.
     * 🔥 ĐÃ THÊM THAM SỐ "note"
     */
    @Async
    public void sendAppealDecisionEmail(String toEmail, String status, String note) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(fromAddress, "GoMet - Culinary Community");
            helper.setTo(toEmail);

            String subject;
            String htmlContent;

            // Xử lý null cho note
            String safeNote = (note != null && !note.trim().isEmpty()) ? note : "Không có ghi chú bổ sung.";

            if ("Approved".equalsIgnoreCase(status)) {
                subject = "Tin vui: Tài khoản của bạn đã được Gỡ Ban";
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
              </td></tr>
              <tr><td style="padding:40px;">
                <div style="text-align:center;margin-bottom:24px;">
                  <div style="font-size:48px;margin-bottom:16px;">✅</div>
                  <h2 style="margin:0;color:#16A34A;font-size:22px;">Tài khoản đã được mở khóa!</h2>
                </div>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Xin chào,</p>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Quản trị viên đã xem xét đơn khiếu nại của bạn và quyết định <strong>Gỡ Ban</strong> cho tài khoản này.</p>
                
                <div style="background:#F0FDF4;border-left:4px solid #16A34A;padding:16px;margin:20px 0;border-radius:0 8px 8px 0;">
                  <p style="margin:0 0 5px;color:#166534;font-size:13px;font-weight:bold;text-transform:uppercase;">Lời nhắn từ Quản trị viên:</p>
                  <p style="margin:0;color:#15803D;font-size:14px;font-style:italic;">"%s"</p>
                </div>

                <p style="color:#57534E;font-size:15px;line-height:1.6;">Bạn có thể đăng nhập và trải nghiệm GoMet ngay bây giờ. Hãy nhớ tuân thủ các quy tắc cộng đồng nhé!</p>
              </td></tr>
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
              </td></tr>
              <tr><td style="padding:40px;">
                <div style="text-align:center;margin-bottom:24px;">
                  <div style="font-size:48px;margin-bottom:16px;">❌</div>
                  <h2 style="margin:0;color:#DC2626;font-size:22px;">Đơn khiếu nại bị từ chối</h2>
                </div>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Xin chào,</p>
                <p style="color:#57534E;font-size:15px;line-height:1.6;">Rất tiếc, sau khi xem xét kỹ lưỡng bằng chứng và lịch sử hoạt động, Quản trị viên quyết định <strong>Giữ nguyên hình phạt (Ban)</strong> đối với tài khoản của bạn.</p>
                
                <div style="background:#FEF2F2;border-left:4px solid #EF4444;padding:16px;margin:20px 0;border-radius:0 8px 8px 0;">
                  <p style="margin:0 0 5px;color:#991B1B;font-size:13px;font-weight:bold;text-transform:uppercase;">Lý do từ chối:</p>
                  <p style="margin:0;color:#7F1D1D;font-size:14px;font-style:italic;">"%s"</p>
                </div>

                <p style="color:#57534E;font-size:15px;line-height:1.6;">Quyết định này là quyết định cuối cùng. Cảm ơn bạn đã quan tâm tới cộng đồng GoMet.</p>
              </td></tr>
            </table>
          </td></tr></table>
        </body></html>
        """.formatted(note);
    }
}

