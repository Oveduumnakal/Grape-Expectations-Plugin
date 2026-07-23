/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oveduumnakal;

import javax.inject.Inject;

import com.google.inject.Provides;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * Tracks wine fermenting and drives the {@link GrapeExpectationsOverlay}.
 *
 * <p>Inventory changes recompute the {@link WineTally} and, when a fresh unfermented wine
 * appears, restart the {@link FermentTimer} (matching the in-game restart-on-new-wine
 * behaviour); the timer is cleared once the batch converts. Cooking level and XP are read
 * live from the client so the banked-XP and level-projection rows stay current. All state
 * is reset on logout or world hop.
 */
@PluginDescriptor(
		name = "Grape Expectations",
		description = "Track wine fermenting: counts, banked Cooking XP, projected level, and a ferment timer",
		tags = {"wine", "ferment", "cooking", "grapes", "xp", "level", "timer", "overlay", "skilling"}
)
public class GrapeExpectationsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GrapeExpectationsConfig config;

	private final FermentTimer timer = new FermentTimer();

	private GrapeExpectationsOverlay overlay;
	private volatile WineTally tally = WineTally.EMPTY;
	private int previousUnfermented;

	@Override
	protected void startUp()
	{
		overlay = new GrapeExpectationsOverlay(this, config, itemManager);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlay = null;
		reset();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INV)
			return;

		ItemContainer inventory = event.getItemContainer();
		WineTally updated = new WineTally(
				inventory.count(ItemID.GRAPES),
				inventory.count(ItemID.JUG_WATER),
				inventory.count(ItemID.JUG_UNFERMENTED_WINE),
				inventory.count(ItemID.JUG_WINE));
		tally = updated;

		int unfermented = updated.getUnfermentedWine();

		if (unfermented > previousUnfermented)
			timer.reset(client.getTickCount());
		else if (unfermented == 0)
			timer.clear();

		previousUnfermented = unfermented;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();

		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING)
			reset();
	}

	private void reset()
	{
		tally = WineTally.EMPTY;
		timer.clear();
		previousUnfermented = 0;
	}

	WineTally getTally()
	{
		return tally;
	}

	boolean isFermenting()
	{
		return timer.isActive() && timer.remainingTicks(client.getTickCount()) > 0;
	}

	double getFermentFraction()
	{
		return timer.fraction(client.getTickCount());
	}

	double getFermentRemainingSeconds()
	{
		return timer.remainingSeconds(client.getTickCount());
	}

	int getCookingLevel()
	{
		return client.getRealSkillLevel(Skill.COOKING);
	}

	int getCookingXp()
	{
		return client.getSkillExperience(Skill.COOKING);
	}

	double getBankedXp()
	{
		int unfermented = tally.getUnfermentedWine();

		if (config.xpMode() == XpMode.OPTIMISTIC)
			return unfermented * (double) WineXpModel.FERMENT_XP;

		return WineXpModel.bankedXp(unfermented, getCookingLevel());
	}

	LevelProjection getProjection()
	{
		return WineXpModel.project(getCookingXp(), getBankedXp());
	}

	int getWinesToNextLevel()
	{
		return WineXpModel.winesToNextLevel(getCookingXp());
	}

	int getWinesTo99()
	{
		return WineXpModel.winesToLevel(getCookingXp(), 99);
	}

	boolean pricesKnown()
	{
		return itemManager.getItemPrice(ItemID.GRAPES) > 0
				&& itemManager.getItemPrice(ItemID.JUG_WATER) > 0
				&& itemManager.getItemPrice(ItemID.JUG_WINE) > 0;
	}

	int getWineMarginPerWine()
	{
		return WineCostModel.marginPerWine(
				itemManager.getItemPrice(ItemID.GRAPES),
				itemManager.getItemPrice(ItemID.JUG_WATER),
				itemManager.getItemPrice(ItemID.JUG_WINE));
	}

	long getBatchMargin()
	{
		return WineCostModel.totalMargin(
				tally.getUnfermentedWine(),
				itemManager.getItemPrice(ItemID.GRAPES),
				itemManager.getItemPrice(ItemID.JUG_WATER),
				itemManager.getItemPrice(ItemID.JUG_WINE));
	}

	@Provides
	GrapeExpectationsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GrapeExpectationsConfig.class);
	}
}
