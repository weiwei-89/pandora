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
//        sb.append("5").append("\n");
//        sb.append("5+7").append("\n");
//        sb.append("5-9").append("\n");
//        sb.append("-5+13").append("\n");
//        sb.append("-5++19").append("\n");
//        sb.append("-5+(+19)").append("\n");
//        sb.append("9>5").append("\n");
//        sb.append("3<9").append("\n");
//        sb.append("3+6>9+15").append("\n");
//        sb.append("5+2==1+6").append("\n");
        sb.append("9+3!=10+2").append("\n");
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
//        sb.append("true").append("\n");
//        sb.append("false").append("\n");
//        sb.append("true==false").append("\n");
//        sb.append("true!=false").append("\n");
//        sb.append("(5>2)==true").append("\n");
//        sb.append("(6>15)!=(2<5)").append("\n");
        sb.append("(9-5)==(2<5)").append("\n");
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
//        sb.append("-10").append("\n");
//        sb.append("+10").append("\n");
//        sb.append("-true").append("\n");
        sb.append("+false").append("\n");
//        sb.append("!true").append("\n");
//        sb.append("!10").append("\n");
//        sb.append("!name").append("\n");
//        sb.append("!-10").append("\n");
//        sb.append("!-false").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        Element result = evaluator.eval(program);
        System.out.println(result.inspect());
    }
}