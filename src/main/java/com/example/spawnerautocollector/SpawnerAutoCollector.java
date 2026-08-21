package com.example.spawnerautocollector;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

/**
 * Addon standalone para o Baritone (branch 26.1 / PR #4990).
 *
 * Fluxo:
 * 1. Detecta quando o jogador quebra um bloco "spawner".
 * 2. Envia o comando "#follow entity item" para o Baritone, forçando
 *    ele a ir pegar o item dropado, mesmo que mineScanDroppedItems
 *    não reconheça o spawner como "drop esperado".
 * 3. A cada tick, verifica se um item "spawner" apareceu no inventário.
 * 4. Assim que detecta o pickup, cancela o follow e reenvia "#mine spawner".
 *
 * ATENCAO: os nomes de pacote (baritone.api.*) seguem a convencao do
 * projeto oficial cabaletta/baritone. Como a branch 26.1 ainda esta
 * em PR aberto, confirme se esses nomes de classe nao mudaram no seu
 * checkout local antes de compilar (olhe dentro de baritone-api-*.jar).
 */
public class SpawnerAutoCollector implements ClientModInitializer {

    // Estado da automacao
    private boolean waitingForPickup = false;
    private int previousSpawnerCount = 0;

    @Override
    public void onInitializeClient() {

        // 1) Quando um spawner e quebrado, muda para o modo "follow item"
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == Blocks.SPAWNER) {
                waitingForPickup = true;
                previousSpawnerCount = countSpawnersInInventory();
                runBaritoneCommand("follow entity item");
            }
        });

        // 2) A cada tick, verifica se ja pegou o spawner
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!waitingForPickup) return;

            int currentCount = countSpawnersInInventory();
            if (currentCount > previousSpawnerCount) {
                // Pegou! Cancela o follow e volta a minerar
                waitingForPickup = false;
                runBaritoneCommand("cancel");
                runBaritoneCommand("mine spawner");
            }
        });
    }

    private int countSpawnersInInventory() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return 0;

        int count = 0;
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            // "spawner_item" pode nao existir em todas as versoes;
            // se sua versao usar Items.SPAWNER diretamente, ajuste aqui.
            if (!stack.isEmpty() && stack.getItem() == Items.SPAWNER) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void runBaritoneCommand(String command) {
        IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        baritone.getCommandManager().execute(command);
    }
}
