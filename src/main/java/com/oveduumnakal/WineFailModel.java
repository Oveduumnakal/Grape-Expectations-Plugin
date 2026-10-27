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

/**
 * Probability that a single unfermented wine ferments successfully into its finished wine
 * (rather than a jug of bad wine, which yields no experience).
 *
 * <p>Each {@link WineType} carries a linear success curve from its {@code lowChance} at
 * Cooking level 1 to its {@code highChance} at level 99, with any value at or above 100%
 * meaning a guaranteed success. For regular wine (Mod Ash's 19%–138%) that reproduces both
 * known data points: roughly 60% success at the level-35 minimum and 100% at the documented
 * no-fail level of 68. For wine of Zamorak it gives about 50% at level 65 and 100% at 98. The
 * raw curves are kept on the enum — tunable in one place — rather than hand-built lookup tables.
 *
 * <p>Note: in-game the batch is rolled in chunks (roughly 15 batches plus a remainder)
 * rather than per wine, so a single batch's realized count varies around this rate. The
 * per-wine probability is nonetheless the correct expected value for the banked-XP row —
 * batching changes the variance, not the mean.
 */
public final class WineFailModel
{
	private WineFailModel()
	{
	}

	/**
	 * Success probability in the range [0, 1] for the given wine and Cooking level.
	 *
	 * @param type         the wine being fermented
	 * @param cookingLevel the player's current Cooking level
	 * @return 1.0 at or above the wine's no-fail level, otherwise the interpolated chance
	 */
	public static double successRate(WineType type, int cookingLevel)
	{
		if (cookingLevel >= type.getNoFailLevel())
			return 1.0;

		double low = type.getLowChance();
		double chance = (low + (type.getHighChance() - low) * (cookingLevel - 1) / 98.0) / 100.0;

		return Math.max(0.0, Math.min(1.0, chance));
	}
}
