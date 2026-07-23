/*
 * Copyright (c) 2026, Oveduumnakal
 * All rights reserved.
 */
package com.oveduumnakal;

/**
 * How the banked-XP row values the fermenting batch: {@link #EXPECTED} weights each wine by
 * its Cooking-level success chance (the realistic expected XP), while {@link #OPTIMISTIC}
 * assumes every wine succeeds (as if at or above the level-68 no-fail threshold). The
 * {@code label} is the name shown in the config dropdown.
 */
public enum XpMode
{
	EXPECTED("Expected (fail-adjusted)"),
	OPTIMISTIC("Optimistic (no-fail)");

	private final String label;

	XpMode(String label)
	{
		this.label = label;
	}

	@Override
	public String toString()
	{
		return label;
	}
}
