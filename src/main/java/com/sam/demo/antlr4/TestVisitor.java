//package com.sam.demo.antlr4;
//
//import com.sam.demo.antlr4.gen.HelloParser;
//import com.sam.demo.antlr4.gen.HelloParserBaseVisitor;
//
//public class TestVisitor extends HelloParserBaseVisitor<String> {
//    @Override
//    public String visitLine(HelloParser.LineContext ctx) {
//        System.out.println(ctx);
//        return super.visitLine(ctx);
//    }
//
//    @Override
//    public String visitRetrun(HelloParser.RetrunContext ctx) {
//        System.out.println(ctx);
//        return super.visitRetrun(ctx);
//    }
//
//    @Override
//    public String visitExpr(HelloParser.ExprContext ctx) {
//        System.out.println(ctx);
//        return super.visitExpr(ctx);
//    }
//}
