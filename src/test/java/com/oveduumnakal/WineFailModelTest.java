/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Verifies the Mod Ash 19%–138% success-rate interpolation and its no-fail cutoff. */
public class WineFailModelTest
{
	private static final double DELTA = 1e-6;

	@Test
	public void guaranteedAtOrAboveNoFailLevel()
	{
		assertEquals(1.0, WineFailModel.successRate(WineType.REGULAR, 68), DELTA);
		assertEquals(1.0, WineFailModel.successRate(WineType.REGULAR, 80), DELTA);
		assertEquals(1.0, WineFailModel.successRate(WineType.REGULAR, 99), DELTA);
	}

	@Test
	public void matchesLowAnchorAtLevelOne()
	{
		assertEquals(0.19, WineFailModel.successRate(WineType.REGULAR, 1), DELTA);
	}

	@Test
	public void matchesKnownRateAtMinLevel()
	{
		assertEquals(0.602857, WineFailModel.successRate(WineType.REGULAR, 35), 1e-4);
	}

	@Test
	public void zamorakMatchesKnownRateAtMinLevel()
	{
		assertEquals(0.494, WineFailModel.successRate(WineType.ZAMORAK, 65), 1e-3);
	}

	@Test
	public void zamorakFailsUntilLevel98()
	{
		assertTrue(WineFailModel.successRate(WineType.ZAMORAK, 97) < 1.0);
		assertEquals(1.0, WineFailModel.successRate(WineType.ZAMORAK, 98), DELTA);
	}

	@Test
	public void increasesMonotonicallyUpToNoFail()
	{
		for (WineType type : WineType.values())
		{
			for (int level = type.getMinLevel(); level < type.getNoFailLevel(); level++)
				assertTrue(WineFailModel.successRate(type, level) < WineFailModel.successRate(type, level + 1));
		}
	}

	@Test
	public void reachesCertaintyExactlyAtNoFailLevel()
	{
		for (WineType type : WineType.values())
		{
			assertTrue(WineFailModel.successRate(type, type.getNoFailLevel() - 1) < 1.0);
			assertEquals(1.0, WineFailModel.successRate(type, type.getNoFailLevel()), DELTA);
		}
	}

	@Test
	public void staysWithinUnitRange()
	{
		for (WineType type : WineType.values())
		{
			for (int level = 1; level <= 99; level++)
			{
				double rate = WineFailModel.successRate(type, level);
				assertTrue(rate >= 0.0 && rate <= 1.0);
			}
		}
	}
}
