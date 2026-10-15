package com.wakedrive.backend.common.service.impl;

import com.wakedrive.backend.common.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private static final String ACCOUNT_CREATED_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="es" xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <title>WakeDrive - Datos de acceso</title>
              <style>
                body { margin: 0; padding: 0; }
                a { text-decoration: none; }
                @media only screen and (max-width: 620px) {
                  .container { width: 100%% !important; }
                  .px { padding-left: 20px !important; padding-right: 20px !important; }
                  .btn a { display: block !important; }
                }
              </style>
            </head>
            <body style="margin:0; padding:0; background-color:#F1F5F9; font-family:Arial, Helvetica, sans-serif;">

              <div style="display:none; max-height:0; overflow:hidden; opacity:0;">
                Tu cuenta de WakeDrive ha sido creada. Aquí están tus datos de inicio de sesión.
              </div>

              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F1F5F9;">
                <tr>
                  <td align="center" style="padding:32px 12px;">

                    <table role="presentation" class="container" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px; max-width:600px; background-color:#FFFFFF; border-radius:16px; overflow:hidden; border:1px solid #E2E8F0;">

                      <tr>
                        <td style="height:4px; line-height:4px; font-size:0; background-color:#2563EB; background-image:linear-gradient(90deg, #0F172A 0%%, #2563EB 50%%, #14B8A6 100%%);">&nbsp;</td>
                      </tr>

                      <tr>
                        <td class="px" align="center" style="padding:32px 40px 8px 40px; background-color:#0F172A;">
                          <h1 style="margin:0; font-size:24px; line-height:32px; color:#FFFFFF; font-weight:bold;">
                            Bienvenido a WakeDrive
                          </h1>
                          <p style="margin:8px 0 28px 0; font-size:15px; line-height:22px; color:#CBD5E1;">
                            Tu cuenta ha sido creada correctamente
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:32px 40px 8px 40px;">
                          <p style="margin:0 0 12px 0; font-size:16px; line-height:24px; color:#0F172A;">
                            Hola <strong>%s</strong>,
                          </p>
                          <p style="margin:0; font-size:15px; line-height:24px; color:#475569;">
                            Se ha habilitado tu acceso a la plataforma administrativa de WakeDrive. A continuación encontrarás tus datos de inicio de sesión:
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:24px 40px;">
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F8FAFC; border:1px solid #E2E8F0; border-left:4px solid #2563EB; border-radius:12px;">
                            <tr>
                              <td style="padding:20px 24px 10px 24px;">
                                <p style="margin:0 0 4px 0; font-size:12px; line-height:16px; color:#64748B; text-transform:uppercase; letter-spacing:1px; font-weight:bold;">
                                  Correo electrónico
                                </p>
                                <p style="margin:0; font-size:16px; line-height:24px; color:#0F172A; font-weight:bold; word-break:break-all;">
                                  %s
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:0 24px;">
                                <div style="height:1px; line-height:1px; font-size:0; background-color:#E2E8F0;">&nbsp;</div>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:10px 24px 20px 24px;">
                                <p style="margin:0 0 4px 0; font-size:12px; line-height:16px; color:#64748B; text-transform:uppercase; letter-spacing:1px; font-weight:bold;">
                                  Contraseña
                                </p>
                                <p style="margin:0; font-size:16px; line-height:24px; color:#0F172A; font-weight:bold; font-family:'Courier New', Courier, monospace; letter-spacing:1px;">
                                  %s
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <tr>
                        <td class="px btn" align="center" style="padding:4px 40px 28px 40px;">
                          <a href="%s" target="_blank" style="display:inline-block; background-color:#2563EB; color:#FFFFFF; font-size:16px; font-weight:bold; line-height:20px; padding:14px 36px; border-radius:10px;">
                            Iniciar sesión
                          </a>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 32px 40px;">
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#FEF9C3; border:1px solid #EAB308; border-radius:12px;">
                            <tr>
                              <td style="padding:16px 20px;">
                                <p style="margin:0 0 4px 0; font-size:14px; line-height:20px; color:#854D0E; font-weight:bold;">
                                  &#9888; Recomendación de seguridad
                                </p>
                                <p style="margin:0; font-size:14px; line-height:21px; color:#713F12;">
                                  Por tu seguridad, deberás cambiar esta contraseña la primera vez que ingreses y no debes compartir estos datos con nadie.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 32px 40px;">
                          <p style="margin:0; font-size:13px; line-height:20px; color:#64748B;">
                            Si no solicitaste esta cuenta o crees que se trata de un error, comunícate con el administrador del sistema.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td align="center" style="padding:24px; background-color:#0F172A;">
                          <p style="margin:0 0 6px 0; font-size:14px; line-height:20px; color:#FFFFFF; font-weight:bold;">
                            Wake<span style="color:#2563EB;">Dri</span><span style="color:#14B8A6;">ve</span>
                          </p>
                          <p style="margin:0; font-size:12px; line-height:18px; color:#94A3B8;">
                            Este es un correo automático, por favor no respondas a este mensaje.<br>
                            &copy; %d WakeDrive. Todos los derechos reservados.
                          </p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """;

    private static final String PASSWORD_RESET_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="es" xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <title>WakeDrive - Recuperar contraseña</title>
              <style>
                body { margin: 0; padding: 0; }
                a { text-decoration: none; }
                @media only screen and (max-width: 620px) {
                  .container { width: 100% !important; }
                  .px { padding-left: 20px !important; padding-right: 20px !important; }
                  .btn a { display: block !important; }
                }
              </style>
            </head>
            <body style="margin:0; padding:0; background-color:#F1F5F9; font-family:Arial, Helvetica, sans-serif;">

              <div style="display:none; max-height:0; overflow:hidden; opacity:0;">
                Recibimos una solicitud para restablecer tu contraseña de WakeDrive.
              </div>

              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F1F5F9;">
                <tr>
                  <td align="center" style="padding:32px 12px;">

                    <table role="presentation" class="container" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px; max-width:600px; background-color:#FFFFFF; border-radius:16px; overflow:hidden; border:1px solid #E2E8F0;">

                      <tr>
                        <td style="height:4px; line-height:4px; font-size:0; background-color:#2563EB; background-image:linear-gradient(90deg, #0F172A 0%, #2563EB 50%, #14B8A6 100%);">&nbsp;</td>
                      </tr>

                      <tr>
                        <td class="px" align="center" style="padding:32px 40px 8px 40px; background-color:#0F172A;">
                          <h1 style="margin:0; font-size:24px; line-height:32px; color:#FFFFFF; font-weight:bold;">
                            Recupera tu contraseña
                          </h1>
                          <p style="margin:8px 0 28px 0; font-size:15px; line-height:22px; color:#CBD5E1;">
                            Recibimos una solicitud para restablecerla
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:32px 40px 8px 40px;">
                          <p style="margin:0 0 12px 0; font-size:16px; line-height:24px; color:#0F172A;">
                            Hola <strong>[NOMBRE_USUARIO]</strong>,
                          </p>
                          <p style="margin:0; font-size:15px; line-height:24px; color:#475569;">
                            Recibimos una solicitud para restablecer la contraseña de tu cuenta de WakeDrive. Haz clic en el botón para crear una nueva contraseña:
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px btn" align="center" style="padding:28px 40px 24px 40px;">
                          <a href="[ENLACE_RESTABLECIMIENTO]" target="_blank" style="display:inline-block; background-color:#2563EB; color:#FFFFFF; font-size:16px; font-weight:bold; line-height:20px; padding:14px 36px; border-radius:10px;">
                            Restablecer contraseña
                          </a>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 24px 40px;">
                          <p style="margin:0 0 6px 0; font-size:13px; line-height:20px; color:#64748B;">
                            Si el botón no funciona, copia y pega este enlace en tu navegador:
                          </p>
                          <p style="margin:0; font-size:13px; line-height:20px; color:#2563EB; word-break:break-all;">
                            [ENLACE_RESTABLECIMIENTO]
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 32px 40px;">
                          <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#FEF9C3; border:1px solid #EAB308; border-radius:12px;">
                            <tr>
                              <td style="padding:16px 20px;">
                                <p style="margin:0 0 4px 0; font-size:14px; line-height:20px; color:#854D0E; font-weight:bold;">
                                  &#9888; Recomendación de seguridad
                                </p>
                                <p style="margin:0; font-size:14px; line-height:21px; color:#713F12;">
                                  Este enlace es válido por 2 horas y solo puede usarse una vez. No lo compartas con nadie.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 32px 40px;">
                          <p style="margin:0; font-size:13px; line-height:20px; color:#64748B;">
                            Si no solicitaste este cambio, puedes ignorar este mensaje. Tu contraseña actual seguirá funcionando.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td align="center" style="padding:24px; background-color:#0F172A;">
                          <p style="margin:0 0 6px 0; font-size:14px; line-height:20px; color:#FFFFFF; font-weight:bold;">
                            Wake<span style="color:#2563EB;">Dri</span><span style="color:#14B8A6;">ve</span>
                          </p>
                          <p style="margin:0; font-size:12px; line-height:18px; color:#94A3B8;">
                            Este es un correo automático, por favor no respondas a este mensaje.<br>
                            &copy; [ANIO] WakeDrive. Todos los derechos reservados.
                          </p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """;

    private static final String COMPANY_REJECTED_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="es" xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <title>WakeDrive - Solicitud de empresa no aceptada</title>
              <style>
                body { margin: 0; padding: 0; }
                a { text-decoration: none; }
                @media only screen and (max-width: 620px) {
                  .container { width: 100% !important; }
                  .px { padding-left: 20px !important; padding-right: 20px !important; }
                  .btn a { display: block !important; }
                }
              </style>
            </head>
            <body style="margin:0; padding:0; background-color:#F1F5F9; font-family:Arial, Helvetica, sans-serif;">

              <div style="display:none; max-height:0; overflow:hidden; opacity:0;">
                No pudimos aceptar la solicitud de tu empresa en WakeDrive. Puedes corregirla e intentarlo de nuevo.
              </div>

              <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#F1F5F9;">
                <tr>
                  <td align="center" style="padding:32px 12px;">

                    <table role="presentation" class="container" width="600" cellpadding="0" cellspacing="0" border="0" style="width:600px; max-width:600px; background-color:#FFFFFF; border-radius:16px; overflow:hidden; border:1px solid #E2E8F0;">

                      <tr>
                        <td style="height:4px; line-height:4px; font-size:0; background-color:#2563EB; background-image:linear-gradient(90deg, #0F172A 0%, #2563EB 50%, #14B8A6 100%);">&nbsp;</td>
                      </tr>

                      <tr>
                        <td class="px" align="center" style="padding:32px 40px 8px 40px; background-color:#0F172A;">
                          <h1 style="margin:0; font-size:24px; line-height:32px; color:#FFFFFF; font-weight:bold;">
                            Tu empresa no fue aceptada
                          </h1>
                          <p style="margin:8px 0 28px 0; font-size:15px; line-height:22px; color:#CBD5E1;">
                            No pudimos aprobar la solicitud por ahora
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:32px 40px 8px 40px;">
                          <p style="margin:0 0 12px 0; font-size:16px; line-height:24px; color:#0F172A;">
                            Hola <strong>[NOMBRE_USUARIO]</strong>,
                          </p>
                          <p style="margin:0; font-size:15px; line-height:24px; color:#475569;">
                            Revisamos la solicitud de registro de <strong style="color:#0F172A;">[NOMBRE_EMPRESA]</strong> en WakeDrive y, por el momento, no pudimos aceptarla. No te preocupes: puedes corregir la información y volver a intentarlo.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px btn" align="center" style="padding:28px 40px 24px 40px;">
                          <a href="[ENLACE_REINTENTO]" target="_blank" style="display:inline-block; background-color:#2563EB; color:#FFFFFF; font-size:16px; font-weight:bold; line-height:20px; padding:14px 36px; border-radius:10px;">
                            Intentar nuevamente
                          </a>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 24px 40px;">
                          <p style="margin:0 0 6px 0; font-size:13px; line-height:20px; color:#64748B;">
                            Si el botón no funciona, copia y pega este enlace en tu navegador:
                          </p>
                          <p style="margin:0; font-size:13px; line-height:20px; color:#2563EB; word-break:break-all;">
                            [ENLACE_REINTENTO]
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td class="px" style="padding:0 40px 32px 40px;">
                          <p style="margin:0; font-size:13px; line-height:20px; color:#64748B;">
                            Revisa los datos y documentos de tu empresa antes de enviar la nueva solicitud. Si crees que se trata de un error, comunícate con el equipo de WakeDrive.
                          </p>
                        </td>
                      </tr>

                      <tr>
                        <td align="center" style="padding:24px; background-color:#0F172A;">
                          <p style="margin:0 0 6px 0; font-size:14px; line-height:20px; color:#FFFFFF; font-weight:bold;">
                            Wake<span style="color:#2563EB;">Dri</span><span style="color:#14B8A6;">ve</span>
                          </p>
                          <p style="margin:0; font-size:12px; line-height:18px; color:#94A3B8;">
                            Este es un correo automático, por favor no respondas a este mensaje.<br>
                            &copy; [ANIO] WakeDrive. Todos los derechos reservados.
                          </p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """;

    @Override
    public void sendAccountCreatedEmail(String to, String adminName, String email, String password) {
        String html = ACCOUNT_CREATED_TEMPLATE.formatted(
                HtmlUtils.htmlEscape(adminName),
                HtmlUtils.htmlEscape(email),
                HtmlUtils.htmlEscape(password),
                HtmlUtils.htmlEscape(frontendUrl + "/auth/login"),
                Year.now().getValue());
        sendHtml(to, "Bienvenido a WakeDrive - Datos de acceso", html);
    }

    @Override
    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        String html = PASSWORD_RESET_TEMPLATE
                .replace("[NOMBRE_USUARIO]", HtmlUtils.htmlEscape(userName))
                .replace("[ENLACE_RESTABLECIMIENTO]", HtmlUtils.htmlEscape(resetLink))
                .replace("[ANIO]", String.valueOf(Year.now().getValue()));
        sendHtml(to, "WakeDrive - Recuperar contraseña", html);
    }

    @Override
    public void sendCompanyRejectedEmail(String to, String userName, String companyName, String retryLink) {
        String html = COMPANY_REJECTED_TEMPLATE
                .replace("[NOMBRE_USUARIO]", HtmlUtils.htmlEscape(userName))
                .replace("[NOMBRE_EMPRESA]", HtmlUtils.htmlEscape(companyName))
                .replace("[ENLACE_REINTENTO]", HtmlUtils.htmlEscape(retryLink))
                .replace("[ANIO]", String.valueOf(Year.now().getValue()));
        sendHtml(to, "WakeDrive - Tu empresa no fue aceptada", html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
