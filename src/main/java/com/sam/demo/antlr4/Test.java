//package com.sam.demo.antlr4;
//
//import com.sam.demo.antlr4.gen.*;
//import org.antlr.v4.runtime.CharStreams;
//import org.antlr.v4.runtime.CodePointCharStream;
//import org.antlr.v4.runtime.CommonTokenStream;
//import org.antlr.v4.runtime.tree.ParseTreeWalker;
//
//import java.io.IOException;
//
//public class Test {
//
//    public static void main(String[] args) throws IOException {
//        CodePointCharStream charStream = CharStreams.fromString("START i=11; return i; end;");
//
//        HelloLexer lexer = new HelloLexer(charStream);
//
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//        HelloParser parser = new HelloParser(tokens);
//
//        HelloParser.MainContext r = parser.main();
//
//        HelloParserVisitor eval = new TestVisitor();
//        eval.visit(r);
//
//        ParseTreeWalker walker = new ParseTreeWalker();
//        walker.walk(new HelloParserBaseListener(){
//        }, r);
//    }
//
//}
