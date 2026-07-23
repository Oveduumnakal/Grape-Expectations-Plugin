/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Verifies the reset-on-new-wine, countdown, expiry, and clear behaviour of the ferment timer. */
public class FermentTimerTest
{
	private static final double DELTA = 1e-6;

	@Test
	public void startsInactive()
	{
		FermentTimer timer = new FermentTimer();
		assertFalse(timer.isActive());
		assertEquals(0, timer.remainingTicks(0));
		assertEquals(0.0, timer.fraction(0), DELTA);
	}

	@Test
	public void resetStartsFullWindow()
	{
		FermentTimer timer = new FermentTimer();
		timer.reset(0);
		assertTrue(timer.isActive());
		assertEquals(FermentTimer.DURATION_TICKS, timer.remainingTicks(0));
		assertEquals(1.0, timer.fraction(0), DELTA);
		assertEquals(13.2, timer.remainingSeconds(0), 1e-6);
	}

	@Test
	public void countsDownAndClampsAtZero()
	{
		FermentTimer timer = new FermentTimer();
		timer.reset(0);
		assertEquals(12, timer.remainingTicks(10));
		assertEquals(0.5, timer.fraction(11), DELTA);
		assertEquals(0, timer.remainingTicks(22));
		assertEquals(0, timer.remainingTicks(30));
		assertEquals(0.0, timer.fraction(30), DELTA);
	}

	@Test
	public void resetRestartsFromNewTick()
	{
		FermentTimer timer = new FermentTimer();
		timer.reset(0);
		timer.reset(5);
		assertEquals(FermentTimer.DURATION_TICKS, timer.remainingTicks(5));
		assertEquals(0, timer.remainingTicks(27));
	}

	@Test
	public void clearStopsCountdown()
	{
		FermentTimer timer = new FermentTimer();
		timer.reset(0);
		timer.clear();
		assertFalse(timer.isActive());
		assertEquals(0, timer.remainingTicks(1));
		assertEquals(0.0, timer.fraction(1), DELTA);
	}
}
