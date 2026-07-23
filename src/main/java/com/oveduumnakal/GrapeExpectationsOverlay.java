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
 * (grapes / jug of water / fermenting wine), the banked Cooking XP, a projected level
 * progress bar, and a decreasing fermentation countdown. Each row is shown only when its
 * config toggle is on and it has something to display; the whole overlay hides when the
 * player holds none of the relevant items.
 */
public class GrapeExpectationsOverlay extends Overlay
{
	private static final Color BACKGROUND = ComponentConstants.STANDARD_BACKGROUND_COLOR;
	private static final Color BORDER = new Color(56, 48, 35);
	private static final Color TEXT = Color.WHITE;
	private static final Color COUNT_COLOR = new Color(220, 220, 220);
	private static final Color LEVEL_LABEL_COLOR = new Color(200, 200, 200);
	private static final Color TRACK = new Color(40, 40, 40);

	private static final int PAD = 6;
	private static final int ICON = 18;
	private static final int ICON_GAP = 3;
	private static final int SEG_GAP = 10;
	private static final int ROW_GAP = 5;
	private static final int BAR_WIDTH = 150;
	private static final int BAR_HEIGHT = 14;
	private static final int LEVEL_LABEL_GAP = 5;

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
		boolean showXp = config.showBankedXp() && fermenting;
		boolean showLevel = config.showLevelProgress() && fermenting;
		String estimatesText = config.showTargetEstimates() ? estimatesText() : "";
		boolean showEstimates = !estimatesText.isEmpty();
		boolean showTimer = config.showFermentTimer() && plugin.isFermenting();

		if (!showCounts && !showXp && !showLevel && !showEstimates && !showTimer)
			return null;

		LevelProjection projection = showLevel ? plugin.getProjection() : null;
		String bankedText = showXp ? bankedText(plugin.getBankedXp()) : null;

		int countsW = showCounts ? countsWidth(fm, tally) : 0;
		int xpW = showXp ? fm.stringWidth(bankedText) : 0;
		int levelW = showLevel ? levelRowWidth(fm, projection) : 0;
		int estW = showEstimates ? fm.stringWidth(estimatesText) : 0;
		int timerW = showTimer ? BAR_WIDTH : 0;
		int content = Math.max(Math.max(Math.max(countsW, xpW), Math.max(levelW, estW)), timerW);
		int width = PAD * 2 + content;

		int[] rowHeights = {
				showCounts ? ICON : 0,
				showXp ? lineHeight : 0,
				showLevel ? BAR_HEIGHT : 0,
				showEstimates ? lineHeight : 0,
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
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(BORDER);
		graphics.drawRect(0, 0, width - 1, height - 1);

		int y = PAD;

		if (showCounts)
		{
			drawCounts(graphics, fm, PAD, y, tally);
			y += ICON + ROW_GAP;
		}

		if (showXp)
		{
			graphics.setColor(config.bankedXpColor());
			graphics.drawString(bankedText, PAD, y + fm.getAscent());
			y += lineHeight + ROW_GAP;
		}

		if (showLevel)
		{
			drawLevelRow(graphics, fm, PAD, y, projection);
			y += BAR_HEIGHT + ROW_GAP;
		}

		if (showEstimates)
		{
			graphics.setColor(LEVEL_LABEL_COLOR);
			graphics.drawString(estimatesText, PAD, y + fm.getAscent());
			y += lineHeight + ROW_GAP;
		}

		if (showTimer)
		{
			String label = timerLabel(plugin.getFermentRemainingSeconds());
			ProgressBar.draw(graphics, PAD, y, BAR_WIDTH, BAR_HEIGHT, plugin.getFermentFraction(),
					config.timerBarColor(), TRACK, BORDER, label, TEXT, fm);
		}

		return new Dimension(width, height);
	}

	/** Draws the three icon-and-count segments of row 1 left to right. */
	private void drawCounts(Graphics2D g, FontMetrics fm, int x, int y, WineTally tally)
	{
		int cx = x;
		cx = drawCount(g, fm, cx, y, ItemID.GRAPES, tally.getGrapes());
		cx = drawCount(g, fm, cx, y, ItemID.JUG_WATER, tally.getJugsOfWater());
		drawCount(g, fm, cx, y, ItemID.JUG_UNFERMENTED_WINE, tally.getUnfermentedWine());
	}

	/** Draws one icon-and-count segment and returns the x at which the next segment starts. */
	private int drawCount(Graphics2D g, FontMetrics fm, int x, int y, int itemId, int count)
	{
		g.drawImage(iconFor(itemId), x, y, ICON, ICON, null);

		int textX = x + ICON + ICON_GAP;
		int baseline = y + (ICON + fm.getAscent() - fm.getDescent()) / 2;
		String text = String.valueOf(count);
		g.setColor(COUNT_COLOR);
		g.drawString(text, textX, baseline);

		return textX + fm.stringWidth(text) + SEG_GAP;
	}

	/** Draws the row 3 level bar flanked by the projected level and the next level. */
	private void drawLevelRow(Graphics2D g, FontMetrics fm, int x, int y, LevelProjection projection)
	{
		String left = String.valueOf(projection.getProjectedLevel());
		String right = String.valueOf(projection.getProjectedLevel() + 1);
		int baseline = y + (BAR_HEIGHT + fm.getAscent() - fm.getDescent()) / 2;

		g.setColor(LEVEL_LABEL_COLOR);
		g.drawString(left, x, baseline);

		int barX = x + fm.stringWidth(left) + LEVEL_LABEL_GAP;
		String pct = Math.round(projection.getFraction() * 100) + "%";
		ProgressBar.draw(g, barX, y, BAR_WIDTH, BAR_HEIGHT, projection.getFraction(),
				config.levelBarColor(), TRACK, BORDER, pct, TEXT, fm);

		g.setColor(LEVEL_LABEL_COLOR);
		g.drawString(right, barX + BAR_WIDTH + LEVEL_LABEL_GAP, baseline);
	}

	private int countsWidth(FontMetrics fm, WineTally tally)
	{
		int total = segmentWidth(fm, tally.getGrapes())
				+ segmentWidth(fm, tally.getJugsOfWater())
				+ segmentWidth(fm, tally.getUnfermentedWine());

		return total - SEG_GAP;
	}

	private int segmentWidth(FontMetrics fm, int count)
	{
		return ICON + ICON_GAP + fm.stringWidth(String.valueOf(count)) + SEG_GAP;
	}

	private int levelRowWidth(FontMetrics fm, LevelProjection projection)
	{
		String left = String.valueOf(projection.getProjectedLevel());
		String right = String.valueOf(projection.getProjectedLevel() + 1);

		return fm.stringWidth(left) + LEVEL_LABEL_GAP + BAR_WIDTH + LEVEL_LABEL_GAP + fm.stringWidth(right);
	}

	private AsyncBufferedImage iconFor(int itemId)
	{
		return iconCache.computeIfAbsent(itemId, itemManager::getImage);
	}

	private static String bankedText(double bankedXp)
	{
		return "Banked +" + String.format("%,d", Math.round(bankedXp)) + " xp";
	}

	/** Builds the "wines to next level / to 99" line, omitting either part once reached. */
	private String estimatesText()
	{
		int next = plugin.getWinesToNextLevel();
		int to99 = plugin.getWinesTo99();
		StringBuilder text = new StringBuilder();

		if (next > 0)
			text.append("Next: ").append(String.format("%,dw", next));

		if (to99 > 0)
		{
			if (text.length() > 0)
				text.append("  ·  ");

			text.append("99: ").append(String.format("%,dw", to99));
		}

		return text.toString();
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
