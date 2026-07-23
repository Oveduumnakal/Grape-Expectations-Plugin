/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Verifies the per-wine and total GE margins, including the loss (negative) case. */
public class WineCostModelTest
{
	@Test
	public void marginIsWinePriceMinusInputs()
	{
		assertEquals(150, WineCostModel.marginPerWine(30, 20, 200));
	}

	@Test
	public void marginIsNegativeWhenInputsExceedWine()
	{
		assertTrue(WineCostModel.marginPerWine(200, 50, 100) < 0);
		assertEquals(-150, WineCostModel.marginPerWine(200, 50, 100));
	}

	@Test
	public void totalScalesWithCount()
	{
		assertEquals(1500L, WineCostModel.totalMargin(10, 30, 20, 200));
		assertEquals(0L, WineCostModel.totalMargin(0, 30, 20, 200));
	}

	@Test
	public void totalStaysExactForLargeCounts()
	{
		assertEquals(50_000_000L, WineCostModel.totalMargin(1_000_000, 0, 0, 50));
	}
}
