package nextstep.org.apache.coyote.http11;

import support.StubSocket;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        File file = new File(resource.getPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
//                "Content-Length: 5564 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        File file = new File(resource.getPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(resource.getPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        File file = new File(resource.getPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                String.format("Content-Length: %d \r\n", file.length()) +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccessTest() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginFailTest() {
        // given
        final String requestBody = "account=gugu&password=wrongPassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerSuccessTest() {
        // given
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                String.format("Content-Length: %d ", requestBody.getBytes().length),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
