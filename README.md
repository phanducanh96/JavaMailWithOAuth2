# Example of Email that implements Microsoft OAuth (Java)
## Why:
Purpose of this GitHub Repo: I had a hard time finding example and tutorial for the implementation; so I figured, I'll make one instead :)<br/>
Since Basic Authentication is getting deprecated in Exchange Online (Reference: [Deprecation of Basic authentication in Exchange Online](https://learn.microsoft.com/en-us/exchange/clients-and-mobile-in-exchange-online/deprecation-of-basic-authentication-exchange-online)), Microsoft suggested to adopt modern authentication (OAuth 2.0 token-based authorization).
## How:
**Step 1**: Create an Azure Application (Refer to: [Authenticate an IMAP, POP or SMTP connection using OAuth](https://learn.microsoft.com/en-us/exchange/client-developer/legacy-protocols/how-to-authenticate-an-imap-pop-smtp-application-by-using-oauth#register-your-application) or README Section in [Azure-Samples/ms-identity-msal-java-samples/Client-Side Scenarios/Device-Code-Flow](https://github.com/Azure-Samples/ms-identity-msal-java-samples/tree/main/2.%20Client-Side%20Scenarios/Device-Code-Flow) )<br/>
**Step 2**: Download needed libraries and dependencies (For Java).<br/>
1. [msal4j](https://mvnrepository.com/artifact/com.microsoft.azure/msal4j)
2. [json-smart](https://mvnrepository.com/artifact/net.minidev/json-smart)
3. [oauth2-oidc-sdk](https://jar-download.com/artifacts/com.nimbusds/oauth2-oidc-sdk)
4. [jackson-core](https://jar-download.com/artifacts/com.fasterxml.jackson.core) <br/>

**Step 3**: Go through the code (This code is for Client Credentials Grant)
## References and further reads:
[Azure-Samples/ms-identity-msal-java-samples/Client-Side Scenarios/Device-Code-Flow](https://github.com/Azure-Samples/ms-identity-msal-java-samples/tree/main/2.%20Client-Side%20Scenarios/Device-Code-Flow)<br/>
[vesa-mailtest](https://github.com/eino-makitalo/vesa-mailtest/)<br/>
[Microsoft identity platform code samples](https://learn.microsoft.com/en-us/azure/active-directory/develop/sample-v2-code)<br/>
[OpenID Connect on the Microsoft identity platform](https://learn.microsoft.com/en-us/azure/active-directory/develop/v2-protocols-oidc#fetch-the-openid-connect-metadata-document)<br/>
[Public client and confidential client applications](https://learn.microsoft.com/en-us/azure/active-directory/develop/msal-client-applications)
