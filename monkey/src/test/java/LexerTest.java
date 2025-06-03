import org.edward.pandora.monkey.model.Lexer;
import org.edward.pandora.monkey.model.Token;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LexerTest {
    @Test
    public void testNextToken() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("let five = 5;").append("\n");
        sb.append("let ten = 10;").append("\n");
        sb.append("let add = fn(x, y) { x+y; };").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        while(true) {
            Token token = lexer.nextToken();
            if(token.getType() == Token.Type.ILLEGAL) {
                break;
            }
            if(token.getType() == Token.Type.EOF) {
                break;
            }
            System.out.println(String.format("token: %s", token.getLiteral()));
        }
    }
}