package br.ufscar;

import static br.ufscar.TipoAl.REAL;
import static br.ufscar.GeradorUtils.verificaTipoVarParam;

public class GeradorCodigoC extends AlBaseVisitor<Void>{
    StringBuilder saida;
    TabelaDeSimbolos tabela;

    public GeradorCodigoC() {
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos();
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitProgr(AlParser.ProgrContext ctx) {
        // Cria a estrutura do arquivo em C e inicia a visitação aos filhos
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n");
        saida.append("\n");
            visitDeclarations(ctx.declarations());
        saida.append("int main() {\n");
            visitBody(ctx.body());
        saida.append("return 0;\n}\n");
        System.out.println(tabela.toString()); // imprime a tabela de simbolos
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitBody(AlParser.BodyContext ctx) {
        // Visita as declarações locais
        ctx.local_declaration().forEach(ld -> visitLocal_declaration(ld));

        // Visita os comandos
        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitDeclarations(AlParser.DeclarationsContext ctx) {
        if(ctx != null){
            //Visita as declaraçõs de variáveis locais
            ctx.local_decl_global().forEach(ldg -> {
                visitLocal_declaration(ldg.local_declaration());
            });
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitLocal_declaration(AlParser.Local_declarationContext ctx) {

        if (ctx.variable() != null)
            // Visita a variavel
            visitVariable(ctx.variable());
        else if(ctx.basic_type() != null) { // Caso seja uma constante
            saida.append("#define " + ctx.IDENT().getText() + " " + ctx.const_value().getText()); 
            saida.append("\n");
        }
        else if(ctx.TIPOW() != null){ // caso seja uma tipagem customizada
            saida.append("typedef ");
            visitType(ctx.type());
            saida.append(ctx.IDENT() + ";\n");
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitVariable(AlParser.VariableContext ctx) {
        String nomeVar = "";
        String strTipoVar = ctx.type().getText();
        TipoAl tipoVar = TipoAl.INVALIDO;
        boolean ponteiro = false;
        
        // Verifica se a variavel é uma definição de struct
        if (ctx.type().register() != null) {
            visitRegister(ctx.type().register());
            for(int i=0; i < ctx.identifier().size(); i++) {
                nomeVar = ctx.identifier(i).getText();
                saida.append(nomeVar);
                saida.append(";\n");
            }
        }
        // Verifica se é um ponteiro
        else if (ctx.type().extend_type() != null) {
            // Remove a marcação de ponteiro da variável
            strTipoVar = strTipoVar.replace("^", "");
            switch (strTipoVar) {
                case "inteiro":
                    tipoVar = TipoAl.INTEIRO;
                    strTipoVar = "int";
                    break;
                case "real":
                    tipoVar = REAL;
                    strTipoVar = "float";
                    break;
                case "literal":
                    tipoVar = TipoAl.CADEIA;
                    strTipoVar = "char";
                    break;
                default:
                    tipoVar = TipoAl.TYPEDEF;
            }

            // Caso seja um ponteiro, adiciona a marcação de ponteiro em C
            if (ctx.type().extend_type().ESTENDIDO() != null){
                strTipoVar = strTipoVar.concat("*");
                ponteiro = true;
            }

            
            // Percorre os identificadores
            for(int i=0; i < ctx.identifier().size(); i++){
                nomeVar = ctx.identifier(i).getText();

                if (tipoVar == TipoAl.TYPEDEF){ // Tipos customizados
                    tabela.verificarTipoCustomizados(nomeVar, strTipoVar); // Verifica e adiciona na tabela de simbolos o respectivo tipo customizado
                }
                else {
                    tabela.inserir(nomeVar, tipoVar, ponteiro); // Adiciona na tabela de simbolos
                }

                // Escreve no arquivo o tipo da variavel e o nome dela
                saida.append(strTipoVar).append(" ").append(nomeVar);

                // Caso seja a definição de String, é adicionado um tamanho fixo de no máximo 80 caracteres.
                if(tipoVar.equals(TipoAl.CADEIA)){
                    saida.append("[80]");
                }
                saida.append(";\n");
            }
        }


        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitType(AlParser.TypeContext ctx) {

        // Verifica se é um ponteiro
        if (ctx.extend_type() != null) {
            saida.append(ctx.extend_type().getText());
        }
        // Verifica se é uma estrutura de dados
        else if(ctx.register() != null) {
            visitRegister(ctx.register());
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_read(AlParser.Cmd_readContext ctx) {
        // Busca o nome e o tipo da váriavel para aplicar a regra correta na leitura doc código em C
        String nomeVar = ctx.identifier().get(0).getText();
        TipoAl tipoVar = tabela.verificar(nomeVar).tipo;
        String tipoVarParam = "";
        tipoVarParam = verificaTipoVarParam(tipoVar);

        // Escreve o comando scanf no arquivo de saida
        saida.append("scanf(\""+tipoVarParam+"\", &").append(nomeVar).append(");\n");
        return null;
    }

    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_write(AlParser.Cmd_writeContext ctx) {
        String nomeVar;
        String tipoVar;
        EntradaTabelaDeSimbolos entradaTabelaDeSimbolos;

        // Percorre as expressões aritmeticas
        for(int i=0; i < ctx.arithmetic_exp().size(); i++){
            saida.append("printf(");
            nomeVar = ctx.arithmetic_exp(i).term(0).getText();
            entradaTabelaDeSimbolos = tabela.verificar(nomeVar);
            // Verifica se encontrou a varíavel na tabela de simbolos
            if(entradaTabelaDeSimbolos != null){
                // Escolhe qual operador deve ser utilizado no printf baseado na tipagem da variável
                tipoVar = GeradorUtils.verificaTipoVarParam(entradaTabelaDeSimbolos.tipo);
                saida.append("\"").append(tipoVar).append("\", ");
            }
            // Visita a expressão aritmetica
            visitArithmetic_exp(ctx.arithmetic_exp(i));
            saida.append(");\n");
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitLogical_op_1(AlParser.Logical_op_1Context ctx) {
        // Escreve no arquivo o operador lógico "ou"
        saida.append(" || ");
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitLogical_op_2(AlParser.Logical_op_2Context ctx) {
        // Escreve no arquivo o operador lógico "e"
        saida.append(" && ");
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_case(AlParser.Cmd_caseContext ctx) {

        // Escreve no arquivo o comando switch
        saida.append("switch(");
        // Visita e escreve no arquivo a expressão aritmetica de entrada do switch
        visitArithmetic_exp(ctx.arithmetic_exp());
        saida.append("){\n");
        // Visita e escreve no arquivo os itens do switch
        visitSelection(ctx.selection());

        // Garante que sempre terá uma opção default
        if(ctx.SENAO() != null){
            saida.append("default:\n");
            visitCmd(ctx.cmd());
            saida.append("break;\n");
        }

        saida.append("}");

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitSelection(AlParser.SelectionContext ctx) {

        // Visita cada item do switch
        ctx.selection_item().forEach(si -> visitSelection_item(si));

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitSelection_item(AlParser.Selection_itemContext ctx) {
        // Visita a constante que será comparada
        visitConstant(ctx.constant());
        // Visita os comandos que estão dentro do item do switch
        ctx.cmd().forEach(cmd -> {
            visitCmd(cmd);
            saida.append("break;\n");
        });
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitConstant(AlParser.ConstantContext ctx) {
        // Visita o intervalo númerico da constante
        ctx.interval_number().forEach(in -> visitInterval_number(in));
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitInterval_number(AlParser.Interval_numberContext ctx) {

        // Define o inicio e o fim do intervalo numerico
        int inicio = Integer.parseInt(ctx.NUM_INT(0).getText());
        int fim = inicio;
        // Atualiza o valor do fim para caso tenha mais de uma condição no mesmo item
        if(ctx.NUM_INT().size() > 1){
            fim = Integer.parseInt(ctx.NUM_INT(1).getText());
        }
        // Escreve no arquivo as condições do item do switch
        for(int i=inicio; i <= fim; i++){
            saida.append("case ").append(i).append(":\n");
        }
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_if(AlParser.Cmd_ifContext ctx) {

        // Escreve no arquivo o comando de condição
        saida.append("if").append("(");

        // Visita a expressão de condição
        visitExpression(ctx.expression());

        saida.append(") {\n");

        // Visita o comando da condição
        visitCmd(ctx.cmd(0));

        saida.append("}\n");

        // Visita o caso contrário da condição
        if(ctx.SENAO() != null){
            saida.append("else {\n");
            visitCmd(ctx.cmd(1));
            saida.append("}\n");
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitExpression(AlParser.ExpressionContext ctx) {

        // Percorre os termos logicos da expressão
        for(int i=0; i < ctx.logical_term().size(); i++){
            // Visita o termo lógico
            visitLogical_term(ctx.logical_term(i));

            // Caso tenha o condicional "ou" escreve no arquivo
            if(ctx.logical_op_1().size() > 0 && i != ctx.logical_term().size()-1){
                saida.append(" || ");
            }
        }
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitLogical_term(AlParser.Logical_termContext ctx) {

        // Percorre os fatores da expressão
        for(int i=0; i < ctx.logical_factor().size(); i++){
            // Visita o fator
            visitLogical_factor(ctx.logical_factor(i));

            // Caso tenha o condicional "e" escreve no arquivo
            if(ctx.logical_op_2().size() > 0 && i != ctx.logical_factor().size()-1){
                saida.append(" && ");
            }
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitLogical_factor(AlParser.Logical_factorContext ctx) {

        // Verifica caso seja uma negação
        if(ctx.NAO() != null){
            saida.append("!");
        }

        // Visita a expressão relacional
        visitRelational_exp(ctx.logical_plot().relational_exp());
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitRelational_exp(AlParser.Relational_expContext ctx) {

        // Verifica se tem abre parenteses na expressao
        if (ctx.ABRE_PARENTESE() != null){
            saida.append("(");
        }

        // Visita a expressão aritmetica
        visitArithmetic_exp(ctx.arithmetic_exp(0));

        // Verifica se tem um operador relacional
        if (ctx.relational_op() != null) {
            visitRelational_op(ctx.relational_op());
            visitArithmetic_exp(ctx.arithmetic_exp(1));
        }

        // Verifica se tem fecha parenteses na expressao
        if (ctx.FECHA_PARENTESE() != null)
            saida.append(")");

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitRelational_op(AlParser.Relational_opContext ctx) {
        String aux = ctx.getText();
        // Compara os operadores relacionais que são diferentes nas duas linguagens
        // e adapta para escrever no arquivo da forma correta
        switch (aux){
            case "=":
                aux = "==";
                break;
            case "<>":
                aux = "!=";
                break;
        }
        saida.append(" " + aux + " ");
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitArithmetic_exp(AlParser.Arithmetic_expContext ctx) {

        // Verifica se tem um abre parenteses na expressao
        if(ctx.getText().contains("(")){
            saida.append("(");
        }

        // Visita os termos da expressão
        for(int i=0; i < ctx.term().size(); i++){
            visitTerm(ctx.term(i));
            // Verifica se é uma adição ou subtração
            if(ctx.op1().size() > 0 && i != ctx.term().size()-1){
                saida.append(" " + ctx.op1(0).getText() + " ");
            }
        }
        
        // Verifica se tem um fecha parenteses na expressao
        if(ctx.getText().contains(")")){
            saida.append(")");
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitTerm(AlParser.TermContext ctx) {

        // Verifica se tem algum fator na expressão
        if(ctx.factor().size() > 0){
            // Percorre a lista de fatores
            for(int i=0; i < ctx.factor().size(); i++){
                // Visita o fator
                visitFactor(ctx.factor(i));
                // Verifica se é uma divisão ou multiplicação
                if(ctx.op2().size() > 0 && i != ctx.factor().size()-1){
                    saida.append(" " + ctx.op2(0).getText() + " ");
                }
            }
        }
        else{
            saida.append(ctx.getText());
        }

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitFactor(AlParser.FactorContext ctx) {

        // Verifica se tem um parcel na expressão
        if(ctx.parcel().size() > 0){
            // Percorre os parcels
            for(int i=0; i < ctx.parcel().size(); i++){

                // Visita a parcela
                visitParcel(ctx.parcel(i));

                // Caso seja uma operação de "mod" adiciona o "%"
                if(ctx.op3().size() > 0 && i != ctx.parcel().size()-1){
                    saida.append(" " + ctx.op3(0).getText() + " ");
                }
            }
        }
        else{
            saida.append(ctx.getText());
        }
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitParcel(AlParser.ParcelContext ctx) {

        // Escreve no valor do termo da expressao
        saida.append(ctx.getText());

        return null;
    }


    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_assignment(AlParser.Cmd_assignmentContext ctx) {
        
        // Salva o termo da esquerda do comando de atribuição
        String nomeVar = ctx.identifier().getText();
        // Busca seu tipo na tabela de simbolos
        EntradaTabelaDeSimbolos tipoVar = tabela.verificar(nomeVar);
        boolean ponteiro = false;

        // Verifica se encontrou na tabela
        if (tipoVar != null) {
            // Atualiza a flag ponteiro para o valor contido na tabela de simbolos
            ponteiro = tipoVar.ponteiro;
            // Caso seja um ponteiro e não esteja atribuindo diretamente ao endereço da várivavel, é necessário adicionar o asterisco antes do nome da variavel.
            if (ponteiro && !ctx.expression().getText().contains("&")) {
                nomeVar = "*" + nomeVar;
            }

            // Verifica se a atribuição envolve uma variavel do tipo string
            if(tipoVar.tipo.equals(TipoAl.CADEIA)) {
                // Utiliza o comando strcpy da biblioteca <string.h>
                saida.append("strcpy(");
                saida.append(nomeVar);
                saida.append(", ");
                visitExpression(ctx.expression());
                saida.append(")");
            }
            else { // Se não escreve o comando de atribuição padrão
                saida.append(nomeVar);
                saida.append(" = ");
                visitExpression(ctx.expression());
            }
        }

        saida.append(";\n");
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_for(AlParser.Cmd_forContext ctx) {

        // Escreve no arquivo o laço "for"
        saida.append("for(").append(ctx.IDENT()).append("=");

        // Visita a expressão aritmetica
        visitArithmetic_exp(ctx.arithmetic_exp(0));

        // Adiciona a condição de parada do laço
        saida.append("; ").append(ctx.IDENT()).append(" <= ");

        // Visita a expressão aritmetica
        visitArithmetic_exp(ctx.arithmetic_exp(1));

        saida.append("; ");

        // Adiciona o interador do laço
        saida.append(ctx.IDENT() + "++");

        saida.append("){\n");

        // Visita os comandos incluso neste laço
        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        saida.append("}\n");

        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_while(AlParser.Cmd_whileContext ctx) {
        // Escreve no arquivo o comando "while"
        saida.append("while(");
        // Visita a expressão
        visitExpression(ctx.expression());
        saida.append("){\n");

        // Visita os comandos do laço
        ctx.cmd().forEach(cmd -> visitCmd(cmd));
        saida.append("}\n");
        return null;
    }

    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitCmd_do(AlParser.Cmd_doContext ctx) {
        
        // Escreve no arquivo o comando "do"
        saida.append("do {\n");

        // Visita os comandos inclusos no laço
        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        saida.append("} while(");

        // Visita a expressão de parada do laço
        visitExpression(ctx.expression());

        saida.append(");\n");

        return null;
    }


    
    /** 
     * @param ctx
     * @return Void
     */
    @Override
    public Void visitRegister(AlParser.RegisterContext ctx) {
        // Visita a criação de uma estrutura de dados (struct)
        
        // Inicializa as variaveis
        String nomeVar;
        String strTipoVar;
        TipoAl tipoVar = TipoAl.INVALIDO;
        boolean ponteiro = false;
        String identifier = ctx.getParent().getParent().getPayload().getText().split(":")[0].replace("tipo", ""); // Busca o nome da variavel a qual a struct esta sendo atribuida
        saida.append("struct {\n");

        // Percorre as definições de variaveis dentro da estrutura de dados
        for(int i=0; i < ctx.variable().size(); i++){
            // Visita a variavel
            visitVariable(ctx.variable(i));

            // Faz o tratamento no nome da variavel para inseri-la na tabela de simbolos
            // Para toda propriedade da estrutura de dados, é concatenado o nome da variavel e um ponto antes da propriedade em si.
            nomeVar = identifier + "." + ctx.variable(i).identifier(0).getText();
            strTipoVar = ctx.variable(i).type().getText();
            switch (strTipoVar){
                case "inteiro":
                    tipoVar = TipoAl.INTEIRO;
                    break;
                case "real":
                    tipoVar = REAL;
                    break;
                case "literal":
                    tipoVar = TipoAl.CADEIA;
                    break;
            }

            // Insere na tabela de simbolos
            tabela.inserir(nomeVar, tipoVar, ponteiro);
        }

        saida.append("} ");

        return null;
    }
}
