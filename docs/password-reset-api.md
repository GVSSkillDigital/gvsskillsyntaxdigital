# Password reset API

Apply `src/main/resources/db/password_reset_history.sql` to the application's
MySQL database before starting the backend. Hibernate uses
`spring.jpa.hibernate.ddl-auto=validate`, so it will not create this table
automatically.

Both endpoints are public and do not require a JWT. Password reset requests
return the same response whether or not the email is registered.

## Check whether an email is registered

`POST /api/auth/verify-email-id` accepts the email as plain-text request body
and returns a JSON boolean. This endpoint intentionally reveals whether an
account exists.

```http
POST /api/auth/verify-email-id
Content-Type: text/plain

student@example.com
```

The response body is `true` when the email is registered and `false` otherwise.

## Request a reset code

`POST /api/auth/passwordreset`

```json
{
  "email": "student@example.com",
  "password": "new-password",
  "confirmPassword": "new-password"
}
```

The passwords must match and follow the registration password length rules.
For registered accounts, an email with a six-digit code is sent. The code
expires after three minutes. Codes and pending passwords are stored as BCrypt
hashes; raw passwords and codes are not stored.

```json
{
  "message": "If an account exists for that email, a verification code will be sent."
}
```

Requests are limited to three per email and ten per IP address in a rolling
15-minute window.

## Confirm the code

`POST /api/auth/confirmcode`

```json
{
  "email": "student@example.com",
  "code": "012345"
}
```

A valid code updates the password and invalidates the account's refresh
sessions. Codes can be used once; five incorrect attempts expire the code.
Success returns a message indicating that the user can log in. Invalid, used,
and expired codes return an error.
