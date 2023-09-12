package nextstep.org.apache.catalina.servlet;

import static nextstep.org.apache.coyote.http11.HttpUtil.selectFirstContentTypeOrDefault;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import nextstep.org.apache.coyote.http11.HttpHeader;
import nextstep.org.apache.coyote.http11.request.Http11Request;
import nextstep.org.apache.coyote.http11.response.Http11Response;
import nextstep.org.apache.coyote.http11.Status;

public abstract class AbstractServlet implements Servlet{

    private static final String RESOURCES_PATH_PREFIX = "static";
    protected static final String NOT_FOUND_DEFAULT_MESSAGE = "404 Not Found";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String HTTP_GET_METHOD = "GET";
    private static final String HTTP_POST_METHOD = "POST";

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        String method = request.getMethod();

        if (method.equals(HTTP_GET_METHOD)) {
            doGet(request, response);
        } else if (method.equals(HTTP_POST_METHOD)) {
            doPost(request, response);
        } else {
            response.setStatus(Status.NOT_IMPLEMENTED);
        }
    }

    protected abstract void doGet(Http11Request request, Http11Response response) throws  Exception;

    protected abstract void doPost(Http11Request request, Http11Response response) throws Exception;

    protected Optional<String> createResponseBody(String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return Optional.of("Hello world!");
        }

        String resourceName = RESOURCES_PATH_PREFIX + requestPath;
        if (!resourceName.contains(".")) {
            resourceName += DEFAULT_EXTENSION;
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    protected void responseWithBody(Http11Request request, Http11Response response) throws IOException {
        Optional<String> responseBody = createResponseBody(request.getPathInfo());
        String contentType = selectFirstContentTypeOrDefault(request.getHeader(HttpHeader.ACCEPT));

        if (responseBody.isEmpty()) {
            responseWithNotFound(request, response);
            return;
        }

        response.setStatus(Status.OK)
                .setHeader(HttpHeader.CONTENT_TYPE, contentType + CHARSET_UTF_8)
                .setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(
                        responseBody.get().getBytes(StandardCharsets.UTF_8).length))
                .setBody(responseBody.get());
    }

    private void responseWithNotFound(Http11Request request, Http11Response response) throws IOException {
        String notFoundPageBody = createResponseBody(NOT_FOUND_PAGE)
                .orElse(NOT_FOUND_DEFAULT_MESSAGE);
        String contentType = selectFirstContentTypeOrDefault(request.getHeader(HttpHeader.ACCEPT));

        response.setStatus(Status.NOT_FOUND)
                .setHeader(HttpHeader.CONTENT_TYPE, contentType + CHARSET_UTF_8)
                .setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(
                        notFoundPageBody.getBytes(StandardCharsets.UTF_8).length))
                .setBody(notFoundPageBody);
    }
}
