import org.edward.pandora.monkey.Lexer;
import org.edward.pandora.monkey.Parser;
import org.edward.pandora.monkey.model.Element;
import org.edward.pandora.monkey.model.Evaluator;
import org.edward.pandora.monkey.model.Program;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EvaluatorTest {
    @Test
    public void testEvalIntegerExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("5").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        Element result = evaluator.eval(program);
        System.out.println(result.inspect());
    }

    @Test
    public void testEvalBooleanExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("true").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        Element result = evaluator.eval(program);
        System.out.println(result.inspect());
    }

    @Test
    public void testBangOperator() throws Exception {
        StringBuilder sb = new StringBuilder();
//        sb.append("!true").append("\n");
//        sb.append("!10").append("\n");
//        sb.append("!name").append("\n");
//        sb.append("-10").append("\n");
//        sb.append("-true").append("\n");
//        sb.append("!-10").append("\n");
        sb.append("!-false").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        Element result = evaluator.eval(program);
        System.out.println(result.inspect());
    }
}