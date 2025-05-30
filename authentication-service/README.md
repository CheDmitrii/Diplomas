# Authentication service
## Use for authentication user get/refresh JWT and manage user data

## Get JWT guide

### Authentication takes place in several steps:

### 1. <ins>Get Code</ins>
    HTTP GET http:\//localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http:\//localhost:9000/oauth2/callback
##### Where:
- **response_type** is type how do you want authenticate;
- **client_id** is unique client application id
- **scope** is a set of permissions or privileges that the client wants to obtain during authentication and authorization (openid - request for authenticate user, offline_access - request for refresh token)
- **redirect_uri** is the URL (address) to which the OAuth 2.0 authorization server redirects the user after successful authentication and issuance of the authorization code or access token.

### 2. <ins>Enter data</ins>
#### After you will redirecting on login page that you configure in security config. Enter login, password and sen request

### 3. <ins>Get token</ins>
#### After success you will redirecting on callback uri and in uri will param with code name
    HTTP POST http://localhost:9000/oauth2/token
    Content-type: application/x-www-form-urlencoded
    body {
        grant_type: authorization_code,
        redirect_uri: http://localhost:9000/oauth2/callback,
        code: <code>
    }
    header: {
        Authorization: Basic BASE64_ENCODED(client_id:client_secret) 
    }
##### Where:
- **grant_type** is type how you will get token;
- **redirect_uri** is redirect uri that you write on 1 step;
- **code** is code that you get from param code of redirect url;
- **client_id** and **client_secret** is configured in security

### 4. <ins>Refresh token</ins>
#### If need refresh token then send next request.
    HTTP POST http://localhost:9000/oauth2/token
    body {
        grant_type: refresh_token,
        refresh_token: <refresh_token>
    }
##### Where:
- **grant_type** is type of refresh token;
- **refresh_token** is token that allow you get new token when old is expired. That token get in answer on step 3;