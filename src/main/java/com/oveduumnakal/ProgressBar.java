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
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Draws a labelled horizontal progress bar onto the game canvas: a background track, a
 * fill proportional to a 0–1 value, a one-pixel border, and a centred caption. Shared by
 * overlay row 3 (level progress) and row 4 (the decreasing ferment countdown).
 */
final class ProgressBar
{
	private ProgressBar()
	{
	}

	/**
	 * Renders the bar at the given position.
	 *
	 * @param g          the canvas graphics context
	 * @param x          left edge
	 * @param y          top edge
	 * @param width      total bar width including border
	 * @param height     total bar height including border
	 * @param fraction   fill amount, clamped to [0, 1]
	 * @param fill       fill colour
	 * @param track      background (unfilled) colour
	 * @param border     outline colour
	 * @param label      centred caption, or null/empty for none
	 * @param labelColor caption colour
	 * @param fm         font metrics for centring the caption
	 */
	static void draw(Graphics2D g, int x, int y, int width, int height, double fraction,
			Color fill, Color track, Color border, String label, Color labelColor, FontMetrics fm)
	{
		double clamped = Math.max(0.0, Math.min(1.0, fraction));
		int fillWidth = (int) Math.round((width - 2) * clamped);

		g.setColor(track);
		g.fillRect(x, y, width, height);

		g.setColor(fill);
		g.fillRect(x + 1, y + 1, fillWidth, height - 2);

		g.setColor(border);
		g.drawRect(x, y, width - 1, height - 1);

		if (label == null || label.isEmpty())
			return;

		int textX = x + (width - fm.stringWidth(label)) / 2;
		int textY = y + (height + fm.getAscent() - fm.getDescent()) / 2;
		g.setColor(labelColor);
		g.drawString(label, textX, textY);
	}
}
