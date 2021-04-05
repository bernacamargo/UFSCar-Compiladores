# Construção de Compiladores

## Analisador Semântico e Gerador de Código

Este projeto consiste na implementação de um compilador para a linguagem LA (Linguagem Algorítma). 
O objetivo deste etapa é analisar e reportar erros semânticos que um código em LA possa ter e também gerar o código equivalente em C.

---

### Autores

-   Bernardo Camargo [@bernacamargo](https://github.com/bernacamargo) - 620343
-   Paulo Betareli [@paulobetareli](https://github.com/paulobetareli) - 587648
-   Renata Praisler [@RenataPraisler](https://github.com/RenataPraisler) - 746044

## Como compilar

1. Primeiramente é necessário ter o Java 11 e o Maven instalados em seu ambiente.
2. Realize o clone do projeto para seu computador
3. Acesse a pasta `analisador-semanticoo` pelo terminal
4. Utilize o comando `mvn package` para compilar e gerar as classes Java do ANTLR4 e o pacote com as dependencias .jar
5. Os arquivos gerados na build podem ser localizados na pasta `target`.

## Como utilizar

1. É necessário ter o Java 11 instalado
2. Faça o download do executável [compilador-final.jar](https://github.com/bernacamargo/UFSCar-Compiladores/raw/analisador-semantico-e-gerador/compilador-final.jar)
3. Agora basta executar o programa baixado através de um terminal. Para isso utilizaremos o comando `java -jar` juntamente com os seguintes parâmetros:

    - **ARG1:** O caminho absoluto para o arquivo baixado no passo anterior;
    - **ARG2:** O caminho absoluto do arquivo de entrada que contém o código em LA;
    - **ARG3:** O caminho absoluto do arquivo de saída.

    <br>

    ```sh
     $ java -jar ARG1 ARG2 ARG3
    ```

    > Os caminhos enviados como parâmetros devem ser **absolutos**!

4. Feito isso será criado o arquivo de saída com a análise lexica, sintatica e semantica do algoritmo de entrada. Caso o compilador não encontre problemas nestas análises, será escrito no arquivo de saída o código em C relativo ao arquivo de entrada.