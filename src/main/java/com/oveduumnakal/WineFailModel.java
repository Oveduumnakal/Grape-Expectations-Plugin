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
 * Probability that a single unfermented wine ferments successfully into a jug of
 * wine (rather than a jug of bad wine, which yields no experience).
 *
 * <p>Per Mod Ash (archived at reldo.runescape.wiki/i/97499), the success chance
 * interpolates linearly from 19% to 138% across Cooking levels 1–99, with any value at
 * or above 100% meaning a guaranteed success. That formula reproduces both known data
 * points: roughly 60% success (~40% failure) at the level-35 minimum, and 100% success
 * at the documented no-fail level of 68 (the curve reaches 100% at level ≈ 67.7). The raw
 * formula is kept here — tunable in one place — rather than a hand-built lookup table.
 *
 * <p>Note: in-game the batch is rolled in chunks (roughly 15 batches plus a remainder)
 * rather than per wine, so a single batch's realized count varies around this rate. The
 * per-wine probability is nonetheless the correct expected value for the banked-XP row —
 * batching changes the variance, not the mean.
 */
public final class WineFailModel
{
	/** Lowest Cooking level at which wine can be fermented at all. */
	public static final int MIN_LEVEL = 35;

	/** Cooking level at or above which fermenting never fails. */
	public static final int NO_FAIL_LEVEL = 68;

	private static final double LOW_CHANCE = 19.0;
	private static final double HIGH_CHANCE = 138.0;

	private WineFailModel()
	{
	}

	/**
	 * Success probability in the range [0, 1] for the given Cooking level.
	 *
	 * @param cookingLevel the player's current Cooking level
	 * @return 1.0 at or above {@link #NO_FAIL_LEVEL}, otherwise the interpolated chance
	 */
	public static double successRate(int cookingLevel)
	{
		if (cookingLevel >= NO_FAIL_LEVEL)
			return 1.0;

		double chance = (LOW_CHANCE + (HIGH_CHANCE - LOW_CHANCE) * (cookingLevel - 1) / 98.0) / 100.0;

		return Math.max(0.0, Math.min(1.0, chance));
	}
}
