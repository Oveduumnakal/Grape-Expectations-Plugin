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
		assertEquals(0.0, WineXpModel.bankedXp(WineType.REGULAR, 0, 50), DELTA);
	}

	@Test
	public void bankedXpIsFullAboveNoFailLevel()
	{
		assertEquals(2000.0, WineXpModel.bankedXp(WineType.REGULAR, 10, 70), DELTA);
	}

	@Test
	public void bankedXpIsFailAdjustedBelowNoFailLevel()
	{
		double expected = 10 * 200 * WineFailModel.successRate(WineType.REGULAR, 35);
		assertEquals(expected, WineXpModel.bankedXp(WineType.REGULAR, 10, 35), DELTA);
		assertTrue(WineXpModel.bankedXp(WineType.REGULAR, 10, 35) < 2000.0);
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

	@Test
	public void winesToNextLevelUsesFullRateAboveNoFail()
	{
		int xp70 = Experience.getXpForLevel(70);
		int bracket = Experience.getXpForLevel(71) - xp70;
		int expected = (int) Math.ceil(bracket / 200.0);
		assertEquals(expected, WineXpModel.winesToNextLevel(WineType.REGULAR, xp70));
	}

	@Test
	public void winesToLevelIsZeroAtOrPastTarget()
	{
		assertEquals(0, WineXpModel.winesToLevel(WineType.REGULAR, Experience.getXpForLevel(80), 75));
		assertEquals(0, WineXpModel.winesToLevel(WineType.REGULAR, Experience.getXpForLevel(75), 75));
	}

	@Test
	public void winesToLevelAboveNoFailMatchesFlatRate()
	{
		int xp68 = Experience.getXpForLevel(68);
		int bracket = Experience.getXpForLevel(70) - xp68;
		int expected = (int) Math.ceil(bracket / 200.0);
		assertEquals(expected, WineXpModel.winesToLevel(WineType.REGULAR, xp68, 70));
	}

	@Test
	public void winesToLevelBelowNoFailExceedsFlatRate()
	{
		int xp40 = Experience.getXpForLevel(40);
		int flat = (int) Math.ceil((Experience.getXpForLevel(45) - xp40) / 200.0);
		assertTrue(WineXpModel.winesToLevel(WineType.REGULAR, xp40, 45) > flat);
	}

	@Test
	public void winesTo99FromLowLevelIntegratesBrackets()
	{
		int xp35 = Experience.getXpForLevel(35);
		int flat = (int) Math.ceil((Experience.getXpForLevel(99) - xp35) / 200.0);
		int actual = WineXpModel.winesToLevel(WineType.REGULAR, xp35, 99);
		assertTrue(actual > flat);
		assertTrue(actual < 2 * flat);
	}

	@Test
	public void zamorakBankedXpIsFailAdjustedBelow98()
	{
		double expected = 10 * 200 * WineFailModel.successRate(WineType.ZAMORAK, 80);
		assertEquals(expected, WineXpModel.bankedXp(WineType.ZAMORAK, 10, 80), DELTA);
		assertEquals(2000.0, WineXpModel.bankedXp(WineType.ZAMORAK, 10, 98), DELTA);
	}

	@Test
	public void zamorakNeedsMoreWinesThanRegularBetween68And98()
	{
		int xp70 = Experience.getXpForLevel(70);
		int zamorak = WineXpModel.winesToNextLevel(WineType.ZAMORAK, xp70);
		assertTrue(zamorak > WineXpModel.winesToNextLevel(WineType.REGULAR, xp70));
	}

	@Test
	public void zamorakBelowMinLevelStaysFinite()
	{
		int xp10 = Experience.getXpForLevel(10);
		int wines = WineXpModel.winesToNextLevel(WineType.ZAMORAK, xp10);
		assertTrue(wines > 0 && wines < 1000);
	}
}
