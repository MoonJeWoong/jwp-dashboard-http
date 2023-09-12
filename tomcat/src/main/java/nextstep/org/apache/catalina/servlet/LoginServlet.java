package nextstep.org.apache.catalina.servlet;

import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.model.User;
import nextstep.org.apache.catalina.cookie.Cookies;
import nextstep.org.apache.coyote.http11.HttpHeader;
import nextstep.org.apache.coyote.http11.request.Http11Request;
import nextstep.org.apache.coyote.http11.response.Http11Response;
import nextstep.org.apache.catalina.session.Session;
import nextstep.org.apache.catalina.session.SessionManager;
import nextstep.org.apache.coyote.http11.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    private static final SessionManager sessionManager = new SessionManager();

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        Cookies cookies = request.getCookies();
        if (cookies.hasCookie("JSESSIONID")) {
            response.setStatus(Status.FOUND)
                    .setHeader(HttpHeader.LOCATION, "/index.html");
            return;
        }
        responseWithBody(request, response);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        Cookies cookies = request.getCookies();
        try {
            Session loginSession = login(
                    request.getParsedBodyValue("account"),
                    request.getParsedBodyValue("password")
            );
            cookies.set("JSESSIONID", loginSession.getId());

            response.setStatus(Status.FOUND)
                    .setHeader(HttpHeader.LOCATION, "/index.html")
                    .setCookies(cookies);
        } catch (NoSuchElementException | InvalidLoginInfoException | NullPointerException e) {
            response.setStatus(Status.FOUND)
                    .setHeader(HttpHeader.LOCATION, "/401.html")
                    .setCookies(cookies);
        }
    }

    private Session login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (!user.checkPassword(password)) {
            throw new InvalidLoginInfoException();
        }
        log.info(user.toString());
        return createSession(user);
    }

    private Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(user.getAccount(), user);
        sessionManager.add(session);
        return session;
    }
}
