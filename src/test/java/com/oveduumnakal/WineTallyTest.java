/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Verifies the immutable count snapshot and its fermenting-state derivation. */
public class WineTallyTest
{
	@Test
	public void emptyHasZeroCountsAndIsNotFermenting()
	{
		assertEquals(0, WineTally.EMPTY.getGrapes());
		assertEquals(0, WineTally.EMPTY.getJugsOfWater());
		assertEquals(0, WineTally.EMPTY.getUnfermentedWine());
		assertEquals(0, WineTally.EMPTY.getJugsOfWine());
		assertFalse(WineTally.EMPTY.isFermenting());
	}

	@Test
	public void exposesEachCount()
	{
		WineTally tally = new WineTally(5, 3, 10, 2);
		assertEquals(5, tally.getGrapes());
		assertEquals(3, tally.getJugsOfWater());
		assertEquals(10, tally.getUnfermentedWine());
		assertEquals(2, tally.getJugsOfWine());
	}

	@Test
	public void isFermentingReflectsUnfermentedWine()
	{
		assertTrue(new WineTally(0, 0, 1, 0).isFermenting());
		assertFalse(new WineTally(9, 9, 0, 9).isFermenting());
	}

	@Test
	public void equalTalliesAreEqual()
	{
		assertEquals(new WineTally(1, 2, 3, 4), new WineTally(1, 2, 3, 4));
	}
}
