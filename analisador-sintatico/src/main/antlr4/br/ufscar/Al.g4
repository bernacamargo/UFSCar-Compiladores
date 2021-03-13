grammar Al;

ALGORITMO: 'algoritmo';
FIM_ALGORITMO: 'fim_algoritmo';
DECLARE: 'declare';
CONSTANTE: 'constante';
TIPOW: 'tipo';
LITERAL: 'literal';
INTEIRO: 'inteiro';
REAL: 'real';
LOGICO: 'logico';
VERDADEIRO: 'verdadeiro';
FALSO: 'falso';
REGISTROW: 'registro';
FIM_REGISTRO: 'fim_registro';
PROCEDIMENTO: 'procedimento';
FIM_PROCEDIMENTO: 'fim_procedimento';
FUNCAO: 'funcao';
FIM_FUNCAO: 'fim_funcao';
VAR: 'var';
LEIA: 'leia';
ESCREVA: 'escreva';
SE: 'se';
ENTAO: 'entao';
SENAO: 'senao';
FIM_SE: 'fim_se';
CASO: 'caso';
SEJA: 'seja';
FIM_CASO: 'fim_caso';
PARA: 'para';
SETA: '<-';
ATE: 'ate';
FACA: 'faca';
FIM_PARA: 'fim_para';
ENQUANTO: 'enquanto';
FIM_ENQUANTO: 'fim_enquanto';
RETORNE: 'retorne';
NAO: 'nao';
PONTOS:  '..';
DOIS_PONTOS: ':';
OP_MAIS: '+';
OP_MENOS: '-';
OP_MUL: '*';
OP_DIV: '/';
OP_PORCENTAGEM: '%';
OP_E_COMERCIAL: '&';
OP_OU: 'ou';
OP_E: 'e';
OP_MAIOR: '>';
OP_MENOR: '<';
OP_MAIOR_IGUAL: '>=';
OP_MENOR_IGUAL: '<=';
OP_DIFERENTE: '<>';
OP_IGUAL: '=';
ABRE_PARENTESE: '(';
FECHA_PARENTESE: ')';
VIRGULA: ',';
PONTO: '.';
ABRE_COLCHETE: '[';
FECHA_COLCHETE: ']';
ESTENDIDO: '^';

IDENT: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

ESC_SEQ: '\\"';

CADEIA: '"' (ESC_SEQ | ~('"' | '\n') )* '"';

CADEIA_NAO_FECHADA: '"' (ESC_SEQ | ~('"') )* '\n';

COMENTARIO: '{' ~('\n' | '\r' | '}')* '}' -> skip; 

COMENTARIO_NAO_FECHADO: '{' ~('\n' | '\r' | '}')* '\n'; 

NUM_INT: ('0'..'9')+;

NUM_REAL: ('0'..'9')+ ('.' ('0'..'9')+)?;

WS: ( ' ' | '\t' | '\r' | '\n') {skip();};

UNDEFINED_CHAR: .;  

// Analis. Sintático 

progr: declarations ALGORITMO body FIM_ALGORITMO EOF | ALGORITMO body FIM_ALGORITMO EOF;

declarations: local_decl_global*;

local_decl_global: local_declaration | global_declaration;

local_declaration: DECLARE variable |
    CONSTANTE IDENT DOIS_PONTOS basic_type OP_IGUAL const_value  |
    TIPOW IDENT DOIS_PONTOS type ;

variable: identifier ( VIRGULA identifier)* DOIS_PONTOS type;

identifier: IDENT (PONTO IDENT)* dimension ;

dimension: (ABRE_COLCHETE arithmetic_exp FECHA_COLCHETE)* ;

type: register | extend_type ;

basic_type: LITERAL | INTEIRO | REAL | LOGICO ;

basic_type_ident: basic_type | IDENT ;

extend_type: ESTENDIDO? basic_type_ident ;

const_value : CADEIA | NUM_INT | NUM_REAL | VERDADEIRO | FALSO ;

register: REGISTROW variable* FIM_REGISTRO ;

global_declaration: PROCEDIMENTO IDENT ABRE_PARENTESE parameters? FECHA_PARENTESE local_declaration* cmd* FIM_PROCEDIMENTO
                  | FUNCAO IDENT ABRE_PARENTESE parameters? FECHA_PARENTESE DOIS_PONTOS extend_type local_declaration*
                   cmd* FIM_FUNCAO;

parameter: VAR? identifier (VIRGULA identifier)* DOIS_PONTOS extend_type;

parameters: parameter (VIRGULA parameter)*;

body: local_declaration* cmd*;

cmd: cmd_read | cmd_write| cmd_if | cmd_case | cmd_for | cmd_while |
     cmd_do | cmd_assignment | cmd_call | cmd_return;

cmd_read: LEIA ABRE_PARENTESE ESTENDIDO? identifier (VIRGULA ESTENDIDO? identifier)* FECHA_PARENTESE;

cmd_write: ESCREVA ABRE_PARENTESE expression (VIRGULA expression)* FECHA_PARENTESE;

cmd_if: SE expression ENTAO cmd* (SENAO cmd*)? FIM_SE;

cmd_case: CASO arithmetic_exp SEJA selection (SENAO cmd)? FIM_CASO;

cmd_for: PARA IDENT SETA arithmetic_exp ATE arithmetic_exp FACA cmd* FIM_PARA;

cmd_while: ENQUANTO expression FACA cmd* FIM_ENQUANTO;

cmd_do: FACA cmd* ATE expression;

cmd_assignment: ESTENDIDO? identifier SETA expression;

cmd_call: IDENT ABRE_PARENTESE expression (VIRGULA expression)* FECHA_PARENTESE;

cmd_return: RETORNE expression;

selection: selection_item*;

selection_item: constant DOIS_PONTOS cmd*;

constant: interval_number (VIRGULA interval_number)*;

interval_number: single_operator? NUM_INT (PONTOS single_operator? NUM_INT)?;

single_operator: OP_MENOS;

arithmetic_exp: term (op1 term)*;

term: factor (op2 factor)*;

factor: parcel (op3 parcel)*;

op1: OP_MAIS | OP_MENOS;

op2: OP_MUL | OP_DIV;

op3: OP_PORCENTAGEM;
non_unary_portion: OP_E_COMERCIAL identifier | CADEIA;

relational_exp: arithmetic_exp (relational_op arithmetic_exp)?;

relational_op: OP_IGUAL | OP_DIFERENTE | OP_MAIOR_IGUAL | OP_MENOR_IGUAL | OP_MAIOR | OP_MENOR;

expression: logical_term (logical_op_1 logical_term)*;

logical_term: logical_factor (logical_op_2 logical_factor)*;

logical_factor: NAO? logical_plot;

logical_plot: ( VERDADEIRO | FALSO ) | relational_exp;

logical_op_1: OP_OU;

logical_op_2: OP_E;

parcel: single_operator? single_parcel | non_unary_portion;

single_parcel: ESTENDIDO? identifier |
                IDENT ABRE_PARENTESE expression (VIRGULA expression)* FECHA_PARENTESE |
                NUM_INT |
                NUM_REAL |
                ABRE_PARENTESE expression FECHA_PARENTESE;

    