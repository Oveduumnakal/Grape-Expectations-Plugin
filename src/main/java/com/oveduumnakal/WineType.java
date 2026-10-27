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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.runelite.api.gameval.ItemID;

/**
 * The kinds of wine the plugin can follow, each with its own ingredients, products, and
 * fermentation success curve.
 *
 * <p>Both wines ferment on the same 12-second window and grant {@link WineXpModel#FERMENT_XP}
 * Cooking XP per success; they differ in the grapes used, the Cooking level needed, and how
 * quickly the chance of bad wine falls away. Each curve interpolates linearly from
 * {@code lowChance} at level 1 to {@code highChance} at level 99 (in percent), with values at
 * or above 100% meaning a guaranteed success.
 */
@Getter
@RequiredArgsConstructor
public enum WineType
{
	/**
	 * Grapes into a jug of wine. Curve per Mod Ash (archived at reldo.runescape.wiki/i/97499):
	 * 19%–138%, about 60% at level 35 and guaranteed from level 68.
	 */
	REGULAR("Jug of wine", ItemID.GRAPES, ItemID.JUG_UNFERMENTED_WINE, ItemID.JUG_WINE,
			35, 68, 19.0, 138.0),

	/**
	 * Zamorak's grapes into wine of Zamorak. Curve from the wiki's skilling chart
	 * (low -125, high 260 out of 256): about 50% at level 65 and guaranteed from level 98.
	 */
	ZAMORAK("Wine of Zamorak", ItemID.ZAMORAK_GRAPES, ItemID.JUG_UNFERMENTED_ZAMORAK_WINE,
			ItemID.WINE_OF_ZAMORAK, 65, 98, -125 / 2.56, 260 / 2.56);

	private final String displayName;
	private final int grapesId;
	private final int unfermentedId;
	private final int wineId;

	/** Lowest Cooking level at which this wine can be made at all. */
	private final int minLevel;

	/** Cooking level at or above which fermenting this wine never fails. */
	private final int noFailLevel;

	private final double lowChance;
	private final double highChance;

	@Override
	public String toString()
	{
		return displayName;
	}
}
