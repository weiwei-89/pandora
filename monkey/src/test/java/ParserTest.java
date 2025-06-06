import org.edward.pandora.monkey.Lexer;
import org.edward.pandora.monkey.Parser;
import org.edward.pandora.monkey.model.Program;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ParserTest {
    @Test
    public void testLetStatements() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("let five = 5;;").append("\n");
        sb.append("let ten = 10;").append("\n");
//        sb.append("let add = fn(x, y) { x+y; };").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testReturnStatements() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("return 5;").append("\n");
//        sb.append("return true;").append("\n");
        sb.append("return student;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testIdentifierExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("5;").append("\n");
//        sb.append("false;").append("\n");
        sb.append("name;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }
}