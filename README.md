=========

## Login and Registration Example Project with Spring Security
If you're already a student of Learn Spring Security, you can get started diving deeper into registration with Module 2 </br>
If you're not yet a student, you can get access to the course here: http://bit.ly/github-lss
</br></br></br>


### Relevant Articles: 
- [Spring Security Registration Tutorial](http://www.baeldung.com/spring-security-registration)
- [The Registration Process With Spring Security](http://www.baeldung.com/registration-with-spring-mvc-and-spring-security)
- [Registration – Activate a New Account by Email](http://www.baeldung.com/registration-verify-user-by-email)
- [Registration with Spring Security – Password Encoding](http://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)
- [Spring Security – Roles and Privileges](http://www.baeldung.com/role-and-privilege-for-spring-security-registration)
- [Prevent Brute Force Authentication Attempts with Spring Security](http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts)
- [Spring Security – Reset Your Password](http://www.baeldung.com/spring-security-registration-i-forgot-my-password)
- [Spring Security Registration – Resend Verification Email](http://www.baeldung.com/spring-security-registration-verification-email)
- [The Registration API becomes RESTful](http://www.baeldung.com/registration-restful-api)
- [Registration – Password Strength and Rules](http://www.baeldung.com/registration-password-strength-and-rules)
- [Updating your Password](http://www.baeldung.com/updating-your-password/)
- [Registration - Integrate reCAPTCHA](http://www.baeldung.com/spring-security-registration-captcha/)
- [Two Factor Auth with Spring Security](http://www.baeldung.com/spring-security-two-factor-authentication-with-soft-token)
- [Registration with Spring – Integrate reCAPTCHA](http://www.baeldung.com/spring-security-registration-captcha)
- [Purging Expired Tokens Generated By The Registration](http://www.baeldung.com/registration-token-cleanup)
- [Custom Login Page for Returning User](http://www.baeldung.com/custom-login-page-for-returning-user)
- [Allow Authentication from Accepted Locations Only with Spring Security](http://www.baeldung.com/spring-security-restrict-authentication-by-geography)
- [Spring Security – Auto Login User After Registration](http://www.baeldung.com/spring-security-auto-login-user-after-registration)
- [Keep Track of Logged In Users with Spring Security](http://www.baeldung.com/spring-security-track-logged-in-users)
- [Login For a Spring Web App – Error Handling and Localization](http://www.baeldung.com/spring-security-login-error-handling-localization)
- [Notify User of Login From New Device or Location](https://www.baeldung.com/spring-security-login-new-device-location)
- [Preventing Username Enumeration Attacks with Spring Security](https://www.baeldung.com/spring-security-enumeration-attacks)

### Build and Deploy the Project
```
mvn clean install
```

This is a Spring Boot project, so you can deploy it by simply using the main class: `Application.java`



### Set up MySQL
```
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```


### Set up Email

You need to configure the email by providing your own username and password in application.properties
You also need to use your own host, you can use Amazon or Google for example.
You may also setup an email server locally.  See "email.properties.localhost.sample" for more details.

### AuthenticationSuccessHandler configuration for Custom Login Page article
If you want to activate the configuration for the article [Custom Login Page for Returning User](http://www.baeldung.com/custom-login-page-for-returning-user), then you need to comment the @Component("myAuthenticationSuccessHandler") annotation in the MySimpleUrlAuthenticationSuccessHandler and uncomment the same in MyCustomLoginAuthenticationSuccessHandler.
#   G e s t i o n - D - a v o c a t  
 #   G e s t i o n - D - a v o c a t  
 #   G e s t i o n - D - a v o c a t  
 #   G e s t i o n - D - a v o c a t  
 #   G e s t i o n - D - a v o c a t  
 #   G e s t i o n - D - a v o c a t  
 #   K H A L L A B I - B I L A L  
 