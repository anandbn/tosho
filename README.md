SalesforceCanvasFrameworkSDK
============================

Salesforce Canvas is a mashup framework for consuming third party applications within Salesforce. Its goal is to connect applications at a UI level instead of an API level. Platform Connect will provide third party applications with a JavaScript SDK so they can seamlessly integrate canvas style applications, while developing in the technology and platform of their choice. 

### How to Clone the Git Repository

	git clone git@github.com:forcedotcom/SalesforceCanvasFrameworkSDK.git

### How to Build Canvas locally

    mvn package
    
### First time keystore generation (for local SSL support)

      > keytool -keystore keystore -alias jetty -genkey -keyalg RSA
      Enter keystore password: 123456
      Re-enter new password: 123456
      What is your first and last name?
        [Unknown]:  salesforce.com
      What is the name of your organizational unit?
        [Unknown]:  platform
      What is the name of your organization?
        [Unknown]:  chimera   
      What is the name of your City or Locality?
        [Unknown]:  San Fancisco
      What is the name of your State or Province?
        [Unknown]:  CA
      What is the two-letter country code for this unit?
        [Unknown]:  us
      Is CN=salesforce.com, OU=platform, O=chimera, L=San Fancisco, ST=CA, C=us correct?
        [no]:  yes

      Enter key password for <jetty>
	(RETURN if same as keystore password):  
      Re-enter new password: 


### How to Run Canvas locally

    sh target/bin/webapp

### How to invoke connect locally

    https:/localhost:8443
    
### Canvas Demo URLs

    https://platform-canvas-dev.herokuapp.com/examples/hello-world/index.jsp
    
### Canvas Callback URLs
    
    https://platform-canvas-dev.herokuapp.com/sdk/callback.html


### How to push new changes to heroku

      git add -A
      git commit -m "My change comments"
      git push heroku master

### How to push new changes to github

      git push origin master

### How to get Heroku logs
      
      heroku logs --tail



