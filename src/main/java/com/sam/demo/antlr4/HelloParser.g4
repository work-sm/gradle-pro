parser grammar HelloParser;

options {
	tokenVocab=HelloLexer;
}

call : main;

// 语法分析器的规则必须以小写字母开头
main : K_START block? K_END LN;

block : line* return;

//以 “:” 开始， “;” 结束， 多规则以 "|" 分隔
line : ID ASSIGN expr LN
    | ID ADDS LN
    | ID SUBS LN
    ;

return : K_RETURN (expr|ID)* LN;


expr : LEFT expr RIGHT
    | expr (MUL|DIV) expr
    | expr (ADD|SUB) expr
    | NUMERIC
    | INTEGER
    ;