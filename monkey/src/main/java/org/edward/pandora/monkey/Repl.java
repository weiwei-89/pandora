package org.edward.pandora.monkey;

import org.edward.pandora.monkey.model.Token;

import java.util.Scanner;

public class Repl {
    public static void main(String[] args) throws Exception {
        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                System.out.println("Please enter a command......");
                String input = scanner.nextLine();
                if("exit".equals(input)) {
                    break;
                }
                Lexer lexer = new Lexer(input);
                while(true) {
                    Token token = lexer.nextToken();
                    if(token.getType() == Token.Type.ILLEGAL) {
                        System.out.println(String.format("invalid token: %s", token.getLiteral()));
                        break;
                    }
                    if(token.getType() == Token.Type.EOF) {
                        break;
                    }
                    System.out.println(String.format("token: %s", token.getLiteral()));
                }
            }
        }
    }
}