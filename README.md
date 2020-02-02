# Simple cross-session token

This example is generally not about cookies. This example shows how to create, persist, and manage a cross session.

We need to work with several browsers, because the user can log in through a web browser and continue working on a mobile phone.

And additionally we need to persist IP address too - for security.

## Run application
Java 1.8 

```
mvn clean install
java -jar target/cross-session-cookies-1-SNAPSHOT.jar
```

## Session
First, lets create our session:
```java
@Entity
@NoArgsConstructor
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Session(String userAgent, String ip, String token){

        this.userAgent = userAgent;
        this.ip = ip;
        this.token = token;
    }

    private String userAgent;
    private String ip;
    private String token;
}
```
This entity have ip, user-agent and generated token, which user provides in each query

## Controllers

Getting session token and persisting:
```java
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthApi {

    private final SessionRepo sessionRepo;  // JpaRepository for Session entity

    public AuthApi(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @GetMapping
    public String getSessionCookie(
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        log.info("New request received");
        log.info("User-Agent: " + userAgent);
        String ip = RequestUtil.getClientIpAddr(request);
        log.info("User IP Address: " + ip);
        Session session = new Session(userAgent, ip, TokenCodeGenerator.generateToken());
        sessionRepo.save(session);

        return session.getToken();
    }
}
```

Here is utility class for getting IP Address from `HttpServletRequest`:
```java
public class RequestUtil {
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

Why he is so long? Because we don't know which of this headers will provide client's browser. Then, we need to check them all

Now, we can create secured endpoint with session as required param (you can put this session in cookie):
```java
@RestController
@RequestMapping("/user")
public class UserApi {
    private final SessionRepo sessionRepo;

    public UserApi(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @GetMapping
    public String getUser(
            @RequestParam("session") String token,
            HttpServletRequest request,
            @RequestHeader("User-Agent") String userAgent
    ){
        Optional<Session> sessionOptional;
        if ((sessionOptional = sessionRepo.findByToken(token)).isPresent()){

            Session session = sessionOptional.get();

            // I think it is good way to insert IP address check last
            if (session.getUserAgent().equals(userAgent) && session.getIp().equals(RequestUtil.getClientIpAddr(request))){
                return "Success";
            } else {
                return "You use another IP or another browser. Please re-auth.";
            }
        } else {
            return "Fail";
        }
    }
}
```

After running application you can check results from diffrent browsers

For example, go to page "localhost:8080/auth" -> copy session and go to page "localhost:8080/user?session=<YOUR_TOKEN>"

You get "Success"

And then check it from cURL:
```
curl -X GET "localhost:8080/user?session=Tp9e-DwvG-5qkN-TuTV"
```

And you will get "You use another IP or another browser. Please re-auth."
  
