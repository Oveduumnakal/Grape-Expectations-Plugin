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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.util.AsyncBufferedImage;

/**
 * Draggable in-game overlay rendering the four wine-fermenting rows: inventory counts
 * (grapes / jug of water / fermenting wine) spread evenly across the row, a projected
 * level progress bar labelled with its percent and the wines still needed to level, the
 * estimated Cooking XP the batch will bank, and a smoothly decreasing fermentation
 * countdown. The box keeps a fixed width so it never snaps between sizes; each row is
 * shown only when its config toggle is on and there is something to display, and the whole
 * overlay hides when the player holds none of the relevant items.
 */
public class GrapeExpectationsOverlay extends Overlay
{
	private static final Color BACKGROUND = ComponentConstants.STANDARD_BACKGROUND_COLOR;
	private static final Color BORDER = new Color(56, 48, 35);
	private static final Color TEXT = Color.WHITE;
	private static final Color COUNT_COLOR = new Color(220, 220, 220);
	private static final Color TRACK = new Color(40, 40, 40);

	private static final int PAD = 6;
	private static final int CONTENT_WIDTH = 168;
	private static final int WIDTH = PAD * 2 + CONTENT_WIDTH;
	private static final int ICON = 18;
	private static final int ICON_GAP = 3;
	private static final int ROW_GAP = 5;
	private static final int BAR_HEIGHT = 14;

	private final GrapeExpectationsPlugin plugin;
	private final GrapeExpectationsConfig config;
	private final ItemManager itemManager;

	/** Cached item icons keyed by item id, populated asynchronously on first use. */
	private final Map<Integer, AsyncBufferedImage> iconCache = new HashMap<>();

	GrapeExpectationsOverlay(GrapeExpectationsPlugin plugin, GrapeExpectationsConfig config, ItemManager itemManager)
	{
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		WineTally tally = plugin.getTally();

		if (tally == null || isIdle(tally))
			return null;

		graphics.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics fm = graphics.getFontMetrics();
		int lineHeight = fm.getHeight();

		boolean fermenting = tally.isFermenting();
		boolean showCounts = config.showCounts();
		boolean showLevel = config.showLevelProgress() && fermenting;
		boolean showXp = config.showBankedXp() && fermenting;
		boolean showTimer = config.showFermentTimer() && plugin.isFermenting();

		if (!showCounts && !showLevel && !showXp && !showTimer)
			return null;

		LevelProjection projection = showLevel ? plugin.getProjection() : null;

		int[] rowHeights = {
				showCounts ? ICON : 0,
				showLevel ? BAR_HEIGHT : 0,
				showXp ? lineHeight : 0,
				showTimer ? BAR_HEIGHT : 0
		};
		int rowCount = 0;
		int bodyHeight = 0;

		for (int h : rowHeights)
		{
			if (h == 0)
				continue;

			bodyHeight += h;
			rowCount++;
		}

		int height = PAD * 2 + bodyHeight + Math.max(0, rowCount - 1) * ROW_GAP;

		graphics.setColor(BACKGROUND);
		graphics.fillRect(0, 0, WIDTH, height);
		graphics.setColor(BORDER);
		graphics.drawRect(0, 0, WIDTH - 1, height - 1);

		int y = PAD;

		if (showCounts)
		{
			drawCounts(graphics, fm, PAD, y, tally);
			y += ICON + ROW_GAP;
		}

		if (showLevel)
		{
			drawLevelBar(graphics, fm, PAD, y, projection);
			y += BAR_HEIGHT + ROW_GAP;
		}

		if (showXp)
		{
			String text = estXpText(plugin.getBankedXp());
			int textX = PAD + (CONTENT_WIDTH - fm.stringWidth(text)) / 2;
			graphics.setColor(config.bankedXpColor());
			graphics.drawString(text, textX, y + fm.getAscent());
			y += lineHeight + ROW_GAP;
		}

		if (showTimer)
		{
			String label = timerLabel(plugin.getFermentRemainingSeconds());
			ProgressBar.draw(graphics, PAD, y, CONTENT_WIDTH, BAR_HEIGHT, plugin.getFermentFraction(),
					config.timerBarColor(), TRACK, BORDER, label, TEXT, fm);
		}

		return new Dimension(WIDTH, height);
	}

	/** Draws the three icon-and-count segments of row 1, one centred in each equal third. */
	private void drawCounts(Graphics2D g, FontMetrics fm, int x, int y, WineTally tally)
	{
		int[] ids = {ItemID.GRAPES, ItemID.JUG_WATER, ItemID.JUG_UNFERMENTED_WINE};
		int[] counts = {tally.getGrapes(), tally.getJugsOfWater(), tally.getUnfermentedWine()};
		int cellWidth = CONTENT_WIDTH / ids.length;

		for (int i = 0; i < ids.length; i++)
		{
			String text = ShortFormat.value(counts[i]);
			int segWidth = ICON + ICON_GAP + fm.stringWidth(text);
			int segX = x + i * cellWidth + (cellWidth - segWidth) / 2;
			drawCount(g, fm, segX, y, ids[i], text);
		}
	}

	/** Draws one icon-and-count segment starting at the given x. */
	private void drawCount(Graphics2D g, FontMetrics fm, int x, int y, int itemId, String count)
	{
		g.drawImage(iconFor(itemId), x, y, ICON, ICON, null);

		int textX = x + ICON + ICON_GAP;
		int baseline = y + (ICON + fm.getAscent() - fm.getDescent()) / 2;
		g.setColor(COUNT_COLOR);
		g.drawString(count, textX, baseline);
	}

	/**
	 * Draws the full-width level bar captioned with its fill percent and the wines still
	 * needed to reach the next level, e.g. {@code 40% (1,700)}.
	 */
	private void drawLevelBar(Graphics2D g, FontMetrics fm, int x, int y, LevelProjection projection)
	{
		int pct = (int) Math.round(projection.getFraction() * 100);
		int wines = plugin.getWinesToNextLevel();
		String label = wines > 0
				? pct + "% (" + ShortFormat.value(wines) + ")"
				: pct + "%";
		ProgressBar.draw(g, x, y, CONTENT_WIDTH, BAR_HEIGHT, projection.getFraction(),
				config.levelBarColor(), TRACK, BORDER, label, TEXT, fm);
	}

	private AsyncBufferedImage iconFor(int itemId)
	{
		return iconCache.computeIfAbsent(itemId, itemManager::getImage);
	}

	private static String estXpText(double bankedXp)
	{
		return "Est. xp: " + ShortFormat.value(Math.round(bankedXp));
	}

	private static String timerLabel(double seconds)
	{
		return (int) Math.ceil(seconds) + "s";
	}

	private static boolean isIdle(WineTally tally)
	{
		return tally.getGrapes() == 0
				&& tally.getJugsOfWater() == 0
				&& tally.getUnfermentedWine() == 0
				&& tally.getJugsOfWine() == 0;
	}
}
