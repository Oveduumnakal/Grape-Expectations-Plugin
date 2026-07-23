/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.Experience;
import org.junit.Test;

/** Verifies banked-XP weighting and the row 3 level projection, including threshold crossings. */
public class WineXpModelTest
{
	private static final double DELTA = 1e-6;

	@Test
	public void bankedXpIsZeroWithNoWine()
	{
		assertEquals(0.0, WineXpModel.bankedXp(0, 50), DELTA);
	}

	@Test
	public void bankedXpIsFullAboveNoFailLevel()
	{
		assertEquals(2000.0, WineXpModel.bankedXp(10, 70), DELTA);
	}

	@Test
	public void bankedXpIsFailAdjustedBelowNoFailLevel()
	{
		double expected = 10 * 200 * WineFailModel.successRate(35);
		assertEquals(expected, WineXpModel.bankedXp(10, 35), DELTA);
		assertTrue(WineXpModel.bankedXp(10, 35) < 2000.0);
	}

	@Test
	public void projectionStaysInBracketForSmallGain()
	{
		int xp50 = Experience.getXpForLevel(50);
		int bracket = Experience.getXpForLevel(51) - xp50;
		LevelProjection projection = WineXpModel.project(xp50, bracket / 2.0);
		assertEquals(50, projection.getCurrentLevel());
		assertEquals(50, projection.getProjectedLevel());
		assertEquals(0.5, projection.getFraction(), 1e-3);
		assertFalse(projection.isLevelUp());
	}

	@Test
	public void projectionRollsToNextBracketOnThresholdCross()
	{
		int xp50 = Experience.getXpForLevel(50);
		double banked = Experience.getXpForLevel(51) - xp50;
		LevelProjection projection = WineXpModel.project(xp50, banked);
		assertEquals(50, projection.getCurrentLevel());
		assertEquals(51, projection.getProjectedLevel());
		assertTrue(projection.isLevelUp());
	}

	@Test
	public void projectionHandlesMultiLevelJump()
	{
		int xp50 = Experience.getXpForLevel(50);
		double banked = Experience.getXpForLevel(60) - xp50;
		LevelProjection projection = WineXpModel.project(xp50, banked);
		assertEquals(60, projection.getProjectedLevel());
	}
}
