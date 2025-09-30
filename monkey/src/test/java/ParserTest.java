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
//        sb.append("let five = 5;;").append("\n");
//        sb.append("let ten = 10;").append("\n");
//        sb.append("let add = fn(x, y) { x+y; };").append("\n");
        sb.append("let add = fn(x) { fn(y){x+y;} };").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testReturnStatements() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("return 5;").append("\n");
        sb.append("return true;").append("\n");
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
        sb.append("false;").append("\n");
        sb.append("name;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testParsingPrefixExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("!5;").append("\n");
        sb.append("-15;").append("\n");
        sb.append("!false;").append("\n");
        sb.append("!true;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testParsingInfixExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("5 + 5").append("\n");
        sb.append("5 - 5").append("\n");
        sb.append("5 * 5").append("\n");
        sb.append("5 / 5").append("\n");
        sb.append("5 > 5").append("\n");
        sb.append("5 < 5").append("\n");
        sb.append("5 == 5").append("\n");
        sb.append("5 != 5").append("\n");
        sb.append("true == true").append("\n");
        sb.append("true != false").append("\n");
        sb.append("false == false").append("\n");
        sb.append("9 + 2 + 5").append("\n");
        sb.append("3 > 5 == false").append("\n");
        sb.append("5+2; 7+9;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testOperatorPriorityParsing() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("5 + 2 * 9;").append("\n");
        sb.append("5 + (3 + 7);").append("\n");
        sb.append("(5 + 7) * 2;").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testIfExpression() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("if(x < y) { x }").append("\n");
        sb.append("if(true) { 5+(2-6); 7*(9+3); }").append("\n");
        sb.append("if(x < y) { x } else { y }").append("\n");
        sb.append("if(true) { 5+(2-6); 7*(9+3); } else { 6-9; 3+9/2;}").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testFunctionLiteralParsing() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("fn() { 6; a+b; }").append("\n");
        sb.append("fn(x) { x; a+b; }").append("\n");
        sb.append("fn(,,x,) { x; a+b; }").append("\n");
        sb.append("fn(x, y) { 5+(2-6); 7*(9+3); }").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }

    @Test
    public void testCallExpressionParsing() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("add()").append("\n");
        sb.append("add(1, 2)").append("\n");
        sb.append("add(1, 2*3, 4+5)").append("\n");
        Lexer lexer = new Lexer(sb.toString());
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        System.out.println(program.string());
    }
}