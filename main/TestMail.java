package javaMailWithOAuth2.main;

import com.microsoft.aad.msal4j.*;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.internet.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TestMail {
	private static Properties prop;
	private static String clientId;
	private static String tenantId;
	private static String clientSecret;
	private static String authEndpoint;
	private static String redirectURI;
	private static String userName;
	private static String userPassword;
	private static String clientAppType;
	private static String publicClientAcquireToken;
	private static String scopes[];
	private static String recipent;
	private static Set<String> SCOPE;
	
    public static void main(String[] args) {
        IAuthenticationResult authresult = null;
        try {
            prop = new ReadPropertyValues().getProperties();
            clientId=prop.getProperty("clientId", "").trim();
            tenantId=prop.getProperty("tenantId", "").trim();
            authEndpoint=prop.getProperty("authEndpoint", "").trim();
            redirectURI=prop.getProperty("redirectURI", "").trim();
            userName = prop.getProperty("userName", "");
            userPassword = prop.getProperty("userPassword", "");
            clientAppType = prop.getProperty("clientAppType", "");
            publicClientAcquireToken = prop.getProperty("publicClientAcquireToken", "");
            scopes=prop.getProperty("scopes", "").trim().split(",");
            SCOPE = new HashSet<String>(Arrays.asList(scopes));
            recipent = prop.getProperty("recipent", "").trim();
            
            authresult = TestMail.acquireToken(clientAppType);
            sendTestMail(recipent,
                    authresult.accessToken(),
                    userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static IAuthenticationResult acquireToken(String clientAppType) throws Exception {
    	IAuthenticationResult authresult = null;
    	switch(clientAppType) {
    	case "Desktop":
    		authresult = TestMail.acquireTokenPublic();
    		break;
    	case "Browserless":
    		authresult = TestMail.acquireTokenPublic();
    		break;
    	case "Mobile":
    		authresult = TestMail.acquireTokenPublic();
    		break;
    	case "Web":
    		authresult = TestMail.acquireTokenConfidential();
    		break;
    	case "WebAPI":
    		authresult = TestMail.acquireTokenConfidential();
    		break;
    	case "Daemon":
    		authresult = TestMail.acquireTokenConfidential();
    		break;
    	default:
    		break;
    	}
    	return authresult;
    }
    
    private static void sendTestMail(String tos, String accessToken,String userName) throws MessagingException {
        Properties props = new Properties();
        //props.put("mail.imap.ssl.enable", "true"); // required for Gmail
        props.put("mail.smtp.auth.xoauth2.disable","false");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth.mechanisms","XOAUTH2");
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.transport.protocol","smtp");
        props.put("mail.smtp.host","smtp.office365.com");
        props.put("mail.smtp.port", "587");
        String token = tokenForSmtp(userName,accessToken);
        props.put("mail.smtp.sasl.mechanisms.oauth2.oauthToken", token);
        props.put("mail.debug",true);

        Session session = Session.getInstance(props);
        try {
            Message m1 = testMessage(userName,session,tos);
            SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
            transport.connect("smtp.office365.com", userName,null);
            transport.issueCommand("AUTH XOAUTH2 " + token, 235);
            if (m1 != null)
                transport.sendMessage(m1, m1.getAllRecipients());

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
    private static String tokenForSmtp(String userName, String accessToken) {
        final String ctrlA=Character.toString((char) 1);

        final String coded= "user=" + userName + ctrlA+"auth=Bearer " + accessToken + ctrlA+ctrlA;
        return Base64.getEncoder().encodeToString(coded.getBytes());
        //base64("user=" + userName + "^Aauth=Bearer " + accessToken + "^A^A")
    }
    
    public static Message testMessage(String from, Session session, String tos) {
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            InternetAddress[] recipients = InternetAddress.parse(tos);
            message.setRecipients(Message.RecipientType.TO,
                    recipients);

            // Set Subject: header field
            message.setSubject("Example Office365 Auth");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("This is a test");
            
            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            
            message.setContent(multipart);
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static IAuthenticationResult acquireTokenPublic() throws Exception {

        // Load token cache from file and initialize token cache aspect. The token cache will have
        // dummy data, so the acquireTokenSilently call will fail.
        TokenCacheAspect tokenCacheAspect = new TokenCacheAspect("../resources/sample_cache.json");

        PublicClientApplication pca = PublicClientApplication.builder(clientId)
                .authority(authEndpoint)
                .setTokenCacheAccessAspect(tokenCacheAspect)
                .build();

        Set<IAccount> accountsInCache = pca.getAccounts().join();
        // Take first account in the cache. In a production application, you would filter
        // accountsInCache to get the right account for the user authenticating.
        IAccount account = accountsInCache.iterator().next();

        IAuthenticationResult authResult;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(SCOPE, account)
                            .build();

            // try to acquire token silently. This call will fail since the token cache
            // does not have any data for the user you are trying to acquire a token for
            authResult = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
            	switch(publicClientAcquireToken) {
            	case "Interactive":
            		authResult = getTokenInteractive(pca);
            		return authResult;
            	case "Silently":
            		authResult = getTokenSilently(pca);
            		return authResult;
            	default:
            		return null;
            	}
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }
        return authResult;
    }
    
    private static IAuthenticationResult getTokenInteractive(PublicClientApplication pca) throws URISyntaxException {
    	
        InteractiveRequestParameters parameters = InteractiveRequestParameters
                .builder(new URI(redirectURI))
                .scopes(SCOPE)
                .build();
        
        CompletableFuture<IAuthenticationResult> authResultFuture = pca.acquireToken(parameters);
        
        IAuthenticationResult authResult = authResultFuture.join();
    	
    	return authResult;
    }
    
    private static IAuthenticationResult getTokenSilently(PublicClientApplication pca) {
    	
        char[] MailPasswordArray = userPassword.toCharArray();
        
        UserNamePasswordParameters parameters = UserNamePasswordParameters.builder(SCOPE, userName, MailPasswordArray).build();
        
        
        CompletableFuture<IAuthenticationResult> authResultFuture = pca.acquireToken(parameters);
        
        IAuthenticationResult authResult = authResultFuture.join();
        
    	return authResult;
    }
    
    private static IAuthenticationResult acquireTokenConfidential() throws Exception {
      clientSecret = prop.getProperty("clientSecret", "").trim();
      
      ConfidentialClientApplication app = ConfidentialClientApplication
      .builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret)).authority(authEndpoint).build();


      IAuthenticationResult authResultTest;
      try {
    	  ClientCredentialParameters clientCredentialParam = ClientCredentialParameters
      	  .builder(SCOPE).build();
      			  
      	  authResultTest = app.acquireToken(clientCredentialParam).join();    
      	  
      	  return authResultTest;
      } catch (Exception ex) {
          throw ex;
      }
    }
}