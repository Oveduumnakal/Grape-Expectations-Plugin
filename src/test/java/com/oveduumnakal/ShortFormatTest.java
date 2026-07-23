/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Verifies the compact number formatting used across the overlay. */
public class ShortFormatTest
{
	@Test
	public void belowThousandIsGroupedDigits()
	{
		assertEquals("0", ShortFormat.value(0));
		assertEquals("28", ShortFormat.value(28));
		assertEquals("999", ShortFormat.value(999));
	}

	@Test
	public void thousandsAbbreviateAndDropTrailingZeros()
	{
		assertEquals("1K", ShortFormat.value(1_000));
		assertEquals("1.7K", ShortFormat.value(1_700));
		assertEquals("12.3K", ShortFormat.value(12_345));
		assertEquals("234K", ShortFormat.value(234_000));
	}

	@Test
	public void millionsAndBillions()
	{
		assertEquals("1.5M", ShortFormat.value(1_500_000));
		assertEquals("13M", ShortFormat.value(13_034_431));
		assertEquals("2.1B", ShortFormat.value(2_100_000_000L));
	}

	@Test
	public void negativesKeepLeadingSign()
	{
		assertEquals("-1.7K", ShortFormat.value(-1_700));
	}
}
