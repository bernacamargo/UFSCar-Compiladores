# Construção de Compiladores

## Analisador Léxico

Este projeto consiste na implementação de um analisador lexico para a linguagem LA (Linguagem Algorítma). O objetivo deste programa é analisar e reportar erros lexicos que um código em LA possa ter.

---

### Autores

-   Bernardo Camargo [@bernacamargo](https://github.com/bernacamargo)
-   Paulo Betareli [@paulobetareli](https://github.com/paulobetareli)
-   Renata Praisler [@RenataPraisler](https://github.com/RenataPraisler)

## Como utilizar

1. Primeiramente é necessário ter o Java, no mínimo na versão 11, instalado em seu ambiente.
2. Faça o download do executável [analisador-lexico.jar](https://github.com/bernacamargo/UFSCar-Compiladores-analise-lexica/raw/main/analisador-lexico.jar)
3. Agora basta executar o programa baixado através de um terminal. Para isso utilizaremos o comando `java -jar` juntamente com os seguintes parâmetros:

    - **ARG1:** O caminho absoluto para o arquivo baixado no passo anterior;
    - **ARG2:** O caminho absoluto do arquivo de entrada que contém o código em LA;
    - **ARG3:** O caminho absoluto do arquivo de saída.

    <br>

    ```sh
     $ java -jar ARG1 ARG2 ARG3
    ```

    > Os caminhos enviados como parâmetros devem ser **absolutos** e não relativos.

4. Feito isso será criado o arquivo de saída com a análise lexica do algoritmo de entrada.

### Exemplo:

> O arquivo executável .jar e o algoritmo.txt estão na pasta raiz do projeto

-   Comando:

    ```sh
    $ java -jar /home/user/compiladores/analisador-lexico/analisador-lexico.jar /home/user/compiladores/analisador-lexico/algoritmo.txt /home/user/compiladores/analisador-lexico/out.txt
    ```

-   Entrada: algoritmo.txt

    ```
    { classificação da faixa etária segundo um critério arbitrário }

    algoritmo
        declare idade: inteiro

        { leitura da idade }
        leia(idade)

        { classificação }
        caso idade seja
            0: escreva("bebe")
            1..10: escreva("crianca")
            11..14: escreva("pre-adolescente")
            15..18: escreva("adolescente")
            19..120: escreva("adulto")
        senao
            escreva("Idade invalida ou sem classificacao definida")
        fim_caso
    fim_algoritmo

    ```

-   Saída: out.txt

    ```
    <'algoritmo','algoritmo'>
    <'declare','declare'>
    <'idade',IDENT>
    <':',':'>
    <'inteiro','inteiro'>
    <'leia','leia'>
    <'(','('>
    <'idade',IDENT>
    <')',')'>
    <'caso','caso'>
    <'idade',IDENT>
    <'seja','seja'>
    <'0',NUM_INT>
    <':',':'>
    <'escreva','escreva'>
    <'(','('>
    <'"bebe"',CADEIA>
    <')',')'>
    <'1',NUM_INT>
    <'..','..'>
    <'10',NUM_INT>
    <':',':'>
    <'escreva','escreva'>
    <'(','('>
    <'"crianca"',CADEIA>
    <')',')'>
    <'11',NUM_INT>
    <'..','..'>
    <'14',NUM_INT>
    <':',':'>
    <'escreva','escreva'>
    <'(','('>
    <'"pre-adolescente"',CADEIA>
    <')',')'>
    <'15',NUM_INT>
    <'..','..'>
    <'18',NUM_INT>
    <':',':'>
    <'escreva','escreva'>
    <'(','('>
    <'"adolescente"',CADEIA>
    <')',')'>
    <'19',NUM_INT>
    <'..','..'>
    <'120',NUM_INT>
    <':',':'>
    <'escreva','escreva'>
    <'(','('>
    <'"adulto"',CADEIA>
    <')',')'>
    <'senao','senao'>
    <'escreva','escreva'>
    <'(','('>
    <'"Idade invalida ou sem classificacao definida"',CADEIA>
    <')',')'>
    <'fim_caso','fim_caso'>
    <'fim_algoritmo','fim_algoritmo'>
    ```
