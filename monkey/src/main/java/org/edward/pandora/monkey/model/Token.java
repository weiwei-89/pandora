package org.edward.pandora.monkey.model;

public class Token {
    private final String literal;
    private final Type type;

    public Token(String literal, Type type) {
        this.literal = literal;
        this.type = type;
    }

    public String getLiteral() {
        return literal;
    }
    public Type getType() {
        return type;
    }

    public enum Type {
        ILLEGAL("illegal"),
        EOF("eof"),

        IDENT("ident"),
        INT("int"),

        ASSIGN("="),
        PLUS("+"),
        MINUS("-"),
        ASTERISK("*"),
        SLASH("/"),
        LT("<"),
        GT(">"),
        BANG("!"),
        EQ("=="),
        NOT_EQ("!="),

        COMMA(","),
        SEMICOLON(";"),

        LPAREN("("),
        RPAREN(")"),
        LBRACE("{"),
        RBRACE("}"),

        FUNCTION("function"),
        LET("let"),
        TRUE("true"),
        FALSE("false"),
        IF("if"),
        ELSE("else"),
        RETURN("return");

        private final String code;

        Type(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Type get(String code) {
            for(Type type : Type.values()) {
                if(type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Keywords {
        FN("fn", Type.FUNCTION),
        LET("let", Type.LET),
        TRUE("true", Type.TRUE),
        FALSE("false", Type.FALSE),
        IF("if", Type.IF),
        ELSE("else", Type.ELSE),
        RETURN("return", Type.RETURN);

        private final String literal;
        private final Type type;

        Keywords(String literal, Type type) {
            this.literal = literal;
            this.type = type;
        }

        public String getLiteral() {
            return literal;
        }
        public Type getType() {
            return type;
        }

        public static Keywords get(String literal) {
            for(Keywords keyword : Keywords.values()) {
                if(keyword.getLiteral().equals(literal)) {
                    return keyword;
                }
            }
            return null;
        }
    }

    public static Type lookupIdent(String literal) {
        Keywords keyword = Keywords.get(literal);
        if(keyword == null) {
            return Type.IDENT;
        }
        return keyword.getType();
    }
}