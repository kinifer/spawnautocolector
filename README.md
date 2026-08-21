# Spawner Auto Collector

Addon para o Baritone (branch 26.1) que pega spawners dropados no chão
automaticamente, sem precisar apertar tecla nenhuma.

## Passo a passo pra compilar (você mesmo, localmente)

1. Instale o **JDK 25** (necessário pro Minecraft 26.x — confirmei isso
   no repositório oficial `FabricMC/fabric-example-mod`, branch `26.1.2`).
2. Copie o seu `baritone-api-fabric-26.1.jar` (o mesmo que você já usa
   no jogo) para dentro da pasta `libs/` deste projeto.
3. As versões em `gradle.properties` já vêm preenchidas e confirmadas
   contra o repositório oficial `FabricMC/fabric-example-mod` na branch
   `26.1.2` (a mais próxima da sua build "26.1" disponível publicamente).
   Se sua build for exatamente "26.1" sem o ".2", tente rodar assim
   primeiro — geralmente funciona porque são a mesma geração de API.
   Se der erro de "version not found", troque `minecraft_version` pra
   `26.1` no arquivo.
4. No terminal, dentro dessa pasta, rode:
   ```
   ./gradlew build
   ```
   (no Windows: `gradlew.bat build`)
5. O `.jar` compilado vai aparecer em `build/libs/spawner-autocollector-1.0.0.jar`.
6. Copie esse `.jar` pra pasta `mods` do seu Minecraft, junto com o
   Baritone e o Fabric API.
7. Abra o jogo. O mod roda sozinho em segundo plano — não precisa
   ativar nada, ele já fica escutando quando um spawner é quebrado.

## Se der erro de compilação

O mais provável é os nomes de classe da API do Baritone terem mudado
entre a branch 26.1 (ainda em PR, não lançada oficialmente) e o que
documentei aqui. Abra o arquivo:

```
src/main/java/com/example/spawnerautocollector/SpawnerAutoCollector.java
```

e ajuste principalmente essas duas linhas se o compilador reclamar:

```java
if (stack.getItem() == Items.SPAWNER) { ... }
baritone.getCommandManager().execute(command);
```

Uma forma rápida de achar o nome certo: abra o `baritone-api-fabric-26.1.jar`
com um descompilador (ex: JD-GUI) ou extraia com `unzip` e olhe as
classes dentro do pacote `baritone/api/`.
