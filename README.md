# Batalha Naval - Java com Socket UDP

## Integrantes da Equipe

* Douglas Bispo
* Franceylton
* Matheus Henrique
* Ruan Victor

## Descrição do Jogo

Batalha Naval é um jogo multiplayer desenvolvido em Java utilizando socket UDP. O jogo suporta até 4 jogadores simultâneos e segue as regras clássicas do Batalha Naval, onde os jogadores tentam afundar os navios uns dos outros até que reste apenas um vencedor.

O jogo inicia quando todos os jogadores aceitam participar e confirmação de prontidão. Cada jogador recebe um tabuleiro 10x10 com 5 navios posicionados aleatoriamente. Os jogadores se revezam para atacar os oponentes escolhendo uma linha e uma coluna. O jogo termina quando resta apenas um jogador com navios no tabuleiro.

## Como Executar o Projeto

1. Certifique-se de ter o Java JDK instalado.

2. Clone o repositório do projeto ou baixe os arquivos.

3. Compile os arquivos Java:
~~~Java
javac Server.java Client.java
~~~
4. Inicie o servidor:
~~~Java
java Server.java
~~~
5. Para cada jogador (até 4), inicie um cliente em uma nova instância do terminal:
~~~Java
java Client.java
~~~
## Como Jogar

1. O servidor será iniciado e os jogadores deverão rodar a classe Cliente.

2. Cada jogador será perguntado se deseja jogar e deverá inserir seu nome.

3. O jogo iniciará apenas quando os quatro jogadores aceitarem jogar.

4. Em seguida, o jogo perguntará se cada jogador está pronto.

5. Cada jogador receberá um tabuleiro 10x10 com 5 navios.

6. Durante sua vez, o jogador:

    * Escolhe qual jogador deseja atacar.

    * Escolhe a linha e a coluna para atacar.

7. O jogo indicará o resultado do ataque:

    * "D" para tiro certeiro (navio atingido).

    * "X" para tiro errado.

    * "N" indica onde estão os navios do próprio jogador.

8. Quando um jogador perder todos os navios, ele será eliminado.

9. O jogo continua até que reste apenas um jogador, que será o vencedor.

10. No final da partida, o jogo perguntará se os jogadores desejam jogar novamente.

    * Se todos aceitarem, uma nova partida será iniciada.

    * Se ao menos um jogador recusar, o servidor será encerrado com a mensagem:
    ~~~Java
      "Algum jogador optou por não jogar novamente, o servidor será encerrado".
    ~~~
