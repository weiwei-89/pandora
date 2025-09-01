package org.edward.pandora.monkey;

import org.edward.pandora.monkey.exception.EndOfLineException;
import org.edward.pandora.monkey.model.Token;

public class Lexer {
    private final String input;

    public Lexer(String input) {
        this.input = input;
    }

    private int currentPosition = -1;
    private char currentCharacter;

    private void read() throws Exception {
        int nextPosition = this.currentPosition + 1;
        if(nextPosition >= this.input.length()) {
            throw new EndOfLineException();
        }
        this.currentPosition = nextPosition;
        this.currentCharacter = this.input.charAt(nextPosition);
    }

    private void skipBlank() throws Exception {
        while(true) {
            char nextCharacter = this.inspectNext();
            if(nextCharacter==' '
                    || nextCharacter=='\n'
                    || nextCharacter=='\r'
                    || nextCharacter=='\t') {
                this.read();
                continue;
            }
            break;
        }
    }

    private char inspectNext() {
        int nextPosition = this.currentPosition + 1;
        if(nextPosition >= this.input.length()) {
            return ' ';
        }
        return this.input.charAt(nextPosition);
    }

    private boolean isLetter(char c) {
        if(('a'<=c&&c<='z') || ('A'<=c&&c<='Z') || c=='_') {
            return true;
        }
        return false;
    }

    private boolean isDigit(char c) {
        if('0'<=c && c<='9') {
            return true;
        }
        return false;
    }

    private String readIdentifier() throws Exception {
        int currentPosition = this.currentPosition;
        while(this.isLetter(this.inspectNext())) {
            this.read();
        }
        return this.input.substring(currentPosition, this.currentPosition+1);
    }

    private String readNumber() throws Exception {
        int currentPosition = this.currentPosition;
        while(this.isDigit(this.inspectNext())) {
            this.read();
        }
        return this.input.substring(currentPosition, this.currentPosition+1);
    }

    public Token nextToken() throws Exception {
        Token token = null;
        try {
            this.skipBlank();
            this.read();
        } catch(EndOfLineException e) {
            token = new Token("", Token.Type.EOF);
            return token;
        }
        switch(this.currentCharacter) {
            case '=':
                if(this.inspectNext() == '=') {
                    this.read();
                    token = new Token("==", Token.Type.EQ);
                } else {
                    token = new Token(String.valueOf(this.currentCharacter), Token.Type.ASSIGN);
                }
                break;
            case '+':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.PLUS);
                break;
            case '-':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.MINUS);
                break;
            case '*':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.ASTERISK);
                break;
            case '/':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.SLASH);
                break;
            case '<':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.LT);
                break;
            case '>':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.GT);
                break;
            case '!':
                if(this.inspectNext() == '=') {
                    this.read();
                    token = new Token("!=", Token.Type.NOT_EQ);
                } else {
                    token = new Token(String.valueOf(this.currentCharacter), Token.Type.BANG);
                }
                break;
            case ',':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.COMMA);
                break;
            case ';':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.SEMICOLON);
                break;
            case '(':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.LPAREN);
                break;
            case ')':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.RPAREN);
                break;
            case '{':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.LBRACE);
                break;
            case '}':
                token = new Token(String.valueOf(this.currentCharacter), Token.Type.RBRACE);
                break;
            default:
                if(this.isLetter(this.currentCharacter)) {
                    String identifier = this.readIdentifier();
                    token = new Token(identifier, Token.lookupIdent(identifier));
                } else if(this.isDigit(this.currentCharacter)) {
                    String number = this.readNumber();
                    token = new Token(number, Token.Type.INT);
                } else {
                    token = new Token(String.valueOf(this.currentCharacter), Token.Type.ILLEGAL);
                }
        }
        return token;
    }
}