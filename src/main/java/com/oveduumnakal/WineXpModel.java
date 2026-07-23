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

import net.runelite.api.Experience;

/**
 * Turns a fermenting batch into the banked Cooking XP (overlay row 2) and the level
 * projection (overlay row 3).
 *
 * <p>Each unfermented wine that succeeds yields {@link #FERMENT_XP} Cooking XP; the
 * banked total is an expected value weighted by {@link WineFailModel#successRate(int)}
 * so it reflects the low-level chance of producing bad wine. The projection then walks
 * that expected XP forward from the player's current total to find the level bracket the
 * bar should display.
 */
public final class WineXpModel
{
	/** Cooking XP granted when one unfermented wine successfully ferments. */
	public static final int FERMENT_XP = 200;

	private WineXpModel()
	{
	}

	/**
	 * Expected Cooking XP that will be realized once the current batch ferments.
	 *
	 * @param unfermentedCount number of unfermented wines currently held
	 * @param cookingLevel     the player's Cooking level (drives the success weighting)
	 * @return the fail-adjusted expected XP
	 */
	public static double bankedXp(int unfermentedCount, int cookingLevel)
	{
		return unfermentedCount * (double) FERMENT_XP * WineFailModel.successRate(cookingLevel);
	}

	/**
	 * Projects where {@code bankedXp} added to the player's current XP lands them.
	 *
	 * @param currentXp the player's current Cooking XP
	 * @param bankedXp  the XP expected once the batch ferments
	 * @return the current and projected levels plus the fill fraction of the projected bracket
	 */
	public static LevelProjection project(int currentXp, double bankedXp)
	{
		int currentLevel = Experience.getLevelForXp(currentXp);
		double projectedXp = Math.min(currentXp + Math.max(0.0, bankedXp), Experience.MAX_SKILL_XP);
		int projectedLevel = Experience.getLevelForXp((int) Math.floor(projectedXp));

		if (projectedLevel >= Experience.MAX_VIRT_LEVEL)
		{
			return new LevelProjection(currentLevel, Experience.MAX_VIRT_LEVEL, 1.0);
		}

		int bracketStart = Experience.getXpForLevel(projectedLevel);
		int bracketEnd = Experience.getXpForLevel(projectedLevel + 1);
		double fraction = (projectedXp - bracketStart) / (double) (bracketEnd - bracketStart);

		return new LevelProjection(currentLevel, projectedLevel, Math.max(0.0, Math.min(1.0, fraction)));
	}
}
