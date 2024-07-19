//package me.Ult1;
//
//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.sql.DataSource;
//import java.util.Properties;
//
//public class Mail {
//
//    String to = "Augustonasa@gmail.com";
//
//    private String
//        from = "Augustonasa@gmail.com",
//        password = "";
//
//    private final MimeMessage message;
//    private final Multipart multipart;
//
//    public Mail() throws MessagingException {
//
//        Properties prop = new Properties();
////        properties.put("mail.smtp.host", "smtp.gmail.com");
////        properties.put("mail.smpt.port", "587");
////        properties.put("mail.smtp.auth", "true");
////        properties.put("mail.smtp.starttls.enable", "true");
//
//        prop.put("mail.smtp.auth", true);
//        prop.put("mail.smtp.starttls.enable", "true");
//        prop.put("mail.smtp.host", "smtp.gmail.com");
//        prop.put("mail.smtp.port", 587);
//        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//
//        // oh yeah, Gmail is made by Google, welp... ctrl + A, ctrl + /
//
//        Session session = Session.getDefaultInstance(prop, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(from, password);
//            }
//        });
//
//        message = new MimeMessage(session);
//        multipart = new MimeMultipart();
//
//        message.setFrom(new InternetAddress(from));
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//
//    }
//
//    public Mail setHeader(String text) throws MessagingException {
//        message.setSubject(text);
//        return this;
//    }
//
//    public Mail addBody(String text) throws MessagingException {
//        BodyPart part = new MimeBodyPart();
//        part.setText(text);
//        multipart.addBodyPart(part);
//        return this;
//    }
//
//    public Mail addAttachment(String filepath, String filename) throws MessagingException {
//        MimeBodyPart part = new MimeBodyPart();
//        part.setDataHandler(new DataHandler(new FileDataSource(filepath)));
//        part.setFileName(filename);
//        return this;
//    }
//
//    public void send() throws MessagingException {
//        message.setContent(multipart);
//
//        Transport.send(message);
//    }
//
//
//}
