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
		assertEquals(1.0, WineFailModel.successRate(68), DELTA);
		assertEquals(1.0, WineFailModel.successRate(80), DELTA);
		assertEquals(1.0, WineFailModel.successRate(99), DELTA);
	}

	@Test
	public void matchesLowAnchorAtLevelOne()
	{
		assertEquals(0.19, WineFailModel.successRate(1), DELTA);
	}

	@Test
	public void matchesKnownRateAtMinLevel()
	{
		assertEquals(0.602857, WineFailModel.successRate(35), 1e-4);
	}

	@Test
	public void increasesMonotonicallyUpToNoFail()
	{
		for (int level = WineFailModel.MIN_LEVEL; level < WineFailModel.NO_FAIL_LEVEL; level++)
			assertTrue(WineFailModel.successRate(level) < WineFailModel.successRate(level + 1));
	}

	@Test
	public void staysWithinUnitRange()
	{
		for (int level = 1; level <= 99; level++)
		{
			double rate = WineFailModel.successRate(level);
			assertTrue(rate >= 0.0 && rate <= 1.0);
		}
	}
}
