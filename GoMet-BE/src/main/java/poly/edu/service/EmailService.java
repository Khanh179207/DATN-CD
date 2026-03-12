package poly.edu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private static final String DEFAULT_RECIPIENT_NAME = "Chef";

    /**
     * Send a beautifully styled HTML email containing the 6-digit OTP code.
     */
    public void sendOtpEmail(String toEmail, String toName, String otp) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = prepareHelper(msg, "GoMet - Culinary Community");
            helper.setTo(toEmail);
            helper.setSubject("GoMet — Your Verification Code");
            helper.setText(buildHtml(toName, otp, toEmail), true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
        }
    }

    private String buildHtml(String name, String otp, String email) {
      String verifyLink = buildFrontendUrl("/verify-email", Map.of(
        "email", email,
        "token", otp
      ));
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
        """.formatted(displayName(name), verifyLink, otp);
    }

    // ─── RESET PASSWORD EMAIL ─────────────────────────────────────────────────

    /**
     * Send a password-reset link email to the user.
     * The rawToken is embedded in the link — it is NEVER stored in the database.
     */
    public void sendResetPasswordEmail(String toEmail, String toName, String rawToken) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = prepareHelper(msg, "GoMet - Culinary Community");
            helper.setTo(toEmail);
            helper.setSubject("GoMet — Reset Your Password");
            helper.setText(buildResetHtml(toName, rawToken), true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset-password email: " + e.getMessage(), e);
        }
    }

    private String buildResetHtml(String name, String rawToken) {
      String resetLink = buildFrontendUrl("/reset-password", Map.of("token", rawToken));
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
        """.formatted(displayName(name), resetLink, resetLink);
    }
    // ─── SUSPICIOUS LOGIN EMAIL ───────────────────────────────────────────────

    /**
     * Sent when a login from an unrecognized device is detected.
     * Contains a magic link to verify the device and a "This wasn't me" link to revoke all.
     */
    public void sendSuspiciousLoginEmail(String toEmail, String toName,
                                          String verifyToken, String wasntMeToken,
                                          String ip, String deviceName) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = prepareHelper(msg, "GoMet Security");
            helper.setTo(toEmail);
            helper.setSubject("⚠️ GoMet — Verify New Login from " + deviceName);
            helper.setText(buildSuspiciousLoginHtml(toName, verifyToken, wasntMeToken, ip, deviceName), true);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send suspicious-login email: " + e.getMessage(), e);
        }
    }

    private String buildSuspiciousLoginHtml(String name, String verifyToken, String wasntMeToken,
                                             String ip, String deviceName) {
  String verifyLink = buildFrontendUrl("/auth/verify-login", Map.of("token", verifyToken));
  String wasntMeLink = buildFrontendUrl("/auth/verify-login", Map.of(
    "token", wasntMeToken,
    "action", "wasnt-me"
  ));
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head><meta charset="UTF-8"><title>GoMet Security Alert</title></head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr><td align="center">
              <table width="520" cellpadding="0" cellspacing="0"
                     style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.08);">
                <tr>
                  <td style="background:#1C1917;padding:32px 40px;text-align:center;">
                    <h1 style="margin:0;color:#EA580C;font-size:28px;letter-spacing:4px;font-weight:900;">GOMET.</h1>
                    <p style="margin:8px 0 0;color:#A8A29E;font-size:13px;">SECURITY ALERT</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding:40px;">
                    <p style="color:#57534E;font-size:16px;margin:0 0 8px;">Hi <strong>%s</strong>,</p>
                    <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 16px;">
                      We detected a login attempt from a new device or location:
                    </p>
                    <table style="background:#FFF7ED;border-radius:12px;padding:16px 20px;margin-bottom:24px;width:100%%;">
                      <tr><td style="color:#9A3412;font-size:13px;">
                        <strong>Device:</strong> %s<br>
                        <strong>IP Address:</strong> %s
                      </td></tr>
                    </table>
                    <p style="color:#78716C;font-size:15px;margin:0 0 24px;">
                      Was this you? Click below to verify this device and complete your login:
                    </p>
                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:16px;">
                      <tr><td align="center">
                        <a href="%s" style="display:inline-block;background:#16A34A;color:#fff;text-decoration:none;
                           font-size:14px;font-weight:800;padding:14px 36px;border-radius:10px;">
                          ✅ &nbsp;Yes, this was me — Verify Device
                        </a>
                      </td></tr>
                    </table>
                    <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                      <tr><td align="center">
                        <a href="%s" style="display:inline-block;background:#DC2626;color:#fff;text-decoration:none;
                           font-size:14px;font-weight:800;padding:14px 36px;border-radius:10px;">
                          🚨 &nbsp;No, this wasn't me — Secure My Account
                        </a>
                      </td></tr>
                    </table>
                    <p style="color:#A8A29E;font-size:12px;text-align:center;">
                      This verification link expires in <strong>10 minutes</strong>.
                    </p>
                  </td>
                </tr>
                <tr>
                  <td style="background:#F5F5F4;padding:20px 40px;text-align:center;border-top:1px solid #E7E5E4;">
                    <p style="color:#A8A29E;font-size:12px;margin:0;">© 2026 GoMet Culinary Community</p>
                  </td>
                </tr>
              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(displayName(name), deviceName, ip, verifyLink, wasntMeLink);
    }

    // ─── PROFILE UPDATE EMAIL ─────────────────────────────────────────────────

    /**
     * Sent when a user updates their profile information (username, avatar, or bio).
     * Lists the fields that were changed in a friendly summary.
     */
    public void sendProfileUpdateEmail(String toEmail, String toName, java.util.List<String> changedFields) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = prepareHelper(msg, "GoMet - Culinary Community");
            helper.setTo(toEmail);
            helper.setSubject("GoMet — Your Profile Has Been Updated");
            helper.setText(buildProfileUpdateHtml(toName, changedFields), true);

            mailSender.send(msg);
        } catch (Exception e) {
            // Non-critical: log but don't throw — profile was already saved
            log.warn("Failed to send profile-update email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildProfileUpdateHtml(String name, java.util.List<String> changedFields) {
          String fieldListHtml = safeChangedFields(changedFields).stream()
            .map(f -> "<li style=\"margin:6px 0;color:#57534E;font-size:14px;\">✔ " + f + "</li>")
            .collect(Collectors.joining("\n"));

          String time = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
            .format(DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy"));

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head><meta charset="UTF-8"><title>GoMet Profile Updated</title></head>
        <body style="margin:0;padding:0;background:#F9F5F0;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9F5F0;padding:40px 0;">
            <tr><td align="center">
              <table width="520" cellpadding="0" cellspacing="0"
                     style="background:#ffffff;border-radius:20px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.08);">
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
                    <p style="color:#78716C;font-size:15px;line-height:1.6;margin:0 0 24px;">
                      Your <strong>GoMet</strong> profile was successfully updated on
                      <strong>%s</strong>. The following information was changed:
                    </p>
                    <div style="background:#FFF7ED;border-radius:12px;padding:18px 24px;margin-bottom:28px;">
                      <ul style="margin:0;padding:0 0 0 4px;list-style:none;">
                        %s
                      </ul>
                    </div>
                    <p style="color:#A8A29E;font-size:13px;text-align:center;margin:0 0 8px;">
                      If you did not make these changes, please
                      <a href="%s/settings/security" style="color:#EA580C;text-decoration:none;font-weight:700;">
                        secure your account immediately
                      </a>.
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
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(displayName(name), time, fieldListHtml, normalizeFrontendUrl());
      }

      private MimeMessageHelper prepareHelper(MimeMessage message, String senderName) throws Exception {
        assertMailConfigured();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(fromAddress, senderName);
        return helper;
      }

      private void assertMailConfigured() {
        if (fromAddress == null || fromAddress.isBlank()) {
          throw new IllegalStateException("SMTP sender address is not configured. Set SMTP_USERNAME before sending email.");
        }
      }

      private String buildFrontendUrl(String path, Map<String, String> queryParams) {
        String query = queryParams.entrySet().stream()
            .map(entry -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()))
            .collect(Collectors.joining("&"));
        return normalizeFrontendUrl() + path + (query.isBlank() ? "" : "?" + query);
      }

      private String normalizeFrontendUrl() {
        if (frontendUrl == null || frontendUrl.isBlank()) {
          return "http://localhost:5173";
        }
        return frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
      }

      private String displayName(String name) {
        if (name == null || name.isBlank()) {
          return DEFAULT_RECIPIENT_NAME;
        }
        return name.trim();
      }

      private List<String> safeChangedFields(List<String> changedFields) {
        if (changedFields == null || changedFields.isEmpty()) {
          return List.of("Profile details");
        }
        return changedFields;
      }

      private String urlEncode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
      }
    }
