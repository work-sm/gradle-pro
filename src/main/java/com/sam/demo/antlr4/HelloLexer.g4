lexer grammar HelloLexer;

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

// 词法分析器的规则必须以大写字母开头
fragment ALPHA : [a-zA-Z];
fragment DIGIT : [0-9];

COMMENT : '//'.*? ->skip;
COMMENTS : ('/**'|'/*').*?'*/' ->skip;
SK : [\t\r\n ]+ ->skip;

LN : ';';
DOT : '.';
MUL : '*';
DIV : '/';
ADD : '+';
ADDS : '++';
SUB : '-';
SUBS : '--';
MOD : '%';
ASSIGN : '=';
EQ : '==';
NOT_EQ : '!=';
LT : '<';
LT_EQ : '<=';
GT : '>';
GT_EQ : '>=';
LEFT : '(';
RIGHT : ')';

K_START : S T A R T;
K_END : E N D;
K_RETURN : R E T U R N;
K_IF : I F;
K_ELSEIF : E L S E I F;
K_ENDIF : E N D I F;

// 比较泛的最后定义
INTEGER : DIGIT+;
NUMERIC : INTEGER DOT DIGIT+;
ID : ALPHA+;
